package io.trustep.utils;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class DroolsRulesProcessor {

    private final KieBase kieBase;

    public void processarRegras(List<Object> input) {
        KieSession session = kieBase.newKieSession();
        try {
            input.forEach(session::insert);
            session.fireAllRules();
        } finally {
            session.dispose();
        }
    }
}
