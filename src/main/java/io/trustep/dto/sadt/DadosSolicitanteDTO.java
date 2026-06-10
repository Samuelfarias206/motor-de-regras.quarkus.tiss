package io.trustep.dto.sadt;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

public class DadosSolicitanteDTO {
    // Quem PEDIU o procedimento — ex: obstetra que chamou o anestesista
    public String codigoOperadoraSolicitante;  // código do prestador na operadora — obrigatório
    public String nomeSolicitante;             // obrigatório
    public String conselhoProfissional;        // "CRM", "CRO", etc — obrigatório
    public String numeroConselho;              // número no conselho — obrigatório
    public String ufConselho;                  // UF do conselho — obrigatório
    public String cbos;                        // código CBOS da ocupação — obrigatório
}
