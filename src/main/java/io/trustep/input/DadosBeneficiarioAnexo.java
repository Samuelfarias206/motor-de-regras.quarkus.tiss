package io.trustep.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Bloco <b>Beneficiário</b> do Anexo Clínico — fluxo recebimentoAnexo (padraoTiss.md §Beneficiário).
 * <p>
 * Vinculado a cada {@link AnexoInput} dentro do lote de anexos.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DadosBeneficiarioAnexo {

    /** Número da carteira do beneficiário. Máx. 20 caracteres. */
    @NotBlank
    @Size(max = 20)
    private String numeroCarteiraBeneficiario;

    /** Indicador de recém-nato: "S" ou "N". Máx. 1 caractere. */
    @NotBlank
    @Size(max = 1)
    private String indicadorRecemNato;

    /** Nome completo do beneficiário. Máx. 70 caracteres. */
    @NotBlank
    @Size(max = 70)
    private String nomeBeneficiario;

    /**
     * Nome social do beneficiário.
     * Condicionado — obrigatório quando o beneficiário possui nome social registrado.
     * Máx. 70 caracteres.
     */
    @Size(max = 70)
    private String nomeSocial;
}
