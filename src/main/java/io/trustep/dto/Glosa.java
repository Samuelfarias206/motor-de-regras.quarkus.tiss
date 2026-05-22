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
}
