package io.trustep.calculators;

import io.trustep.drools.models.Procedimento;
import io.trustep.entities.RegraOperadoraEntity;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;

/**
 * Serviço de apoio ao Drools.
 * Executa as operações matemáticas ditadas pelo motor de regras.
 */
@ApplicationScoped
public class CalculadoraService {

    /**
     * Aplica uma regra vinda do banco de dados ao procedimento, conforme 
     * decisão tomada pelo DRL (Pattern Matching).
     *
     * @param procedimento o procedimento sendo calculado
     * @param regra a entidade do banco contendo a ação a ser executada
     */
    public void aplicarRegra(Procedimento procedimento, RegraOperadoraEntity regra) {
        BigDecimal valorFinal = procedimento.getValorApurado() != null 
                ? procedimento.getValorApurado() 
                : procedimento.getValorBase();

        switch (regra.getTipoAcao()) {
            case DESCONTO_PERCENTUAL:
                BigDecimal desconto = valorFinal.multiply(regra.getValorAcao());
                valorFinal = valorFinal.subtract(desconto);
                break;
            case ACRESCIMO_PERCENTUAL:
                BigDecimal acrescimo = valorFinal.multiply(regra.getValorAcao());
                valorFinal = valorFinal.add(acrescimo);
                break;
            case VALOR_FIXO:
                valorFinal = regra.getValorAcao();
                break;
        }

        // Rastreabilidade
        procedimento.getRegrasAplicadas().add(regra.getDescricaoLog());
        procedimento.setValorApurado(valorFinal);
    }

    /**
     * Define o valor base se nenhuma regra for aplicada.
     */
    public void aplicarSemRegra(Procedimento procedimento) {
        if (procedimento.getValorApurado() == null) {
            procedimento.setValorApurado(procedimento.getValorBase());
            procedimento.getRegrasAplicadas().add("Nenhuma regra aplicável encontrada no Drools. Valor base mantido.");
        }
    }

}
