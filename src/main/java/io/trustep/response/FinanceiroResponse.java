package io.trustep.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Resposta da Etapa 6 – Enviar para Financeiro (spec.md §6). */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FinanceiroResponse {
    private String numeroTitulo;
    private String status;
}
