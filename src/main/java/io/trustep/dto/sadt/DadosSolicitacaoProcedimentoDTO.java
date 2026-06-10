package io.trustep.dto.sadt;

public class DadosSolicitacaoProcedimentoDTO {
    public String dataRealizacao;       // formato yyyy-MM-dd — obrigatório
    public String horaInicial;          // formato HH:mm — obrigatório para anestesia
    public String horaFinal;            // formato HH:mm — obrigatório para anestesia
    public String codigoTabela;         // "22" = TUSS, "00" = AMB, "98" = tabela própria
    public String codigoProcedimento;   // código TUSS/CBHPM — obrigatório
    public String descricaoProcedimento;
    public int quantidade;              // obrigatório
    public double valorUnitario;        // valor base do procedimento
    public String viaAcesso;            // código via de acesso — obrigatório para cirurgia
    public String tecnicaUtilizada;     // código — obrigatório para cirurgia
    public String reducaoAcrescimo;     // "00" sem redução, "70" com redução etc
}
