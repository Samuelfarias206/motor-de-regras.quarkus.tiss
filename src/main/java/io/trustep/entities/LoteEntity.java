package io.trustep.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade JPA que representa um Lote TISS gerado.
 * <p>
 * Contém as informações do lote (número, data, valor total)
 * e se relaciona com as guias ({@link GuiaEntity}) processadas no lote.
 * </p>
 */
@Entity
@Table(name = "lote")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Número único do lote (ex: UUID truncado gerado pelo serviço). */
    @Column(name = "numero_lote", unique = true, nullable = false)
    private String numeroLote;

    /** Registro ANS da operadora destinatária do lote. */
    @Column(name = "registro_ans_operadora")
    private String registroANSOperadora;

    /** Valor total somado de todas as guias do lote. */
    @Column(name = "valor_total_lote", precision = 15, scale = 2)
    private BigDecimal valorTotalLote;

    /** Hash MD5 do epilogo TISS. */
    @Column(name = "hash_epilogo")
    private String hashEpilogo;

    /** Data/hora de criação do lote (formato yyyy-MM-dd HH:mm:ss). */
    @Column(name = "criado_em", nullable = false, updatable = false)
    private String criadoEm;

    /** Guias pertencentes a este lote. */
    @Builder.Default
    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<GuiaEntity> guias = new ArrayList<>();
}
