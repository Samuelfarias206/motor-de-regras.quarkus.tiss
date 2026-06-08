package io.trustep.input;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa um <b>Anexo Clínico</b> individual — fluxo recebimentoAnexo
 * (padraoTiss.md §Identificação da Guia, §Beneficiário, §Dados Clínicos).
 * <p>
 * Cada instância desta classe corresponde a um documento clínico vinculado
 * a uma guia dentro do lote ({@link AnexosInput}).
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnexoInput {

    // ── Identificação da Guia ────────────────────────────────────────────────

    /** Número da guia no prestador. Obrigatório. Máx. 20 caracteres. */
    @NotBlank
    @Size(max = 20)
    private String numeroGuiaPrestador;

    /**
     * Número da guia na operadora.
     * Condicionado — obrigatório quando a operadora já atribuiu número à guia.
     * Máx. 20 caracteres.
     */
    @Size(max = 20)
    private String numeroGuiaOperadora;

    /**
     * Código indicando ausência de código de validação (ex.: "01" = não possui senha).
     * Condicionado. Máx. 2 caracteres.
     */
    @Size(max = 2)
    private String ausenciaCodigoValidacao;

    /**
     * Código de validação / senha da guia.
     * Opcional. Máx. 10 caracteres.
     */
    @Size(max = 10)
    private String codigoValidacao;

    /**
     * Data da autorização da guia pela operadora.
     * Condicionado — obrigatório quando há autorização prévia. Formato AAAA-MM-DD.
     */
    private String dataAutorizacao;

    /**
     * Senha de autorização atribuída pela operadora.
     * Condicionado. Máx. 20 caracteres.
     */
    @Size(max = 20)
    private String senha;

    /**
     * Data de validade da senha de autorização.
     * Condicionado. Formato AAAA-MM-DD.
     */
    private String dataValidadeSenha;

    // ── Beneficiário ──────────────────────────────────────────────────────────

    /** Dados de identificação do beneficiário. Obrigatório. */
    @NotNull
    @Valid
    private DadosBeneficiarioAnexo beneficiario;

    // ── Dados Clínicos ────────────────────────────────────────────────────────

    /**
     * Dados clínicos do beneficiário (ex.: Quimioterapia).
     * Opcional — preencher conforme o tipo de guia.
     */
    @Valid
    private DadosClinicosAnexo dadosClinicos;

    // ── Documento ─────────────────────────────────────────────────────────────

    /** Tipo do documento (ex.: RELATORIO_MEDICO, NOTA_FISCAL, REGISTRO_ANVISA, SOLICITACAO_MEDICA). */
    private String tipoDocumento;

    /** Conteúdo do arquivo em Base64. */
    private String arquivo;
}
