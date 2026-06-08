package io.trustep.input;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Corpo da requisição do fluxo <b>recebimentoAnexo</b> — Etapa 2 – Receber Anexos
 * (padraoTiss.md §Informações do Lote).
 * <p>
 * Transporta os metadados do lote e a lista de anexos clínicos ({@link AnexoInput}).
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnexosInput {

    // ── Informações do Lote ──────────────────────────────────────────────────

    /** Número do protocolo TISS gerado na Etapa 1. Obrigatório. Máx. 12 caracteres. */
    @NotBlank
    @Size(max = 12)
    private String numeroProtocolo;

    /** Data de envio do lote de anexos. Obrigatório. Formato AAAA-MM-DD. */
    @NotBlank
    private String dataEnvioLoteAnexos;

    /** Número do lote de anexos clínicos. Obrigatório. Máx. 12 caracteres. */
    @NotBlank
    @Size(max = 12)
    private String numeroLote;

    /** Registro ANS da operadora de saúde. Obrigatório. Máx. 6 caracteres. */
    @NotBlank
    @Size(max = 6)
    private String registroANS;

    /**
     * Código do contratado executante na operadora.
     * Obrigatório. Máx. 14 caracteres.
     */
    @NotBlank
    @Size(max = 14)
    private String codigoContratadoExecutanteOperadora;

    /** Quantidade de anexos clínicos contidos no lote. Obrigatório. */
    @NotNull
    private Integer quantidadeAnexosClinicos;

    /**
     * Observação ou justificativa para o envio do lote.
     * Opcional. Máx. 500 caracteres.
     */
    @Size(max = 500)
    private String observacaoJustificativa;

    // ── Documentos do Lote ───────────────────────────────────────────────────

    /** Lista de anexos clínicos que compõem o lote. */
    @Valid
    private List<AnexoInput> anexos;
}
