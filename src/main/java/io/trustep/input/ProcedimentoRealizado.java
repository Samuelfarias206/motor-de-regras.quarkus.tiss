package io.trustep.input;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa um <b>Procedimento Realizado</b> na Guia de Cobrança — fluxo loteGuias
 * (padraoTiss.md §Procedimentos Realizados).
 *
 * <p><b>Regra de negócio:</b> {@code sequencialReferencia} deve ser único dentro
 * da mesma guia. A validação é feita no {@code TissDecisionService.receberGuia()}.</p>
 *
 * <p>Os campos condicionados são opcionais na estrutura mas podem ser obrigatórios
 * dependendo do tipo de guia ou procedimento.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcedimentoRealizado {

    /**
     * Sequencial de referência do procedimento dentro da guia.
     * Condicionado — quando preenchido, deve ser <b>único</b> por guia.
     * Máx. 4 dígitos.
     */
    private Integer sequencialReferencia;

    /** Data de realização do procedimento. Condicionado. Formato AAAA-MM-DD. */
    private String dataRealizacao;

    /** Hora de início do procedimento. Condicionado. Formato HH:MM:SS. */
    private String horaInicial;

    /** Hora de fim do procedimento. Condicionado. Formato HH:MM:SS. */
    private String horaFinal;

    /**
     * Tabela de referência do procedimento (ex.: "22" = TUSS, "00" = própria).
     * Condicionado. Máx. 2 caracteres.
     */
    @Size(max = 2)
    private String tabelaReferencia;

    /**
     * Código do procedimento na tabela informada.
     * Condicionado. Máx. 10 caracteres.
     */
    @Size(max = 10)
    private String codigoProcedimento;

    /**
     * Descrição do procedimento (usado quando não há código padronizado).
     * Condicionado. Máx. 150 caracteres.
     */
    @Size(max = 150)
    private String descricaoProcedimento;

    /** Quantidade realizada do procedimento. Condicionado. Máx. 3 dígitos. */
    private Integer quantidadeRealizada;

    /**
     * Via de acesso do procedimento cirúrgico (ex.: "1" = única, "2" = mesma via).
     * Condicionado. Máx. 1 caractere.
     */
    @Size(max = 1)
    private String viaAcesso;

    /**
     * Técnica utilizada no procedimento cirúrgico (ex.: "1" = convencional, "2" = videolaparoscópica).
     * Condicionado. Máx. 1 caractere.
     */
    @Size(max = 1)
    private String tecnicaUtilizada;

    /** Valores financeiros do procedimento. Obrigatório. */
    @NotNull
    @Valid
    private ValorProcedimento valores;
}
