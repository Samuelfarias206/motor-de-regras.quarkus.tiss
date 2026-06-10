package io.trustep.dto.sadt;

public class DadosBeneficiarioDTO {
    public String numeroCarteira;          // obrigatório — com dígito verificador (TISS 3.05)
    public String atendimentoRN;           // RN = recém-nascido, "S" ou "N" — obrigatório
    public String nomeBeneficiario;        // obrigatório
    public String numeroCNS;               // Cartão Nacional de Saúde — opcional
    public String dataValidadeCarteira;    // formato yyyy-MM-dd — opcional
    public String nomePlano;               // opcional
}
