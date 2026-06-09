package io.trustep.input;

import io.trustep.status.StatusProtocolo;
import io.trustep.status.TipoGuia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Corpo da requisição da Etapa 4 – Aprovar Conta (spec.md §4).
 * <p>
 * Transporta os parâmetros de auditoria que, num sistema real, viriam de
 * consultas a sistemas externos (elegibilidade, autorização prévia, etc.).
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContaRequest {
    private String numeroProtocolo;//numeroGuia
    private TipoGuia tipoGuia;
    /** Se o auditor identificou glosa na conta. */
    private boolean possuiGlosa;
    /** Tipo da glosa (GLOSA_PARCIAL, GLOSA_TOTAL, GLOSA_ADMINISTRATIVA, GLOSA_TECNICA). */
    private String tipoGlosa;
    /** Se o prestador apresentou autorização prévia válida. */
    private boolean possuiAutorizacao;
    /** Se o atendimento está coberto por Resolução Normativa da ANS. */
    private boolean atendimentoRN;
    private StatusProtocolo statusProtocolo;
}
