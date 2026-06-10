package io.trustep.drools.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Procedimento {
    private String operadora;
    private String tipo;
    private BigDecimal valorBase;
    private BigDecimal valorApurado;
}
