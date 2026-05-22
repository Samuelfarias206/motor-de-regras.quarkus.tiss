package io.trustep.output;

import io.trustep.dto.*;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TissResultante {
    private Elegibilidade elegibilidade;
    private Autorizacao autorizacao;
    private Glosa glosa;
    private StatusProtocolo statusProtocolo;
    private Cancelamento cancelamento;
    private Demonstrativo demonstrativo;
    private List<Documento> documentos;
}
