package io.trustep.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Fato de entrada para as regras Drools na Etapa 4 – Aprovar Conta.
 * <p>
 * Substitui o antigo {@code TissInput} nas regras DRL: separa claramente
 * os dados de contexto da guia (vindos do estado em memória + da requisição)
 * dos objetos de saída ({@link Elegibilidade}, {@link Autorizacao}, {@link Glosa}).
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContaFato {
    private BigInteger numeroGuia;
    private String tipoGuia;
    private BigDecimal valorGuia;
    /** Atendimento coberto por Resolução Normativa (RN) da ANS — confere elegibilidade automática. */
    private boolean atendimentoRN;
    /** Indica se há glosa apontada pelo auditor. */
    private boolean possuiGlosa;
    /** Tipo de glosa informado (GLOSA_PARCIAL, GLOSA_TOTAL, etc.). */
    private String tipoGlosa;
    /** Confirma se o prestador já possui autorização para o procedimento. */
    private boolean possuiAutorizacao;
    private String statusProtocolo;
}
