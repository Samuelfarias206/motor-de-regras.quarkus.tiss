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

import java.util.List;

/**
 * Corpo da requisição do fluxo <b>loteGuias</b> — Etapa 1 – Receber Guia
 * (padraoTiss.md §Cabeçalho da Guia).
 * <p>
 * Representa a Guia de Cobrança com cabeçalho de autorização, beneficiário,
 * prestador executante, dados de faturamento e procedimentos realizados.
 * </p>
 * <p>
 * O {@code valorGuia} total é calculado automaticamente pelo serviço como
 * a soma de {@link ValorProcedimento#getValorTotalProcedimento()} de cada
 * {@link ProcedimentoRealizado}.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TissInput {

    // ── Cabeçalho da Guia ────────────────────────────────────────────────────

    /** Registro ANS da operadora de saúde. Obrigatório. Máx. 6 caracteres. */
    @NotBlank
    @Size(max = 6)
    private String registroANS;

    /**
     * Número da guia atribuído pelo prestador.
     * Obrigatório. Máx. 20 caracteres.
     * Campo TISS: {@code numeroGuiaPrestador}.
     */
    @NotBlank
    @Size(max = 20)
    private String numeroGuiaPrestador;

    /** Data da autorização emitida pela operadora. Obrigatório. Formato AAAA-MM-DD. */
    @NotBlank
    private String dataAutorizacao;

    /** Senha de autorização emitida pela operadora. Obrigatório. Máx. 20 caracteres. */
    @NotBlank
    @Size(max = 20)
    private String senha;

    /**
     * Data de validade da senha de autorização.
     * Condicionado. Formato AAAA-MM-DD.
     */
    private String dataValidadeSenha;

    /**
     * Código indicando ausência de código de validação (ex.: "01" = não possui senha).
     * Condicionado. Máx. 2 caracteres.
     */
    @Size(max = 2)
    private String ausenciaCodigoValidacao;

    /**
     * Código de validação alternativo.
     * Opcional. Máx. 10 caracteres.
     */
    @Size(max = 10)
    private String codigoValidacao;

    /**
     * Tipo de guia (ex.: INTERNACAO, SADT, OPME, QUIMIOTERAPIA, ODONTO).
     * Obrigatório — usado nas regras de negócio e motor Drools.
     */
    @NotBlank
    private String tipoGuia;

    // ── Beneficiário ──────────────────────────────────────────────────────────

    /** Identificação do beneficiário. Obrigatório. */
    @NotNull
    @Valid
    private BeneficiarioGuia beneficiario;

    // ── Prestador Executante ───────────────────────────────────────────────────

    /** Dados do prestador executante. Obrigatório. */
    @NotNull
    @Valid
    private PrestadorExecutante prestadorExecutante;

    // ── Dados de Faturamento ───────────────────────────────────────────────────

    /** Período e tipo de faturamento. Obrigatório. */
    @NotNull
    @Valid
    private DadosFaturamento dadosFaturamento;

    // ── Procedimentos Realizados ───────────────────────────────────────────────

    /**
     * Lista de procedimentos realizados na guia.
     * Obrigatório — deve conter ao menos um procedimento.
     * Regra: {@code sequencialReferencia} deve ser único por guia.
     */
    @NotNull
    @Valid
    private List<ProcedimentoRealizado> procedimentosRealizados;
}
