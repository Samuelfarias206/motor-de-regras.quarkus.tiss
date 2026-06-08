package io.trustep.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Bloco <b>Beneficiário</b> da Guia de Cobrança — fluxo loteGuias (padraoTiss.md §Beneficiário).
 * <p>
 * Vinculado ao {@link TissInput} como sub-objeto obrigatório.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficiarioGuia {

    /** Número da carteira do beneficiário. Máx. 20 caracteres. */
    @NotBlank
    @Size(max = 20)
    private String numeroCarteiraBeneficiario;

    /** Indicador de recém-nato: "S" ou "N". Máx. 1 caractere. */
    @NotBlank
    @Size(max = 1)
    private String indicadorRecemNato;

    /**
     * Tipo de identificação do beneficiário.
     * Condicionado — obrigatório quando {@code identificadorBeneficiario} for preenchido.
     * Máx. 2 caracteres.
     */
    @Size(max = 2)
    private String tipoIdentificacaoBeneficiario;

    /**
     * Identificação biométrica ou adicional do beneficiário em Base64.
     * Opcional.
     */
    private String identificadorBeneficiario;
}
