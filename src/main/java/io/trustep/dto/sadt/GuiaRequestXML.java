package io.trustep.dto.sadt;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

/**
 * Wrapper raiz para o XML de entrada.
 * Mapeia:
 * <pre>{@code
 * <guias>
 *   <guia>...</guia>
 *   <guia>...</guia>
 * </guias>
 * }</pre>
 */
@JacksonXmlRootElement(localName = "guias")
public class GuiaRequestXML {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "guia")
    public List<GuiaSpSadtDTO> guias;
}
