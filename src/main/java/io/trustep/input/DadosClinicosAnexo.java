package io.trustep.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Bloco <b>Dados Clínicos</b> do Anexo Clínico — fluxo recebimentoAnexo (padraoTiss.md §Dados Clínicos).
 * <p>
 * Preenchido conforme o tipo de guia (ex.: Quimioterapia). Os campos marcados como
 * {@code Opcional} podem ser {@code null}; os marcados como {@code Condicionado}
 * dependem da situação clínica do beneficiário.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DadosClinicosAnexo {

    /** Peso do beneficiário em kg — formato 3,2. */
    @NotNull
    private BigDecimal pesoBeneficiario;

    /** Altura do beneficiário em m — formato 3,2. */
    @NotNull
    private BigDecimal alturaBeneficiario;

    /** Superfície corporal em m² — formato 2,2. */
    @NotNull
    private BigDecimal superficieCorporal;

    /** Idade do beneficiário em anos. */
    @NotNull
    private Integer idadeBeneficiario;

    /** Sexo do beneficiário: "M" (masculino) ou "F" (feminino). Máx. 1 caractere. */
    @NotBlank
    @Size(max = 1)
    private String sexoBeneficiario;

    /** Nome do profissional solicitante. Máx. 70 caracteres. */
    @NotBlank
    @Size(max = 70)
    private String nomeProfissionalSolicitante;

    /** Telefone do profissional solicitante (apenas dígitos). Máx. 11 caracteres. */
    @NotBlank
    @Size(max = 11)
    private String telefoneProfissionalSolicitante;

    /** E-mail do profissional solicitante. Condicionado. Máx. 60 caracteres. */
    @Size(max = 60)
    private String emailProfissionalSolicitante;

    /** Data do diagnóstico. Condicionado. Formato AAAA-MM-DD. */
    private String dataDiagnostico;

    /** CID-10 do diagnóstico principal. Opcional. Máx. 4 caracteres. */
    @Size(max = 4)
    private String diagnosticoPrincipal;

    /** CID-10 do diagnóstico secundário. Opcional. Máx. 4 caracteres. */
    @Size(max = 4)
    private String diagnosticoSecundario;

    /** CID-10 do terceiro diagnóstico. Opcional. Máx. 4 caracteres. */
    @Size(max = 4)
    private String terceiroDiagnostico;

    /** CID-10 do quarto diagnóstico. Opcional. Máx. 4 caracteres. */
    @Size(max = 4)
    private String quartoDiagnostico;

    /** Estadiamento do tumor (ex.: "I", "II", "III", "IV"). Máx. 1 caractere. */
    @NotBlank
    @Size(max = 1)
    private String estadiamentoTumor;

    /** Finalidade do tratamento (ex.: "C" = curativo, "P" = paliativo). Máx. 1 caractere. */
    @NotBlank
    @Size(max = 1)
    private String finalidadeTratamento;

    /** Escala de capacidade funcional (Karnofsky/ECOG). Máx. 1 caractere. */
    @NotBlank
    @Size(max = 1)
    private String escalaCapacidadeFuncional;
}
