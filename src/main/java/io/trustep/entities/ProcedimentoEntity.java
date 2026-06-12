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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Representa os procedimentos executados dentro de uma Guia.
 * Guarda o log de auditoria das regras de negócio aplicadas no Drools.
 */
@Entity
@Table(name = "procedimento_auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcedimentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guia_id", nullable = false)
    @JsonBackReference
    private GuiaEntity guia;

    @Column(name = "codigo_procedimento")
    private String codigoProcedimento;

    @Column(name = "descricao_procedimento")
    private String descricaoProcedimento;

    @Column(name = "valor_base", precision = 15, scale = 2)
    private BigDecimal valorBase;

    @Column(name = "valor_apurado", precision = 15, scale = 2)
    private BigDecimal valorApurado;

    /**
     * Histórico textual (audit trail) das regras aplicadas pelo motor Drools.
     */
    @Column(name = "regras_aplicadas", length = 2000)
    private String regrasAplicadas;
}
