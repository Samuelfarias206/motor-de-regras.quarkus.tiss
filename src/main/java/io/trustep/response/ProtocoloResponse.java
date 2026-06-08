package io.trustep.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Resposta da Etapa 1 – Receber Guia (spec.md §1). */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProtocoloResponse {
    private String numeroProtocolo;
    private String status;
    private String mensagem;
}
