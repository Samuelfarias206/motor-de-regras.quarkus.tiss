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
import java.time.LocalDate;

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

    // === VARIÁVEIS OPCIONAIS DE ANESTESIOLOGIA E FATURAMENTO COMPLEXO ===

    @Column(name = "data_inicio")
    private LocalDate dataInicio; // A partir de qual data a regra vale

    @Column(name = "data_fim")
    private LocalDate dataFim; // Até qual data a regra vale

    @Column(name = "idade_minima")
    private Integer idadeMinima; // Para regras de risco (ex: pediatria/neonatos)

    @Column(name = "idade_maxima")
    private Integer idadeMaxima; // Para regras de risco (ex: geriatria)

    @Column(name = "carater_atendimento", length = 2)
    private String caraterAtendimento; // Ex: "01" (Eletivo), "02" (Urgência - acréscimo 30%)

    @Column(name = "grau_participacao", length = 2)
    private String grauParticipacao; // Ex: "00" (Principal), "01" (Auxiliar)

    @Column(name = "descricao_log")
    private String descricaoLog; // A mensagem que vai para a rastreabilidade
}
