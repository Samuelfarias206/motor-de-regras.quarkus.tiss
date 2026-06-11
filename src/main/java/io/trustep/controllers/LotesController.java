package io.trustep.controllers;

import io.trustep.dto.sadt.GuiaSpSadtDTO;
import io.trustep.entities.GuiaEntity;
import io.trustep.entities.LoteEntity;
import io.trustep.repositories.GuiaRepository;
import io.trustep.repositories.LoteRepository;
import io.trustep.services.TissLoteService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

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
 *
 * GET  /tiss/lotes               → Lista todos os lotes salvos
 * GET  /tiss/lotes/{numero}      → Busca lote por número
 * GET  /tiss/guias               → Lista todas as guias salvas
 * GET  /tiss/guias/{numero}      → Busca guia por número do prestador
 * </pre>
 */
@Path("/tiss")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LotesController {

    @Inject
    TissLoteService loteService;

    @Inject
    LoteRepository loteRepository;

    @Inject
    GuiaRepository guiaRepository;

//    /**
//     * Etapa 1 – Receber Guia (spec §1).
//     * Valida campos obrigatórios e gera o {@code numeroProtocolo}.
//     */
//    @POST
//    @Path("/guia")
//    public ProtocoloResponse receberGuia(TissInput input) {
//        return service.receberGuia(input);
//    }
//
//    /**
//     * Etapa 2 – Receber Anexos (spec §2).
//     * Vincula documentos ao protocolo existente.
//     */
//    @POST
//    @Path("/anexos")
//    public AnexosResponse receberAnexos(AnexosInput input) {
//        return service.receberAnexos(input);
//    }
//
//    /**
//     * Etapa 3 – Validar Documentação (spec §3).
//     * Verifica se todos os documentos obrigatórios foram enviados para o tipo de guia.
//     */
//    @POST
//    @Path("/validar-documentacao")
//    public DocumentacaoResponse validarDocumentacao(ProtocoloRequest request) {
//        return service.validarDocumentacao(request.getNumeroProtocolo());
//    }

//    /**
//     * Etapa 4 – Aprovar Conta (spec §4).
//     * Executa auditoria via regras Drools: elegibilidade, autorização e glosa.
//     */
//    @POST
//    @Path("/aprovar-conta")
//    public ContaResponse aprovarConta(ContaRequest request) {
//        return service.aprovarConta(request);
//    }
//
//    /**
//     * Etapa 5 – Gerar Demonstrativo (spec §5).
//     * Determina o tipo de demonstrativo e gera o PDF.
//     */
//    @POST
//    @Path("/demonstrativo")
//    public DemonstrativoResponse gerarDemonstrativo(ProtocoloRequest request) {
//        return service.gerarDemonstrativo(request.getNumeroProtocolo());
//    }
//
//    /**
//     * Etapa 6 – Enviar para Financeiro (spec §6).
//     * Gera título financeiro e agenda pagamento.
//     */
//    @POST
//    @Path("/financeiro")
//    public FinanceiroResponse enviarFinanceiro(ProtocoloRequest request) {
//        return service.enviarFinanceiro(request.getNumeroProtocolo());
//    }
//
//    /**
//     * Etapa 7 – Pagamento (spec §7).
//     * Efetiva o pagamento ao prestador.
//     */
//    @POST
//    @Path("/pagamento")
//    public PagamentoResponse pagamento(ProtocoloRequest request) {
//        return service.pagamento(request.getNumeroProtocolo());
//    }

    // =========================================================================
    // CONSULTAS — endpoints GET para visualizar dados persistidos no H2
    // =========================================================================

    @POST
    @Path("/lote")
    public String processarLote(List<GuiaSpSadtDTO> guiaSpSadtDTO) {
        return this.loteService.gerarLoteXml(guiaSpSadtDTO);
    }

    @POST
    @Path("/lote-xml")
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    public String processarLoteXml(String request) {
        return this.loteService.gerarLoteXml(request);
    }


    /** Lista todos os lotes salvos no banco. */
    @GET
    @Path("/lotes")
    public List<LoteEntity> listarLotes() {
        return loteRepository.listAll();
    }

    /** Busca um lote específico pelo número. */
    @GET
    @Path("/lotes/{numeroLote}")
    public LoteEntity buscarLote(@PathParam("numeroLote") String numeroLote) {
        return loteRepository.findByNumeroLote(numeroLote)
                .orElse(null);
    }

    /** Lista todas as guias salvas no banco. */
    @GET
    @Path("/guias")
    public List<GuiaEntity> listarGuias() {
        return guiaRepository.listAll();
    }

    /** Busca uma guia pelo número do prestador. */
    @GET
    @Path("/guias/{numeroGuiaPrestador}")
    public GuiaEntity buscarGuia(@PathParam("numeroGuiaPrestador") String numeroGuiaPrestador) {
        return guiaRepository.findByNumeroGuiaPrestador(numeroGuiaPrestador)
                .orElse(null);
    }

    /** Lista todas as guias de um lote específico. */
    @GET
    @Path("/lotes/{numeroLote}/guias")
    public List<GuiaEntity> listarGuiasDoLote(@PathParam("numeroLote") String numeroLote) {
        return loteRepository.findByNumeroLote(numeroLote)
                .map(lote -> guiaRepository.findByLoteId(lote.getId()))
                .orElse(List.of());
    }

//    @POST
//    @Path("/enviarcsv")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    public String enviarCSV(@RestForm("csv") InputStream csv,
//                            @RestForm("files") List<FileUpload> files) {
//        return service.processarCSV(csv, files);
//    }

}
