package io.trustep.status;

public enum StatusProtocolo {
    // Etapa 1 – Receber Guia
    RECEBIDO,
    // Etapa 2 – Receber Anexos
    ANEXOS_RECEBIDOS,
    // Etapa 3 – Validar Documentação
    DOCUMENTACAO_VALIDADA,
    PENDENTE_DOCUMENTACAO,
    // Etapa 4 – Aprovar Conta
    EM_ANALISE,
    APROVADA,
    GLOSADA,
    REJEITADO,
    AGUARDANDO_PAGAMENTO,
    // Etapa 7 – Pagamento
    PAGO,
    PROCESSADO
}
