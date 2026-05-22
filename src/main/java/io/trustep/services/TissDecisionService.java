package io.trustep.services;

import io.trustep.input.TissInput;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
@RequiredArgsConstructor
public class TissDecisionService {

    private final DMNRuntime dmnRuntime;

    public Map<String, Object> avaliar(TissInput input) {

        DMNModel dmnModel = dmnRuntime.getModels().get(0);

        DMNContext context = dmnRuntime.newContext();

        context.set("tipoGuia", input.getTipoGuia());
        context.set("statusProtocolo", input.getStatusProtocolo());
        context.set("valorGuia", input.getValorGuia());
        context.set("possuiGlosa", input.getPossuiGlosa());
        context.set("tipoGlosa", input.getTipoGlosa());
        context.set("atendimentoRN", input.getAtendimentoRN());
        context.set("possuiAutorizacao", input.getPossuiAutorizacao());

        DMNResult result = dmnRuntime.evaluateAll(dmnModel, context);

        if (result.hasErrors()) {
            throw new RuntimeException(result.getMessages().toString());
        }

        Map<String, Object> response = new HashMap<>();

        result.getDecisionResults().forEach(decision ->
                response.put(decision.getDecisionName(), decision.getResult())
        );

        return response;
    }

}
