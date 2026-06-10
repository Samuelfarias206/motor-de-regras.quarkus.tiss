package io.trustep.dto.sadt;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "guias", namespace = "") // Equivalente ao @JacksonXmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GuiaRequestXML {

    @XmlElement(name = "guia")
    public List<GuiaSpSadtDTO> guias;
}
