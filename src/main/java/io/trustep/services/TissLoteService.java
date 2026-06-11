package io.trustep.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.trustep.drools.models.Procedimento;
import io.trustep.dto.sadt.DadosSolicitacaoProcedimentoDTO;
import io.trustep.dto.sadt.GuiaRequestXML;
import io.trustep.dto.sadt.GuiaSpSadtDTO;
import io.trustep.entities.GuiaEntity;
import io.trustep.entities.LoteEntity;
import io.trustep.repositories.GuiaRepository;
import io.trustep.repositories.LoteRepository;
import io.trustep.utils.DroolsRulesProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


@ApplicationScoped
@RequiredArgsConstructor
public class TissLoteService {


    private final DroolsRulesProcessor rulesProcessor;
    private final LoteRepository loteRepository;
    private final GuiaRepository guiaRepository;

    private static final String CNPJ_PRESTADOR  = "12345678000199";
    private static final String NOME_PRESTADOR  = "COOPERATIVA ANESTESIA LTDA";
    private static final String VERSAO_TISS     = "3.05.00";
    private static final int    MAX_GUIAS_LOTE  = 100; // limite do padrão ANS

    // =========================================================================
    // PONTO DE ENTRADA PÚBLICO
    // Recebe a lista de guias, valida o limite, e orquestra a geração do XML.
    // Retorna o XML como String pronto para ser salvo ou devolvido ao cliente.
    // =========================================================================
    @Transactional
    public String gerarLoteXml(List<GuiaSpSadtDTO> guias) {

        // --- ETAPA 1: Validação do limite de guias por lote ---
        // O padrão TISS define que um único arquivo XML não pode conter
        // mais de 100 guias. Se o cliente enviar mais, rejeitamos antes
        // de processar qualquer coisa — evita processar parcialmente e
        // gerar um XML inválido silenciosamente.
        if (guias == null || guias.isEmpty()) {
            throw new IllegalArgumentException("Lista de guias não pode ser vazia.");
        }
        if (guias.size() > MAX_GUIAS_LOTE) {
            throw new IllegalArgumentException(
                    "Lote excede o limite de " + MAX_GUIAS_LOTE +
                            " guias por arquivo TISS. Enviado: " + guias.size()
            );
        }

        // --- ETAPA 2: Geração do número de lote e timestamps ---
        // O número do lote é um identificador único desse envio.
        // Usamos UUID truncado para garantir unicidade sem depender
        // de sequência do banco nesse momento da PoC.
        // Em produção, esse número deve ser sequencial e persistido
        // para rastreabilidade e reenvio em caso de rejeição.
        String numeroLote   = gerarNumeroLote();
        String dataGeracao  = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String horaGeracao  = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        // --- ETAPA 3: Processamento de cada guia ---
        // Para cada guia da lista:
        //   a) aplica as regras do Drools (desconto/acréscimo por operadora)
        //   b) calcula o valor total apurado da guia (soma dos procedimentos)
        //   c) gera o bloco XML da guia
        // O AtomicInteger garante sequencial correto mesmo se futuramente
        // isso rodar em paralelo.
        AtomicInteger sequencia = new AtomicInteger(1);
        StringBuilder blocoGuias = new StringBuilder();
        BigDecimal valorTotalLote = BigDecimal.ZERO;

        for (GuiaSpSadtDTO guia : guias) {
            // Aplica regras Drools e calcula o total da guia
            BigDecimal valorTotalGuia = calcularValorGuia(guia);
            valorTotalLote = valorTotalLote.add(valorTotalGuia);

            // Gera o XML da guia e acumula no StringBuilder
            blocoGuias.append(gerarBlocoGuia(guia, sequencia.getAndIncrement(), valorTotalGuia));
        }

        // --- PERSISTÊNCIA: salva o lote e as guias no banco ---
        persistirLoteEGuias(numeroLote, guias, valorTotalLote,
                guias.get(0).registroANSOperadora, calcularHashEpilogo(blocoGuias.toString()));

        // --- ETAPA 4: Cálculo do hash do epilogo ---
        // O padrão TISS exige um hash MD5 no epilogo que representa
        // a "assinatura" do conteúdo do lote para detectar adulteração.
        // Por ora geramos um placeholder — em produção deve ser o MD5
        // real do conteúdo entre <prestadorParaOperadora> e </prestadorParaOperadora>.
        String hash = calcularHashEpilogo(blocoGuias.toString());

        // --- ETAPA 5: Montagem da mensagem TISS completa ---
        // A mensagem TISS tem sempre 3 seções obrigatórias:
        //   <cabecalho>              — identifica quem envia, para quem e quando
        //   <prestadorParaOperadora> — contém o lote com as guias
        //   <epilogo>                — hash de integridade
        return montarMensagemTiss(
                numeroLote, dataGeracao, horaGeracao,
                guias.get(0).registroANSOperadora,  // todas as guias do lote são da mesma operadora
                blocoGuias.toString(),
                valorTotalLote,
                hash
        );
    }

    public String gerarLoteXml(String request) {
        try {
            XmlMapper xmlMapper = new XmlMapper();
//            xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            GuiaRequestXML wrapper = xmlMapper.readValue(request, GuiaRequestXML.class);
            return gerarLoteXml(wrapper.guias);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Erro ao processar XML de entrada: " + e.getMessage(), e);
        }
    }

    // =========================================================================
    // ETAPA 3a: APLICA DROOLS E CALCULA VALOR TOTAL DA GUIA
    // Itera sobre os procedimentos da guia, aplica as regras de contrato
    // via Drools em cada um, e retorna a soma dos valores apurados.
    // O valorApurado de cada ProcedimentoItemDTO é atualizado in-place
    // para que o gerador de XML use o valor já calculado.
    // =========================================================================
    private BigDecimal calcularValorGuia(GuiaSpSadtDTO guia) {
        BigDecimal total = BigDecimal.ZERO;

        for (DadosSolicitacaoProcedimentoDTO item : guia.procedimentos) {

            // Monta o objeto de domínio que o Drools conhece
            Procedimento proc = new Procedimento();
            proc.setOperadora(guia.operadora);
            proc.setTipo(guia.tipoProcedimento);
            proc.setValorBase(item.valorUnitario.multiply(BigDecimal.valueOf(item.quantidade)));

            if (guia.executante != null) {
                proc.setCpfExecutante(guia.executante.cpfExecutante);
                proc.setNomeExecutante(guia.executante.nomeExecutante);
            }

            // Drools aplica as regras do .drl e preenche valorTotal
            rulesProcessor.processarRegras(Collections.singletonList(proc));
//            droolsService.aplicarRegras(proc);

            // Devolve o valor calculado para o item — será usado no XML
            final var valorCalculadoProcedimento = Optional.ofNullable(proc)
                    .map(p -> p.getValorApurado() != null ? p.getValorApurado() : p.getValorBase())
                    .orElse(BigDecimal.ZERO);
            item.setValorTotal(valorCalculadoProcedimento);
            total = total.add(item.getValorTotal());
        }

        return total;
    }

    // =========================================================================
    // ETAPA 3b: GERA O BLOCO XML DE UMA GUIA SP/SADT
    // Cada guia no lote é um elemento <guiaSPSADT> com 5 sub-blocos:
    //   1. cabecalhoGuia      — identificação da guia e autorização
    //   2. dadosBeneficiario  — quem foi atendido
    //   3. dadosSolicitante   — quem pediu (ex: o obstetra)
    //   4. dadosSolicitacao   — os procedimentos realizados
    //   5. dadosExecutante    — quem executou (o anestesista, grau 13)
    // A sequência numérica é o número da guia dentro do lote (1..N).
    // =========================================================================
    private String gerarBlocoGuia(GuiaSpSadtDTO guiaSpSadt, int seq, BigDecimal valorTotal) {
        return """
            <guiaSPSADT>

              <!-- BLOCO 1: Cabeçalho da guia -->
              <!-- Identifica a guia para a operadora.
                   numeroGuiaPrincipal é o número dado pela operadora na autorização prévia.
                   Sem ele, a guia pode ser rejeitada ou tratada como não autorizada. -->
              <cabecalhoGuia>
                <registroANS>%s</registroANS>
                <numeroGuiaPrestador>%s</numeroGuiaPrestador>
                <numeroGuiaPrincipal>%s</numeroGuiaPrincipal>
                <dataAutorizacao>%s</dataAutorizacao>
                <senhaAutorizacao>%s</senhaAutorizacao>
                <dataValidadeSenha>%s</dataValidadeSenha>
                <numeroGuiaOperadora>%05d</numeroGuiaOperadora>
              </cabecalhoGuia>

              <!-- BLOCO 2: Dados do beneficiário -->
              <!-- numeroCarteira deve ter dígito verificador válido no TISS 3.05.
                   atendimentoRN ("S"/"N") indica se é recém-nascido — impacta
                   a regra de elegibilidade da operadora. -->
              <dadosBeneficiario>
                <numeroCarteira>%s</numeroCarteira>
                <atendimentoRN>%s</atendimentoRN>
                <nomeBeneficiario>%s</nomeBeneficiario>
                <numeroCNS>%s</numeroCNS>
                <dataValidadeCarteira>%s</dataValidadeCarteira>
              </dadosBeneficiario>

              <!-- BLOCO 3: Dados do solicitante -->
              <!-- Quem pediu o procedimento — normalmente o médico que
                   solicitou a anestesia (ex: o obstetra no parto).
                   cbos é o código da ocupação (CBO do Ministério do Trabalho). -->
              <dadosSolicitante>
                <codigoOperadora>%s</codigoOperadora>
                <nomeSolicitante>%s</nomeSolicitante>
                <conselhoProfissional>%s</conselhoProfissional>
                <numeroConselho>%s</numeroConselho>
                <ufConselho>%s</ufConselho>
                <cbos>%s</cbos>
              </dadosSolicitante>

              <!-- BLOCO 4: Dados da solicitação -->
              <!-- caraterAtendimento: "01"=eletivo, "02"=urgência.
                   tipoAtendimento: "05"=internação (parto é internação).
                   indicacaoAcidente: "0"=não é acidente — impacta cobertura. -->
              <dadosSolicitacao>
                <caraterAtendimento>%s</caraterAtendimento>
                <tipoAtendimento>%s</tipoAtendimento>
                <indicacaoAcidente>%s</indicacaoAcidente>
                <procedimentosRealizados>
                  %s
                </procedimentosRealizados>
                <valorTotal>%.2f</valorTotal>
              </dadosSolicitacao>

              <!-- BLOCO 5: Dados do executante -->
              <!-- grauParticipacao "13" = anestesista.
                   Sem esse campo correto, o pagamento pode ir para
                   o cirurgião ao invés do anestesista. É o campo mais
                   crítico para a cooperativa. -->
              <dadosExecutante>
                <codigoOperadora>%s</codigoOperadora>
                <cpfContratado>%s</cpfContratado>
                <nomeContratado>%s</nomeContratado>
                <conselhoProfissional>%s</conselhoProfissional>
                <numeroConselho>%s</numeroConselho>
                <ufConselho>%s</ufConselho>
                <cbos>%s</cbos>
                <grauParticipacao>%s</grauParticipacao>
              </dadosExecutante>

            </guiaSPSADT>
            """.formatted(
                // cabeçalho
                guiaSpSadt.registroANSOperadora,
                guiaSpSadt.numeroGuiaPrestador,
                nvl(guiaSpSadt.numeroGuiaPrincipal, ""),
                nvl(guiaSpSadt.dataAutorizacao, ""),
                nvl(guiaSpSadt.senhaAutorizacao, ""),
                nvl(guiaSpSadt.dataValidadeSenha, ""),
                seq,
                // beneficiário
                guiaSpSadt.beneficiario.numeroCarteira,
                guiaSpSadt.beneficiario.atendimentoRN,
                guiaSpSadt.beneficiario.nomeBeneficiario,
                nvl(guiaSpSadt.beneficiario.numeroCNS, ""),
                nvl(guiaSpSadt.beneficiario.dataValidadeCarteira, ""),
                // solicitante
                guiaSpSadt.solicitante.codigoOperadoraSolicitante,
                guiaSpSadt.solicitante.nomeSolicitante,
                guiaSpSadt.solicitante.conselhoProfissional,
                guiaSpSadt.solicitante.numeroConselho,
                guiaSpSadt.solicitante.ufConselho,
                guiaSpSadt.solicitante.cbos,
                // solicitação
                guiaSpSadt.caraterAtendimento,
                guiaSpSadt.tipoAtendimento,
                guiaSpSadt.indicacaoAcidente,
                gerarProcedimentos(guiaSpSadt.procedimentos),
                valorTotal,
                // executante
                guiaSpSadt.executante.codigoOperadoraExecutante,
                guiaSpSadt.executante.cpfExecutante,
                guiaSpSadt.executante.nomeExecutante,
                guiaSpSadt.executante.conselhoProfissional,
                guiaSpSadt.executante.numeroConselho,
                guiaSpSadt.executante.ufConselho,
                guiaSpSadt.executante.cbos,
                guiaSpSadt.executante.grauParticipacao
        );
    }

    // =========================================================================
    // ETAPA 3c: GERA OS ITENS DE PROCEDIMENTO DENTRO DA GUIA
    // Cada item é um procedimento realizado naquele atendimento.
    // Uma guia de anestesia para parto pode ter mais de um item:
    // ex: a anestesia em si + monitoração + sedação complementar.
    // codigoTabela "22" = tabela TUSS (padrão ANS obrigatório desde 3.05).
    // valorApurado já foi preenchido pelo Drools na etapa 3a.
    // =========================================================================
    private String gerarProcedimentos(List<DadosSolicitacaoProcedimentoDTO> itens) {
        StringBuilder sb = new StringBuilder();

        for (DadosSolicitacaoProcedimentoDTO item : itens) {
            sb.append("""
                <procedimento>
                  <dataRealizacao>%s</dataRealizacao>
                  <horaInicial>%s</horaInicial>
                  <horaFinal>%s</horaFinal>
                  <codigoTabela>%s</codigoTabela>
                  <codigoProcedimento>%s</codigoProcedimento>
                  <descricaoProcedimento>%s</descricaoProcedimento>
                  <quantidade>%d</quantidade>
                  <valorUnitario>%.2f</valorUnitario>
                  <valorApurado>%.2f</valorApurado>
                  <reducaoAcrescimo>%s</reducaoAcrescimo>
                </procedimento>
                """.formatted(
                    item.dataRealizacao,
                    nvl(item.horaInicial, ""),
                    nvl(item.horaFinal, ""),
                    item.codigoTabela,
                    item.codigoProcedimento,
                    item.descricaoProcedimento,
                    item.quantidade,
                    item.valorUnitario,
                    item.valorTotal,
                    nvl(item.reducaoAcrescimo, "00")
            ));
        }

        return sb.toString();
    }

    // =========================================================================
    // ETAPA 4: HASH DO EPILOGO
    // O padrão TISS exige MD5 do conteúdo do lote no epilogo.
    // Operadoras usam esse hash para detectar se o XML foi adulterado
    // após a geração. Aqui calculamos o MD5 real do bloco de guias.
    // Se o hash não bater, a operadora rejeita o lote inteiro.
    // =========================================================================
    private String calcularHashEpilogo(String conteudo) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(conteudo.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            // MD5 está disponível em qualquer JVM — esse catch é só por contrato
            throw new RuntimeException("Falha ao calcular hash MD5 do epilogo", e);
        }
    }

    // =========================================================================
    // ETAPA 5: MONTAGEM FINAL DA MENSAGEM TISS
    // Envolve tudo nas 3 seções obrigatórias do padrão:
    //   <cabecalho>              — envelope com remetente, destinatário e versão
    //   <prestadorParaOperadora> — o lote com as guias
    //   <epilogo>                — hash de integridade
    // O namespace xmlns é obrigatório — sem ele o XSD rejeita na validação.
    // tipoTransacao "ENVIO_LOTE_GUIAS" é o valor fixo para esse tipo de envio.
    // =========================================================================
    private String montarMensagemTiss(
            String numeroLote, String dataGeracao, String horaGeracao,
            String registroANSOperadora, String blocoGuias,
            BigDecimal valorTotalLote, String hash) {

        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <mensagemTISS
              xmlns="http://www.ans.gov.br/padroes/tiss/schemas"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://www.ans.gov.br/padroes/tiss/schemas
                https://www.ans.gov.br/images/stories/Prestadores/schemas/3-05-00/tissV3_05_00.xsd">

              <!-- SEÇÃO 1: Cabeçalho da mensagem -->
              <!-- Identifica a transação, quem envia (prestador) e quem recebe (operadora).
                   sequencialTransacao deve ser incrementado a cada envio para a mesma operadora —
                   serve para a operadora detectar se perdeu algum lote. -->
              <cabecalho>
                <identificacaoTransacao>
                  <tipoTransacao>ENVIO_LOTE_GUIAS</tipoTransacao>
                  <sequencialTransacao>%s</sequencialTransacao>
                  <dataRegistroTransacao>%s</dataRegistroTransacao>
                  <horaRegistroTransacao>%s</horaRegistroTransacao>
                </identificacaoTransacao>
                <origem>
                  <identificacaoPrestador>
                    <cnpjContratado>%s</cnpjContratado>
                  </identificacaoPrestador>
                </origem>
                <destino>
                  <registroANS>%s</registroANS>
                </destino>
                <versaoTISS>%s</versaoTISS>
              </cabecalho>

              <!-- SEÇÃO 2: Corpo — lote com as guias -->
              <!-- numeroLote identifica esse conjunto de guias.
                   valorTotalLote é a soma de todos os valorTotal das guias —
                   a operadora confere esse total como primeiro filtro de integridade. -->
              <prestadorParaOperadora>
                <loteGuias>
                  <numeroLote>%s</numeroLote>
                  <guias>
                    %s
                  </guias>
                  <valorTotalLote>%.2f</valorTotalLote>
                </loteGuias>
              </prestadorParaOperadora>

              <!-- SEÇÃO 3: Epilogo -->
              <!-- hash MD5 do conteúdo do lote.
                   Se qualquer caractere do lote for alterado após a geração,
                   o hash muda e a operadora rejeita o arquivo. -->
              <epilogo>
                <hash>%s</hash>
              </epilogo>

            </mensagemTISS>
            """.formatted(
                numeroLote,       // sequencialTransacao (reuso do número de lote na PoC)
                dataGeracao,
                horaGeracao,
                CNPJ_PRESTADOR,
                registroANSOperadora,
                VERSAO_TISS,
                numeroLote,
                blocoGuias,
                valorTotalLote,
                hash
        );
    }

    // =========================================================================
    // UTILITÁRIOS
    // =========================================================================

    // Gera número de lote único truncado — em produção usar sequencial do banco
    private String gerarNumeroLote() {
        return UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }

    // Null-safe: evita "null" literal no XML quando campo opcional não foi informado
    private String nvl(String valor, String fallback) {
        return valor != null && !valor.isBlank() ? valor : fallback;
    }

    // =========================================================================
    // PERSISTÊNCIA: salva o lote e as guias processadas no banco de dados.
    // Chamado após a aplicação das regras Drools e cálculo dos valores.
    // =========================================================================
    private void persistirLoteEGuias(
            String numeroLote,
            List<GuiaSpSadtDTO> guias,
            BigDecimal valorTotalLote,
            String registroANSOperadora,
            String hashEpilogo) {

        // 1. Cria e persiste o lote
        LoteEntity loteEntity = LoteEntity.builder()
                .numeroLote(numeroLote)
                .registroANSOperadora(registroANSOperadora)
                .valorTotalLote(valorTotalLote)
                .hashEpilogo(hashEpilogo)
                .build();
        loteRepository.salvar(loteEntity);

        // 2. Cria e persiste cada guia vinculada ao lote
        int seq = 1;
        for (GuiaSpSadtDTO guia : guias) {
            BigDecimal valorGuia = calcularValorGuia(guia);

            GuiaEntity guiaEntity = GuiaEntity.builder()
                    .lote(loteEntity)
                    .numeroGuiaPrestador(guia.numeroGuiaPrestador)
                    .numeroGuiaPrincipal(guia.numeroGuiaPrincipal)
                    .registroANSOperadora(guia.registroANSOperadora)
                    .dataAutorizacao(guia.dataAutorizacao)
                    .senhaAutorizacao(guia.senhaAutorizacao)
                    .numeroCarteiraBeneficiario(guia.beneficiario != null ? guia.beneficiario.numeroCarteira : null)
                    .nomeBeneficiario(guia.beneficiario != null ? guia.beneficiario.nomeBeneficiario : null)
                    .cpfExecutante(guia.executante != null ? guia.executante.cpfExecutante : null)
                    .nomeExecutante(guia.executante != null ? guia.executante.nomeExecutante : null)
                    .grauParticipacao(guia.executante != null ? guia.executante.grauParticipacao : null)
                    .caraterAtendimento(guia.caraterAtendimento)
                    .tipoAtendimento(guia.tipoAtendimento)
                    .operadora(guia.operadora)
                    .tipoProcedimento(guia.tipoProcedimento)
                    .valorTotalGuia(valorGuia)
                    .sequenciaNoLote(seq++)
                    .build();
            guiaRepository.salvar(guiaEntity);
        }
    }
}
