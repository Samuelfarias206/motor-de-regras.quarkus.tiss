package io.trustep.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Intercepta exceções não tratadas da aplicação e as converte no padrão
 * RFC 9457 (Problem Details for HTTP APIs).
 */
public class GlobalExceptionHandler {

    @ServerExceptionMapper
    public Response handleIllegalArgumentException(IllegalArgumentException exception) {
        Map<String, Object> problemDetails = new LinkedHashMap<>();
        problemDetails.put("type", "about:blank");
        problemDetails.put("title", "Requisição Inválida");
        problemDetails.put("status", 400);

        // Se a exceção encapsula outra com uma mensagem mais clara, tentamos extrair a causa raiz.
        String detail = exception.getMessage();
        if (exception.getCause() instanceof IllegalArgumentException) {
            detail = exception.getCause().getMessage();
        }

        problemDetails.put("detail", detail);

        return Response.status(400)
                .type("application/problem+json")
                .entity(problemDetails)
                .build();
    }

    @ServerExceptionMapper
    public Response handleGenericException(Exception exception) {
        // Se já for uma exceção tratada do JAX-RS (ex: 404 Not Found, 415 Unsupported Media Type), deixamos passar.
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        }

        Map<String, Object> problemDetails = new LinkedHashMap<>();
        problemDetails.put("type", "about:blank");
        problemDetails.put("title", "Erro Interno do Servidor");
        problemDetails.put("status", 500);
        problemDetails.put("detail", "Ocorreu um erro inesperado no processamento da requisição: " + exception.getMessage());

        return Response.status(500)
                .type("application/problem+json")
                .entity(problemDetails)
                .build();
    }
}
