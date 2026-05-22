package io.trustep.dto;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Autorizacao {
    private boolean necessitaAutorizacao;
    private String tipoAutorizacao;
}
