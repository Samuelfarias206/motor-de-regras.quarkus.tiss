package io.trustep.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.trustep.dto.ContaFato;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class TransformerUtil {

    private final static Logger log = LoggerFactory.getLogger(TransformerUtil.class);
    private final static ObjectMapper mapper = new ObjectMapper();

    public static List<ContaFato> transformerToContaFato(Map<String,String> input) {
        try {
            final String json = mapper.writeValueAsString(input);
            return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, ContaFato.class));
        } catch (Exception e) {
            log.error("Erro ao tentar converter String para ContaFato " + e.getMessage());
            throw new RuntimeException("Erro ao converter o conta fato");
        }
    }
}
