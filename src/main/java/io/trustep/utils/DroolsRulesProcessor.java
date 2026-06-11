package io.trustep.utils;

import io.trustep.calculators.CalculadoraService;
import io.trustep.entities.RegraOperadoraEntity;
import io.trustep.repositories.RegraOperadoraRepository;
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
    private final RegraOperadoraRepository regraOperadoraRepository;

    public void processarRegras(List<Object> input) {
        KieSession session = kieBase.newKieSession();
        try {
            // Injeta o serviço Java como global no Drools
            session.setGlobal("calculadora", calculadoraService);
            
            // 1. Injeta os Procedimentos e outros fatos no Drools
            input.forEach(session::insert);

            // 2. Busca todas as regras cadastradas no Banco de Dados
            // e injeta na Working Memory do Drools para que ele faça o cruzamento.
            List<RegraOperadoraEntity> regrasDb = regraOperadoraRepository.listAll();
            regrasDb.forEach(session::insert);

            session.fireAllRules();
        } finally {
            session.dispose();
        }
    }
}
