package io.trustep.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Entidade JPA que representa uma Guia SP/SADT TISS.
 * <p>
 * Mapeia os campos principais do {@code GuiaSpSadtDTO} para persistência
 * no banco de dados. Pertence a um {@link LoteEntity}.
 * </p>
 */
@Entity
@Table(name = "guia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuiaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Lote ao qual esta guia pertence. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    @JsonBackReference
    private LoteEntity lote;

    /** Número da guia do prestador. */
    @Column(name = "numero_guia_prestador")
    private String numeroGuiaPrestador;

    /** Número da guia principal (autorização da operadora). */
    @Column(name = "numero_guia_principal")
    private String numeroGuiaPrincipal;

    /** Registro ANS da operadora. */
    @Column(name = "registro_ans_operadora")
    private String registroANSOperadora;

    /** Data de autorização (yyyy-MM-dd). */
    @Column(name = "data_autorizacao")
    private String dataAutorizacao;

    /** Senha de autorização. */
    @Column(name = "senha_autorizacao")
    private String senhaAutorizacao;

    // ── Beneficiário ────────────────────────────────────────────────────────

    @Column(name = "numero_carteira_beneficiario")
    private String numeroCarteiraBeneficiario;

    @Column(name = "nome_beneficiario")
    private String nomeBeneficiario;

    // ── Executante ──────────────────────────────────────────────────────────

    @Column(name = "cpf_executante")
    private String cpfExecutante;

    @Column(name = "nome_executante")
    private String nomeExecutante;

    @Column(name = "grau_participacao")
    private String grauParticipacao;

    // ── Atendimento ─────────────────────────────────────────────────────────

    @Column(name = "carater_atendimento")
    private String caraterAtendimento;

    @Column(name = "tipo_atendimento")
    private String tipoAtendimento;

    /** Operadora usada para resolver regras Drools. */
    @Column(name = "operadora")
    private String operadora;

    /** Tipo de procedimento usado no Drools. */
    @Column(name = "tipo_procedimento")
    private String tipoProcedimento;

    // ── Valores ─────────────────────────────────────────────────────────────

    /** Valor total calculado da guia (soma dos procedimentos após regras Drools). */
    @Column(name = "valor_total_guia", precision = 15, scale = 2)
    private BigDecimal valorTotalGuia;

    /** Sequência da guia dentro do lote (1..N). */
    @Column(name = "sequencia_no_lote")
    private Integer sequenciaNoLote;

    /** Data/hora de criação do registro (formato yyyy-MM-dd HH:mm:ss). */
    @Column(name = "criado_em", nullable = false, updatable = false)
    private String criadoEm;

    @PrePersist
    protected void onPrePersist() {
        if (this.criadoEm == null) {
            this.criadoEm = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }
}
