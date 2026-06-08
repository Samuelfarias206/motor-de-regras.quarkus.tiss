package io.trustep.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Resposta da Etapa 3 – Validar Documentação (spec.md §3).
 * <p>
 * Se {@code pendencias} estiver vazia, o status será {@code DOCUMENTACAO_VALIDADA}.
 * Caso contrário, {@code PENDENTE_DOCUMENTACAO} e a lista indicará o que falta.
 * </p>
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DocumentacaoResponse {
    private String status;
    private List<String> pendencias;
}
