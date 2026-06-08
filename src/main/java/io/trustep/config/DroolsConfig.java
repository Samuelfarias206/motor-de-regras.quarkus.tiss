package io.trustep.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.kie.api.KieBase;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

/**
 * Configuração CDI para o motor de regras Drools.
 * <p>
 * Produz um {@link KieBase} singleton (ApplicationScoped) construído a partir
 * do arquivo {@code rules/tiss-rules.drl} no classpath.
 * </p>
 * <p>
 * <strong>Por que criar esta classe?</strong><br>
 * O {@link TissDecisionService} precisava injetar {@code KieHelper} diretamente,
 * mas {@code KieHelper} não é um bean CDI — o que causaria falha de startup.
 * A abordagem correta é produzir um {@link KieBase} compilado uma única vez,
 * reutilizado por todas as sessões (KieSession) criadas por requisição.
 * </p>
 */
@ApplicationScoped
public class DroolsConfig {

    @Produces
    @ApplicationScoped
    public KieBase kieBase() {
        KieHelper helper = new KieHelper();
        helper.addResource(
                ResourceFactory.newClassPathResource("rules/tiss-rules.drl"),
                ResourceType.DRL
        );

        Results results = helper.verify();
        if (results != null && !results.getMessages(Message.Level.ERROR).isEmpty()) {
            throw new RuntimeException(
                    "Erros ao compilar regras Drools: " + results.getMessages()
            );
        }

        return helper.build();
    }
}
