package io.trustep.services;

import io.trustep.dto.Autorizacao;
import io.trustep.dto.Elegibilidade;
import io.trustep.dto.Glosa;
import io.trustep.input.TissInput;
import io.trustep.output.TissResultante;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;

// imports for Drools KIE
import org.kie.api.KieBase;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieSession;
import org.kie.api.io.ResourceType;
import org.kie.internal.utils.KieHelper;
import org.kie.internal.io.ResourceFactory;

import java.io.StringWriter;

@ApplicationScoped
@RequiredArgsConstructor
public class TissDecisionService {

    private final DMNRuntime dmnRuntime;

    private final KieHelper kieHelper;

    public String processarTiss(TissInput input) {
        try {
            // Cria objetos de saída iniciais (valores default)
            Elegibilidade elegibilidade = Elegibilidade.builder()
                    .elegivel(false)
                    .mensagem("Beneficiário não elegível")
                    .build();

            Autorizacao autorizacao = Autorizacao.builder()
                    .necessitaAutorizacao(false)
                    .tipoAutorizacao("")
                    .build();

            Glosa glosa = Glosa.builder()
                    .statusGlosa("SEM_GLOSA")
                    .permiteRecurso(false)
                    .valorLiberado(input.getValorGuia())
                    .build();

            // Carrega e executa regras Drools do arquivo classpath:rules/tiss-rules.drl
            KieHelper kieHelper = new KieHelper();
            kieHelper.addResource(ResourceFactory.newClassPathResource("rules/tiss-rules.drl"), ResourceType.DRL);
            Results verify = kieHelper.verify();
            if (verify != null && verify.getMessages().size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (Message m : verify.getMessages()) {
                    sb.append(m.getText()).append("\n");
                }
                throw new RuntimeException("Erros ao compilar regras: \n" + sb.toString());
            }

            KieBase kbase = kieHelper.build();
            KieSession ksession = kbase.newKieSession();

            // Insere os fatos: entrada e objetos de saída (para serem atualizados pelas regras)
            ksession.insert(input);
            ksession.insert(elegibilidade);
            ksession.insert(autorizacao);
            ksession.insert(glosa);

            ksession.fireAllRules();

            ksession.dispose();

            TissResultante resultado = TissResultante.builder()
                    .elegibilidade(elegibilidade)
                    .autorizacao(autorizacao)
                    .glosa(glosa)
                    .build();

            JAXBContext context =
                    JAXBContext.newInstance(TissResultante.class);

            Marshaller marshaller =
                    context.createMarshaller();

            marshaller.setProperty(
                    Marshaller.JAXB_FORMATTED_OUTPUT,
                    true
            );

            StringWriter writer = new StringWriter();

            marshaller.marshal(resultado, writer);

            return writer.toString();

        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }



//    public Map<String, Object> avaliar(TissInput input) {
//
//        DMNModel dmnModel = dmnRuntime.getModels().get(0);
//
//        DMNContext context = dmnRuntime.newContext();
//
//        context.set("tipoGuia", input.getTipoGuia());
//        context.set("statusProtocolo", input.getStatusProtocolo());
//        context.set("valorGuia", input.getValorGuia());
//        context.set("possuiGlosa", input.getPossuiGlosa());
//        context.set("tipoGlosa", input.getTipoGlosa());
//        context.set("atendimentoRN", input.getAtendimentoRN());
//        context.set("possuiAutorizacao", input.getPossuiAutorizacao());
//
//        DMNResult result = dmnRuntime.evaluateAll(dmnModel, context);
//
//        if (result.hasErrors()) {
//            throw new RuntimeException(result.getMessages().toString());
//        }
//
//        Map<String, Object> response = new HashMap<>();
//
//        result.getDecisionResults().forEach(decision ->
//                response.put(decision.getDecisionName(), decision.getResult())
//        );
//
//        return response;
//    }

}
