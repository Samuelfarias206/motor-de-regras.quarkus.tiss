package io.trustep;

import io.trustep.input.AnexosInput;
import io.trustep.input.ContaRequest;
import io.trustep.input.ProtocoloRequest;
import io.trustep.input.TissInput;
import io.trustep.response.AnexosResponse;
import io.trustep.response.ContaResponse;
import io.trustep.response.DemonstrativoResponse;
import io.trustep.response.DocumentacaoResponse;
import io.trustep.response.FinanceiroResponse;
import io.trustep.response.PagamentoResponse;
import io.trustep.response.ProtocoloResponse;
import io.trustep.services.TissDecisionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * API REST do fluxo de processamento de guias TISS.
 * <p>
 * Expõe as 7 etapas do spec.md como endpoints POST independentes.
 * Todos produzem e consomem JSON.
 * O estado entre etapas é correlacionado pelo {@code numeroProtocolo}.
 * </p>
 *
 * <pre>
 * POST /tiss/guia                → Etapa 1 – Receber Guia
 * POST /tiss/anexos              → Etapa 2 – Receber Anexos
 * POST /tiss/validar-documentacao → Etapa 3 – Validar Documentação
 * POST /tiss/aprovar-conta       → Etapa 4 – Aprovar Conta
 * POST /tiss/demonstrativo       → Etapa 5 – Gerar Demonstrativo
 * POST /tiss/financeiro          → Etapa 6 – Enviar para Financeiro
 * POST /tiss/pagamento           → Etapa 7 – Pagamento
 * </pre>
 */
@Path("/tiss")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TissResource {

    @Inject
    TissDecisionService service;

    /**
     * Etapa 1 – Receber Guia (spec §1).
     * Valida campos obrigatórios e gera o {@code numeroProtocolo}.
     */
    @POST
    @Path("/guia")
    public ProtocoloResponse receberGuia(TissInput input) {
        return service.receberGuia(input);
    }

    /**
     * Etapa 2 – Receber Anexos (spec §2).
     * Vincula documentos ao protocolo existente.
     */
    @POST
    @Path("/anexos")
    public AnexosResponse receberAnexos(AnexosInput input) {
        return service.receberAnexos(input);
    }

    /**
     * Etapa 3 – Validar Documentação (spec §3).
     * Verifica se todos os documentos obrigatórios foram enviados para o tipo de guia.
     */
    @POST
    @Path("/validar-documentacao")
    public DocumentacaoResponse validarDocumentacao(ProtocoloRequest request) {
        return service.validarDocumentacao(request.getNumeroProtocolo());
    }

    /**
     * Etapa 4 – Aprovar Conta (spec §4).
     * Executa auditoria via regras Drools: elegibilidade, autorização e glosa.
     */
    @POST
    @Path("/aprovar-conta")
    public ContaResponse aprovarConta(ContaRequest request) {
        return service.aprovarConta(request);
    }

    /**
     * Etapa 5 – Gerar Demonstrativo (spec §5).
     * Determina o tipo de demonstrativo e gera o PDF.
     */
    @POST
    @Path("/demonstrativo")
    public DemonstrativoResponse gerarDemonstrativo(ProtocoloRequest request) {
        return service.gerarDemonstrativo(request.getNumeroProtocolo());
    }

    /**
     * Etapa 6 – Enviar para Financeiro (spec §6).
     * Gera título financeiro e agenda pagamento.
     */
    @POST
    @Path("/financeiro")
    public FinanceiroResponse enviarFinanceiro(ProtocoloRequest request) {
        return service.enviarFinanceiro(request.getNumeroProtocolo());
    }

    /**
     * Etapa 7 – Pagamento (spec §7).
     * Efetiva o pagamento ao prestador.
     */
    @POST
    @Path("/pagamento")
    public PagamentoResponse pagamento(ProtocoloRequest request) {
        return service.pagamento(request.getNumeroProtocolo());
    }
}
