package io.trustep.drools.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    // === METADADOS PARA ANESTESIOLOGIA E REGRAS COMPLEXAS ===
    private LocalDate dataExecucao;
    private Integer idadePaciente;
    private String caraterAtendimento; // "01"=Eletivo, "02"=Urgência
    private String grauParticipacao;   // "00", "01", "12", etc.
    
    // Rastreabilidade: guarda o histórico do que foi aplicado ao procedimento
    private List<String> regrasAplicadas = new ArrayList<>();
}
