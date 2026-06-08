package io.trustep.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Corpo genérico das etapas 3, 5, 6 e 7 — apenas referencia o protocolo já existente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProtocoloRequest {
    private String numeroProtocolo;
}
