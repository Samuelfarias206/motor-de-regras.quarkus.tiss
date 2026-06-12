package io.trustep.entities;

/**
 * Representa o ciclo de vida e os status TISS de uma guia médica/SADT.
 */
public enum StatusGuia {
    /** Guia acabou de dar entrada no sistema via lote. */
    RECEBIDA,
    
    /** Regras do motor de negócios aplicadas com sucesso (Status inicial após entrada). */
    PROCESSADA,
    
    /** Guia retida para análise manual (médica ou de enfermagem). */
    EM_AUDITORIA,
    
    /** Guia rejeitada (parcial ou integralmente), aguardando recurso de glosa. */
    GLOSADA,
    
    /** Guia aprovada sem ressalvas, aguardando liquidação financeira. */
    LIBERADA_PARA_PAGAMENTO,
    
    /** Guia liquidada/paga ao prestador. */
    PAGA,
    
    /** Guia cancelada a pedido do prestador. */
    CANCELADA
}
