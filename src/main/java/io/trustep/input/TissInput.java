package io.trustep.input;

import io.trustep.status.StatusProtocolo;
import io.trustep.status.TipoGlosa;
import io.trustep.status.TipoGuia;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TissInput {
    private String tipoGuia;

    private String statusProtocolo;

    private BigDecimal valorGuia;

    private Boolean possuiGlosa;

    private String tipoGlosa;

    private Boolean atendimentoRN;

    private Boolean possuiAutorizacao;
}
