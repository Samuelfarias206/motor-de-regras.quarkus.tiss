package io.trustep.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Bloco <b>Valores Financeiros</b> de cada {@link ProcedimentoRealizado}
 * — fluxo loteGuias (padraoTiss.md §Valores Financeiros).
 * <p>
 * Todos os valores monetários são representados com escala de 2 casas decimais.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValorProcedimento {

    /**
     * Fator de redução ou acréscimo aplicado ao procedimento (ex.: 1.00, 0.70).
     * Formato 1,2 — obrigatório.
     */
    @NotNull
    private BigDecimal fatorReducaoAcrescimo;

    /**
     * Valor unitário do procedimento antes do fator.
     * Formato 8,2 — obrigatório.
     */
    @NotNull
    private BigDecimal valorUnitario;

    /**
     * Valor total do procedimento após aplicação do fator e quantidade.
     * Formato 8,2 — obrigatório.
     * Fórmula: {@code valorUnitario × quantidadeRealizada × fatorReducaoAcrescimo}.
     */
    @NotNull
    private BigDecimal valorTotalProcedimento;

    /**
     * Código do centro de consumo.
     * Condicionado — obrigatório para guias que exijam controle por centro de custo.
     * Máx. 2 caracteres.
     */
    @Size(max = 2)
    private String centroConsumo;

    /**
     * Grau de participação do profissional no procedimento.
     * Condicionado — obrigatório em equipes multiprofissionais.
     * Máx. 2 caracteres.
     */
    @Size(max = 2)
    private String grauParticipacaoProfissional;
}
