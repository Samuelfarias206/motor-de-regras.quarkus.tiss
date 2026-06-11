package io.trustep.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entidade que armazena as regras dinâmicas parametrizadas no banco de dados.
 */
@Entity
@Table(name = "regra_operadora")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegraOperadoraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "operadora", nullable = false)
    private String operadora; // Ex: UNIMED, AMIL

    @Column(name = "cpf_executante")
    private String cpfExecutante; // Pode ser nulo se a regra valer para todos os médicos

    @Column(name = "tipo_procedimento")
    private String tipoProcedimento; // Ex: CIRURGIA. Pode ser nulo se valer para tudo

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_acao", nullable = false)
    private TipoAcaoRegra tipoAcao;

    @Column(name = "valor_acao", precision = 15, scale = 4, nullable = false)
    private BigDecimal valorAcao; // Ex: 0.50 (50%), ou 100.00 (valor fixo)

    @Column(name = "descricao_log")
    private String descricaoLog; // A mensagem que vai para a rastreabilidade
}
