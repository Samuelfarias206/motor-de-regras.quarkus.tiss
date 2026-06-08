package io.trustep.store;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.Year;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Store em memória que mantém o estado dos protocolos TISS durante o processamento.
 * <p>
 * Thread-safe graças ao uso de {@link ConcurrentHashMap} e {@link AtomicInteger}.
 * Em produção, seria substituído por persistência (ex: Panache + PostgreSQL).
 * </p>
 */
@ApplicationScoped
public class ProtocoloStore {

    private final ConcurrentHashMap<String, ProtocoloState> store = new ConcurrentHashMap<>();
    private final AtomicInteger seqProtocolo = new AtomicInteger(1);
    private final AtomicInteger seqTitulo = new AtomicInteger(100);

    /**
     * Gera um número de protocolo único no formato {@code PROTO-YYYYNNNNN}.
     * Ex: {@code PROTO-202600001}
     */
    public String gerarNumeroProtocolo() {
        return String.format("PROTO-%d%05d", Year.now().getValue(), seqProtocolo.getAndIncrement());
    }

    /**
     * Gera um número de título financeiro único no formato {@code FIN-YYYYNNNNN}.
     * Ex: {@code FIN-202600100}
     */
    public String gerarNumeroTitulo() {
        return String.format("FIN-%d%05d", Year.now().getValue(), seqTitulo.getAndIncrement());
    }

    /** Persiste ou atualiza o estado de um protocolo. */
    public void salvar(String numeroProtocolo, ProtocoloState state) {
        store.put(numeroProtocolo, state);
    }

    /**
     * Recupera o estado de um protocolo.
     *
     * @throws IllegalArgumentException se o protocolo não existir.
     */
    public ProtocoloState buscar(String numeroProtocolo) {
        ProtocoloState state = store.get(numeroProtocolo);
        if (state == null) {
            throw new IllegalArgumentException(
                    "Protocolo não encontrado: " + numeroProtocolo);
        }
        return state;
    }
}
