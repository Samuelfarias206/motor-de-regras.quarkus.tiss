package io.trustep.status;

/**
 * Tipos de demonstrativo gerados na Etapa 5 – Gerar Demonstrativo (spec.md §5).
 */
public enum TipoDemonstrativo {
    /** Conta médica aprovada sem glosa. */
    DEMONSTRATIVO_PAGAMENTO_MEDICO,
    /** Conta odontológica aprovada (tipoGuia == ODONTO). */
    DEMONSTRATIVO_PAGAMENTO_ODONTOLOGICO,
    /** Conta rejeitada por pendência documental. */
    DEMONSTRATIVO_REJEICAO,
    /** Conta com glosa parcial ou total. */
    DEMONSTRATIVO_ANALISE_CONTA
}
