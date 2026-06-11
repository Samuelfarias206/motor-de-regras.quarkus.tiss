package io.trustep.utils;

import io.trustep.calculators.CalculadoraService;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class DroolsRulesProcessor {

    private final KieBase kieBase;
    private final CalculadoraService calculadoraService;

    public void processarRegras(List<Object> input) {
        KieSession session = kieBase.newKieSession();
        try {
            // Injeta o serviço Java como global no Drools
            session.setGlobal("calculadora", calculadoraService);
            
            input.forEach(session::insert);
            session.fireAllRules();
        } finally {
            session.dispose();
        }
    }
}
