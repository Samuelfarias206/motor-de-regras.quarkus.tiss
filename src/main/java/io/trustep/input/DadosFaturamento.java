package io.trustep.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Bloco <b>Dados de Faturamento</b> da Guia de Cobrança — fluxo loteGuias
 * (padraoTiss.md §Dados de Faturamento).
 * <p>
 * Vinculado ao {@link TissInput} como sub-objeto obrigatório.
 * Datas no formato {@code AAAA-MM-DD} e horas no formato {@code HH:MM:SS}.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DadosFaturamento {

    /**
     * Caráter do atendimento (ex.: "1" = eletivo, "2" = urgência/emergência).
     * Máx. 1 caractere.
     */
    @NotBlank
    @Size(max = 1)
    private String caraterAtendimento;

    /**
     * Tipo de faturamento (ex.: "1" = normal, "2" = complementar).
     * Máx. 1 caractere.
     */
    @NotBlank
    @Size(max = 1)
    private String tipoFaturamento;

    /** Data de início do período de faturamento. Formato AAAA-MM-DD. */
    @NotBlank
    private String dataInicioFaturamento;

    /** Hora de início do atendimento. Formato HH:MM:SS. */
    @NotBlank
    private String horaInicioFaturamento;

    /** Data de fim do período de faturamento. Formato AAAA-MM-DD. */
    @NotBlank
    private String dataFimFaturamento;

    /** Hora de fim do atendimento. Formato HH:MM:SS. */
    @NotBlank
    private String horaFimFaturamento;

    /**
     * Tipo de internação (ex.: "1" = clínica, "2" = cirúrgica, "3" = obstétrica).
     * Máx. 1 caractere.
     */
    @NotBlank
    @Size(max = 1)
    private String tipoInternacao;
}
