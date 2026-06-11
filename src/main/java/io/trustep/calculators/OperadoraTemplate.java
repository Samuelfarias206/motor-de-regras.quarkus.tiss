package io.trustep.calculators;

import io.trustep.drools.models.Procedimento;
import io.trustep.dto.ContaFato;
import io.trustep.dto.Glosa;

/**
 * Interface que define o "Template" de cálculos financeiros para uma operadora de saúde.
 * <p>
 * O motor de regras Drools irá repassar a execução para as implementações desta interface,
 * separando a lógica de decisão (Drools) da lógica matemática/financeira (Java).
 * </p>
 */
public interface OperadoraTemplate {

    /**
     * Retorna o nome da operadora ao qual este template se aplica (ex: "UNIMED", "AMIL").
     *
     * @return o identificador da operadora
     */
    String getNomeOperadora();

    /**
     * Aplica regras de cálculo, descontos ou acréscimos a um procedimento.
     * Os detalhes de rastreabilidade devem ser adicionados à lista de regrasAplicadas do Procedimento.
     *
     * @param procedimento o procedimento sendo avaliado
     */
    void calcularProcedimento(Procedimento procedimento);

    /**
     * Aplica regras de cálculo de glosa.
     *
     * @param fato  o contexto da conta/guia sendo processada
     * @param glosa o objeto de resultado a ser preenchido
     */
    void calcularGlosa(ContaFato fato, Glosa glosa);
}
