package io.trustep.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Resposta da Etapa 7 – Pagamento (spec.md §7). */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PagamentoResponse {
    private String status;
    private BigDecimal valorPago;
    /** Data do pagamento em formato ISO-8601 (yyyy-MM-dd). */
    private String dataPagamento;
}
