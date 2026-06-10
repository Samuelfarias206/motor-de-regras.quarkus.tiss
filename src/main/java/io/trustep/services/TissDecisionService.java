package io.trustep.services;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.trustep.dto.Autorizacao;
import io.trustep.dto.ContaFato;
import io.trustep.dto.Elegibilidade;
import io.trustep.dto.Glosa;
import io.trustep.input.AnexosInput;
import io.trustep.input.ContaRequest;
import io.trustep.input.ProcedimentoRealizado;
import io.trustep.input.TissInput;
import io.trustep.response.AnexosResponse;
import io.trustep.response.ContaResponse;
import io.trustep.response.DemonstrativoResponse;
import io.trustep.response.DocumentacaoResponse;
import io.trustep.response.FinanceiroResponse;
import io.trustep.response.PagamentoResponse;
import io.trustep.response.ProtocoloResponse;
import io.trustep.store.ProtocoloState;
import io.trustep.store.ProtocoloStore;
import io.trustep.utils.DroolsRulesProcessor;
import io.trustep.utils.TransformerUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Serviço principal do fluxo TISS.
 * <p>
 * Cada método corresponde a uma etapa do spec.md.
 * O estado entre etapas é mantido pelo {@link ProtocoloStore} (in-memory).
 * As regras de negócio da Etapa 4 são executadas pelo motor Drools via {@link KieBase}.
 * </p>
 */
@ApplicationScoped
@RequiredArgsConstructor
public class TissDecisionService {

    /** Motor de regras Drools — ApplicationScoped (singleton, compilado uma vez). */
    private final KieBase kieBase;

    /** Store em memória que correlaciona protocolo ↔ estado do fluxo. */
    private final ProtocoloStore protocoloStore;

    @Inject
    DroolsRulesProcessor droolsRulesProcessor;

    /**
     * Documentos obrigatórios por tipo de guia — spec §3 (tabela DMN).
     * <p>
     * Num sistema real, esta tabela viria de um arquivo DMN ou banco de dados.
     * Aqui é implementada como Map constante para fidelidade à spec sem
     * introduzir complexidade desnecessária.
     * </p>
     */
    private static final Map<String, List<String>> DOCS_OBRIGATORIOS = Map.of(
            "INTERNACAO", List.of("RELATORIO_MEDICO"),
            "OPME",       List.of("NOTA_FISCAL", "REGISTRO_ANVISA"),
            "SADT",       List.of("SOLICITACAO_MEDICA")
    );

    // ─────────────────────────────────────────────────────────────────────────
    // Etapa 1 – Receber Guia (spec §1 — loteGuias)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Valida campos obrigatórios, registra protocolo e persiste o estado inicial.
     * <p>
     * Regras aplicadas:
     * <ul>
     *   <li>O campo {@code sequencialReferencia} deve ser único dentro da lista de
     *       procedimentos (padraoTiss.md §Regra de Negócio).</li>
     *   <li>O {@code valorGuia} é calculado automaticamente como a soma dos
     *       {@code valorTotalProcedimento} de cada procedimento.</li>
     * </ul>
     * </p>
     *
     * @throws IllegalArgumentException se {@code sequencialReferencia} não for único.
     */
    public ProtocoloResponse receberGuia(TissInput input) {
        validarSequencialReferencia(input.getProcedimentosRealizados());

        BigDecimal valorGuia = calcularValorTotalGuia(input.getProcedimentosRealizados());

        String numeroProtocolo = protocoloStore.gerarNumeroProtocolo();

        ProtocoloState state = ProtocoloState.builder()
                .numeroGuiaPrestador(input.getNumeroGuiaPrestador())
                .registroANS(input.getRegistroANS())
                .tipoGuia(input.getTipoGuia())
                // derivados dos sub-objetos para retrocompatibilidade com regras Drools
                .codigoPrestador(input.getPrestadorExecutante() != null
                        ? input.getPrestadorExecutante().getCodigoContratadoExecutanteOperadora()
                        : null)
                .codigoBeneficiario(input.getBeneficiario() != null
                        ? input.getBeneficiario().getNumeroCarteiraBeneficiario()
                        : null)
                .dataAtendimento(input.getDadosFaturamento() != null
                        ? input.getDadosFaturamento().getDataInicioFaturamento()
                        : null)
                .valorGuia(valorGuia)
                // sub-objetos completos conforme padrão TISS
                .beneficiario(input.getBeneficiario())
                .prestadorExecutante(input.getPrestadorExecutante())
                .dadosFaturamento(input.getDadosFaturamento())
                .procedimentosRealizados(input.getProcedimentosRealizados() != null
                        ? input.getProcedimentosRealizados()
                        : List.of())
                .status("RECEBIDO")
                .build();

        protocoloStore.salvar(numeroProtocolo, state);

        return ProtocoloResponse.builder()
                .numeroProtocolo(numeroProtocolo)
                .status("RECEBIDO")
                .mensagem("Guia recebida com sucesso")
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Etapa 2 – Receber Anexos (spec §2 — recebimentoAnexo)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Vincula os documentos ao protocolo existente no store e persiste os metadados do lote.
     */
    public AnexosResponse receberAnexos(AnexosInput input) {
        ProtocoloState state = protocoloStore.buscar(input.getNumeroProtocolo());

        // Persistir metadados do lote de anexos
        state.setNumeroLote(input.getNumeroLote());
        state.setDataEnvioLoteAnexos(input.getDataEnvioLoteAnexos());
        state.setCodigoContratadoExecutanteAnexo(input.getCodigoContratadoExecutanteOperadora());

        // Coletar tipos de documentos dos anexos
        List<String> tipos = input.getAnexos() == null ? List.of() :
                input.getAnexos().stream()
                        .map(a -> a.getTipoDocumento())
                        .collect(toList());

        state.getTiposAnexos().addAll(tipos);
        state.setStatus("ANEXOS_RECEBIDOS");
        protocoloStore.salvar(input.getNumeroProtocolo(), state);

        return AnexosResponse.builder()
                .status("ANEXOS_RECEBIDOS")
                .quantidadeAnexos(tipos.size())
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Etapa 3 – Validar Documentação (spec §3 — DMN: Validar Documentos Anexos)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Verifica se todos os documentos obrigatórios foram enviados para o tipo de guia.
     * Implementa a tabela de decisão do spec §3.
     */
    public DocumentacaoResponse validarDocumentacao(String numeroProtocolo) {
        ProtocoloState state = protocoloStore.buscar(numeroProtocolo);

        List<String> docsNecessarios = DOCS_OBRIGATORIOS.getOrDefault(
                state.getTipoGuia(), List.of());

        List<String> pendencias = docsNecessarios.stream()
                .filter(doc -> !state.getTiposAnexos().contains(doc))
                .collect(toList());

        if (pendencias.isEmpty()) {
            state.setStatus("DOCUMENTACAO_VALIDADA");
            protocoloStore.salvar(numeroProtocolo, state);
            return DocumentacaoResponse.builder()
                    .status("DOCUMENTACAO_VALIDADA")
                    .pendencias(List.of())
                    .build();
        } else {
            state.setStatus("PENDENTE_DOCUMENTACAO");
            protocoloStore.salvar(numeroProtocolo, state);
            return DocumentacaoResponse.builder()
                    .status("PENDENTE_DOCUMENTACAO")
                    .pendencias(pendencias)
                    .build();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Etapa 4 – Aprovar Conta (spec §4 — Drools: Elegibilidade, Autorizacao, Glosa)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Executa auditoria técnica e administrativa via regras Drools.
     * Determina se a conta é APROVADA ou GLOSADA e calcula o valorAprovado.
     */
    public ContaResponse aprovarConta(ContaRequest request) {
        ProtocoloState state = protocoloStore.buscar(request.getNumeroProtocolo());

        // ── Objetos de saída (modificados pelas regras Drools) ────────────────
        Elegibilidade elegibilidade = Elegibilidade.builder()
                .elegivel(false)
                .mensagem("Beneficiário não elegível")
                .build();

        Autorizacao autorizacao = Autorizacao.builder()
                .necessitaAutorizacao(false)
                .tipoAutorizacao("")
                .build();

        Glosa glosa = Glosa.builder()
                .statusGlosa("SEM_GLOSA")
                .permiteRecurso(false)
                .valorLiberado(state.getValorGuia())
                .build();

        // ── Fato de entrada (contexto da auditoria) ───────────────────────────
        ContaFato fato = ContaFato.builder()
                .tipoGuia(state.getTipoGuia())
                .valorGuia(state.getValorGuia())
                .atendimentoRN(request.isAtendimentoRN())
                .possuiGlosa(request.isPossuiGlosa())
                .tipoGlosa(request.getTipoGlosa())
                .possuiAutorizacao(request.isPossuiAutorizacao())
                .build();

        // ── Executar sessão Drools (uma por requisição, descartada no finally) ─
        KieSession session = kieBase.newKieSession();
        try {
            session.insert(fato);
            session.insert(elegibilidade);
            session.insert(autorizacao);
            session.insert(glosa);
            session.fireAllRules();
        } finally {
            session.dispose();
        }

        // ── Determinar status da conta com base nos resultados das regras ─────
        String statusConta;
        BigDecimal valorAprovado;
        String tipoGlosa = null;

        if (!elegibilidade.isElegivel()) {
            // Beneficiário inelegível → glosa total
            statusConta = "GLOSADA";
            tipoGlosa = "GLOSA_TOTAL";
            valorAprovado = BigDecimal.ZERO;
        } else if ("GLOSADO".equals(glosa.getStatusGlosa())) {
            statusConta = "GLOSADA";
            tipoGlosa = glosa.getTipoGlosa() != null
                    ? glosa.getTipoGlosa()
                    : "GLOSA_PARCIAL";
            valorAprovado = glosa.getValorLiberado() != null
                    ? glosa.getValorLiberado()
                    : BigDecimal.ZERO;
        } else {
            statusConta = "APROVADA";
            valorAprovado = state.getValorGuia();
        }

        // ── Persistir resultado no estado do protocolo ────────────────────────
        state.setStatusConta(statusConta);
        state.setValorAprovado(valorAprovado);
        state.setTipoGlosa(tipoGlosa);
        state.setStatus(statusConta);
        protocoloStore.salvar(request.getNumeroProtocolo(), state);

        return ContaResponse.builder()
                .statusConta(statusConta)
                .valorAprovado(valorAprovado)
                .tipoGlosa(tipoGlosa)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Etapa 5 – Gerar Demonstrativo (spec §5 — DMN: Determinar Tipo de Demonstrativo)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Determina o tipo de demonstrativo com base no resultado da etapa 4.
     * Gera um PDF mock em Base64 (substituir por lib PDF em produção).
     */
    public DemonstrativoResponse gerarDemonstrativo(String numeroProtocolo) {
        ProtocoloState state = protocoloStore.buscar(numeroProtocolo);

        String tipoDemonstrativo;
        if ("APROVADA".equals(state.getStatusConta())) {
            tipoDemonstrativo = "ODONTO".equals(state.getTipoGuia())
                    ? "DEMONSTRATIVO_PAGAMENTO_ODONTOLOGICO"
                    : "DEMONSTRATIVO_PAGAMENTO_MEDICO";
        } else if ("GLOSADA".equals(state.getStatusConta())) {
            tipoDemonstrativo = "DEMONSTRATIVO_ANALISE_CONTA";
        } else {
            tipoDemonstrativo = "DEMONSTRATIVO_REJEICAO";
        }

        state.setTipoDemonstrativo(tipoDemonstrativo);
        state.setStatus("DEMONSTRATIVO_GERADO");
        protocoloStore.salvar(numeroProtocolo, state);

        // PDF mock em Base64 — em produção integrar iText/PDFBox
        String arquivoPdf = Base64.getEncoder().encodeToString(
                ("PDF|" + tipoDemonstrativo + "|" + numeroProtocolo).getBytes()
        );

        return DemonstrativoResponse.builder()
                .tipoDemonstrativo(tipoDemonstrativo)
                .arquivoPdf(arquivoPdf)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Etapa 6 – Enviar para Financeiro (spec §6)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Gera título financeiro e agenda pagamento.
     */
    public FinanceiroResponse enviarFinanceiro(String numeroProtocolo) {
        ProtocoloState state = protocoloStore.buscar(numeroProtocolo);
        String numeroTitulo = protocoloStore.gerarNumeroTitulo();

        state.setNumeroTitulo(numeroTitulo);
        state.setStatus("AGUARDANDO_PAGAMENTO");
        protocoloStore.salvar(numeroProtocolo, state);

        return FinanceiroResponse.builder()
                .numeroTitulo(numeroTitulo)
                .status("AGUARDANDO_PAGAMENTO")
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Etapa 7 – Pagamento (spec §7)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Efetiva o pagamento ao prestador com o valor aprovado na etapa 4.
     */
    public PagamentoResponse pagamento(String numeroProtocolo) {
        ProtocoloState state = protocoloStore.buscar(numeroProtocolo);

        state.setStatus("PAGO");
        protocoloStore.salvar(numeroProtocolo, state);

        return PagamentoResponse.builder()
                .status("PAGO")
                .valorPago(state.getValorAprovado())
                .dataPagamento(LocalDate.now().toString())
                .build();
    }

    public String processarCSV(InputStream csv, List<FileUpload> files) {
        try {
            CsvMapper mapper = new CsvMapper();

            CsvSchema schema = CsvSchema.emptySchema()
                    .withHeader();

            List<AnexosInput> anexos = mapper
                    .readerFor(AnexosInput.class)
                    .with(schema)
                    .<AnexosInput>readValues(csv)
                    .readAll();

//            final var contasFatos = listaMapaGuias.stream().map(TransformerUtil::transformerToContaFato).toList();
            droolsRulesProcessor.processarRegras(Collections.singletonList(anexos));
            anexos.stream().forEach(anexo -> {
                /**
                 * Validaçao de anexos para cada guia, verificando
                 * quais documentos são necessários para
                 * o tipo de guia e comparando com os anexos enviados
                 * documentos*/

            });
            //validando anexos

            // criar demonstrativos de pagamento para cada guia aprovada e retornar resposta

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "";
    }


    // ─────────────────────────────────────────────────────────────────────────
    // Métodos auxiliares privados
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Valida a unicidade do {@code sequencialReferencia} dentro da lista de procedimentos.
     * <p>
     * Regra TISS: "O campo {@code sequencialReferencia} não pode se repetir dentro
     * da mesma guia" (padraoTiss.md §Regra de Negócio).
     * </p>
     *
     * @throws IllegalArgumentException se houver {@code sequencialReferencia} duplicado.
     */
    private void validarSequencialReferencia(List<ProcedimentoRealizado> procedimentos) {
        if (procedimentos == null || procedimentos.isEmpty()) {
            return;
        }

        List<Integer> sequenciais = procedimentos.stream()
                .map(ProcedimentoRealizado::getSequencialReferencia)
                .filter(Objects::nonNull)
                .collect(toList());

        Set<Integer> unicos = new HashSet<>(sequenciais);
        if (unicos.size() < sequenciais.size()) {
            throw new IllegalArgumentException(
                    "sequencialReferencia deve ser único por guia. "
                    + "Valores duplicados encontrados: " + sequenciais);
        }
    }

    /**
     * Calcula o valor total da guia como soma dos {@code valorTotalProcedimento}
     * de cada procedimento.
     *
     * @param procedimentos lista de procedimentos realizados.
     * @return soma dos valores totais; {@link BigDecimal#ZERO} se a lista for nula ou vazia.
     */
    private BigDecimal calcularValorTotalGuia(List<ProcedimentoRealizado> procedimentos) {
        if (procedimentos == null || procedimentos.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return procedimentos.stream()
                .filter(p -> p.getValores() != null
                        && p.getValores().getValorTotalProcedimento() != null)
                .map(p -> p.getValores().getValorTotalProcedimento())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
