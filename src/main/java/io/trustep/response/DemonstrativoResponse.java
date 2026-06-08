package io.trustep.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Resposta da Etapa 5 – Gerar Demonstrativo (spec.md §5). */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DemonstrativoResponse {
    private String tipoDemonstrativo;
    /** PDF gerado em Base64 (mock para fins de desenvolvimento). */
    private String arquivoPdf;
}
