package io.trustep.dto;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Glosa {
    private String statusGlosa;
    private boolean permiteRecurso;
    private BigDecimal valorLiberado;
    /** Tipo da glosa aplicada (ex: GLOSA_PARCIAL, GLOSA_TOTAL) — spec §4. */
    private String tipoGlosa;
    /** Valor aprovado após desconto da glosa — spec §4. */
    private BigDecimal valorAprovado;
}
