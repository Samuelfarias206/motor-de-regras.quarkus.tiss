package io.trustep.dto.sadt;

import java.util.List;

public class GuiaSpSadtDTO {

    // === CABEÇALHO DA GUIA ===
    public String registroANSOperadora;     // registro ANS da operadora — obrigatório
    public String numeroGuiaPrestador;      // numeração própria do prestador — obrigatório
    public String numeroGuiaPrincipal;      // número da guia de autorização da operadora — obrigatório quando autorizado
    public String dataAutorizacao;          // formato yyyy-MM-dd
    public String senhaAutorizacao;         // senha de autorização — quando exigida
    public String dataValidadeSenha;        // formato yyyy-MM-dd

    // === DADOS DO BENEFICIÁRIO ===
    public DadosBeneficiarioDTO beneficiario;    // obrigatório

    // === DADOS DO SOLICITANTE ===
    public DadosSolicitanteDTO solicitante;      // obrigatório

    // === DADOS DA SOLICITAÇÃO ===
    public String caraterAtendimento;       // "01"=eletivo, "02"=urgência/emergência — obrigatório
    public String tipoAtendimento;          // "01"=consulta, "03"=terapias, "05"=internação, etc
    public String indicacaoAcidente;        // "0"=não acidente, "1"=acidente trânsito, "9"=outros
    public String tipoConsulta;             // obrigatório apenas para consultas
    public String motivoEncerramento;       // obrigatório

    // === PROCEDIMENTOS ===
    public List<DadosSolicitacaoProcedimentoDTO> procedimentos;  // obrigatório — até 100 por guia

    // === EXECUTANTE ===
    public DadosContratadoExecutanteDTO executante;        // obrigatório

    // === DADOS PARA CÁLCULO (internos — não vão no XML, usados pelo Drools) ===
    public String operadora;               // "AMIL", "UNIMED" — usado para resolver regras
    public String tipoProcedimento;        // "PARTO", "CIRURGIA" — usado pelo Drools
}
