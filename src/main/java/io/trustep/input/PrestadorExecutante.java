package io.trustep.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Bloco <b>Prestador Executante</b> da Guia de Cobrança — fluxo loteGuias
 * (padraoTiss.md §Prestador Executante).
 * <p>
 * Vinculado ao {@link TissInput} como sub-objeto obrigatório.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrestadorExecutante {

    /**
     * Código do contratado executante na operadora. Máx. 14 caracteres.
     * Equivalente ao campo TISS {@code codigoContratadoExecutanteOperadora}.
     */
    @NotBlank
    @Size(max = 14)
    private String codigoContratadoExecutanteOperadora;

    /** Código CNES do estabelecimento executante. Máx. 7 caracteres. */
    @NotBlank
    @Size(max = 7)
    private String codigoCNESExecutante;

    /**
     * Nome do profissional executante.
     * Condicionado — obrigatório para guias em que há identificação do profissional.
     * Máx. 70 caracteres.
     */
    @Size(max = 70)
    private String nomeProfissionalExecutante;

    /** Sigla do conselho profissional (ex.: "CRM", "CRO"). Máx. 2 caracteres. */
    @NotBlank
    @Size(max = 2)
    private String conselhoProfissional;

    /** Número de registro no conselho profissional. Máx. 15 caracteres. */
    @NotBlank
    @Size(max = 15)
    private String numeroConselho;

    /** UF do conselho profissional (ex.: "SP", "RJ"). Máx. 2 caracteres. */
    @NotBlank
    @Size(max = 2)
    private String ufConselho;

    /** Código Brasileiro de Ocupações (CBO) do profissional. Máx. 6 caracteres. */
    @NotBlank
    @Size(max = 6)
    private String codigoCBO;
}
