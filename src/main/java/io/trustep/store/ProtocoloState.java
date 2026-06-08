package io.trustep.store;

import io.trustep.input.BeneficiarioGuia;
import io.trustep.input.DadosFaturamento;
import io.trustep.input.PrestadorExecutante;
import io.trustep.input.ProcedimentoRealizado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa o estado acumulado de um protocolo TISS ao longo das 7 etapas.
 * <p>
 * Mantido em memória pelo {@link ProtocoloStore}.
 * Cada etapa do fluxo lê e atualiza este objeto.
 * </p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProtocoloState {

    // ── Etapa 1: cabeçalho da guia (loteGuias) ──────────────────────────────

    /** Campo TISS: {@code numeroGuiaPrestador}. */
    private String numeroGuiaPrestador;

    /** Registro ANS da operadora. Campo TISS: {@code registroANS}. */
    private String registroANS;

    /** Tipo de guia (INTERNACAO, SADT, OPME, etc.). */
    private String tipoGuia;

    /** Data de início do faturamento — derivado de {@link DadosFaturamento#getDataInicioFaturamento()}. */
    private String dataAtendimento;

    /**
     * Valor total da guia calculado como soma de
     * {@code ProcedimentoRealizado.valores.valorTotalProcedimento}.
     */
    private BigDecimal valorGuia;

    // ── Etapa 1: sub-objetos completos ───────────────────────────────────────

    /** Beneficiário da guia de cobrança. */
    private BeneficiarioGuia beneficiario;

    /** Prestador executante da guia de cobrança. */
    private PrestadorExecutante prestadorExecutante;

    /** Dados de faturamento (período, caráter, tipo de internação). */
    private DadosFaturamento dadosFaturamento;

    /** Lista de procedimentos realizados na guia. */
    @Builder.Default
    private List<ProcedimentoRealizado> procedimentosRealizados = new ArrayList<>();

    // ── Atalhos derivados dos sub-objetos (para retrocompatibilidade interna) ─

    /**
     * Código do contratado executante — derivado de
     * {@link PrestadorExecutante#getCodigoContratadoExecutanteOperadora()}.
     */
    private String codigoPrestador;

    /**
     * Número da carteira do beneficiário — derivado de
     * {@link BeneficiarioGuia#getNumeroCarteiraBeneficiario()}.
     */
    private String codigoBeneficiario;

    // ── Etapa 2: lote de anexos (recebimentoAnexo) ───────────────────────────

    /** Número do lote de anexos clínicos. */
    private String numeroLote;

    /** Data de envio do lote de anexos. Formato AAAA-MM-DD. */
    private String dataEnvioLoteAnexos;

    /**
     * Código do contratado executante informado no lote de anexos.
     * Pode diferir do informado na guia em casos de subcontratação.
     */
    private String codigoContratadoExecutanteAnexo;

    /** Tipos de documentos dos anexos clínicos recebidos (ex.: "RELATORIO_MEDICO"). */
    @Builder.Default
    private List<String> tiposAnexos = new ArrayList<>();

    // ── Status corrente do protocolo ──────────────────────────────────────────

    private String status;

    // ── Etapa 4: resultado da auditoria (Drools) ──────────────────────────────

    private String statusConta;
    private BigDecimal valorAprovado;
    private String tipoGlosa;

    // ── Etapa 5: demonstrativo ────────────────────────────────────────────────

    private String tipoDemonstrativo;

    // ── Etapa 6: financeiro ───────────────────────────────────────────────────

    private String numeroTitulo;
}
