package io.trustep.calculators;

import io.trustep.calculators.templates.DynamicTemplate;
import io.trustep.drools.models.Procedimento;
import io.trustep.dto.ContaFato;
import io.trustep.dto.Glosa;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Serviço que gerencia o cálculo. Agora delega totalmente para o motor dinâmico
 * que acessa o banco de dados.
 */
@ApplicationScoped
public class CalculadoraService {

    @Inject
    DynamicTemplate dynamicTemplate;

    /**
     * Método chamado pelo motor do Drools para calcular valores de um Procedimento.
     * @param procedimento o procedimento a ser calculado
     */
    public void calcularProcedimento(Procedimento procedimento) {
        dynamicTemplate.calcularProcedimento(procedimento);
    }

    /**
     * Método chamado pelo motor do Drools para calcular a Glosa de uma conta.
     * @param fato contexto da conta
     * @param glosa objeto de glosa a ser atualizado
     */
    public void calcularGlosa(ContaFato fato, Glosa glosa) {
        dynamicTemplate.calcularGlosa(fato, glosa);
    }
}
