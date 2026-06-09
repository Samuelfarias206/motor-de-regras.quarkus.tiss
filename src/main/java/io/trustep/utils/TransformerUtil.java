package io.trustep.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransformerUtil {

    private ObjectMapper mapper;

    public static String transformerToContaFato(String input) {
        try {
//            modelMapper.
            return input != null ? input.toUpperCase() : null;
        } catch (Exception e) {
            // Log the exception if necessary
            return null; // Return null in case of any exception
        }
    }
}
