package io.trustep.drools.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Procedimento {
    private String operadora;
    private String tipo;
    private BigDecimal valorBase;
    private BigDecimal valorApurado;
    
    // Dados do médico para regras específicas (ex: bônus ou desconto por profissional)
    private String cpfExecutante;
    private String nomeExecutante;
    
    // Rastreabilidade: guarda o histórico do que foi aplicado ao procedimento
    private List<String> regrasAplicadas = new ArrayList<>();
}
