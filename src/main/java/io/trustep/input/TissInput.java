package io.trustep.input;

import java.math.BigDecimal;

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
public class TissInput {
    private String tipoGuia;
    private String statusProtocolo;
    private BigDecimal valorGuia;
    private boolean possuiGlosa;
    private String tipoGlosa;
    private boolean atendimentoRN;
    private boolean possuiAutorizacao;
}
