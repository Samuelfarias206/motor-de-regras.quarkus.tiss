package io.trustep.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Resposta da Etapa 4 – Aprovar Conta (spec.md §4).
 * <p>
 * {@code tipoGlosa} é nulo quando {@code statusConta == APROVADA}.
 * </p>
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ContaResponse {
    private String statusConta;
    private BigDecimal valorAprovado;
    /** Preenchido apenas quando statusConta == GLOSADA. */
    private String tipoGlosa;
}
