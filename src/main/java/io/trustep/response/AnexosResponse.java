package io.trustep.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Resposta da Etapa 2 – Receber Anexos (spec.md §2). */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AnexosResponse {
    private String status;
    private int quantidadeAnexos;
}
