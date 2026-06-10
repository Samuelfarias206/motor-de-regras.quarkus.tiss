package io.trustep.dto.sadt;


public class DadosContratadoExecutanteDTO {
    // Quem EXECUTOU — o anestesista da cooperativa
    public String codigoOperadoraExecutante;  // código do prestador na operadora
    public String cpfExecutante;              // CPF do médico executante — obrigatório
    public String nomeExecutante;             // obrigatório
    public String conselhoProfissional;       // "CRM"
    public String numeroConselho;             // número CRM
    public String ufConselho;                 // UF
    public String cbos;                       // código CBOS
    public String grauParticipacao;           // "01"=cirurgião, "13"=anestesista — obrigatório
}
