package io.trustep.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.trustep.entities.LoteEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Repository para a entidade {@link LoteEntity}.
 * <p>
 * Intermediário de comunicação com o banco para operações de Lote TISS.
 * Estende {@link PanacheRepository} que já fornece métodos prontos
 * como {@code persist}, {@code findById}, {@code listAll}, etc.
 * </p>
 */
@ApplicationScoped
public class LoteRepository implements PanacheRepository<LoteEntity> {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Busca um lote pelo seu número único.
     *
     * @param numeroLote o número do lote
     * @return Optional contendo o lote, ou vazio se não encontrado
     */
    public Optional<LoteEntity> findByNumeroLote(String numeroLote) {
        return find("numeroLote", numeroLote).firstResultOptional();
    }

    /**
     * Busca todos os lotes de uma determinada operadora (por registro ANS).
     *
     * @param registroANS o registro ANS da operadora
     * @return lista de lotes da operadora
     */
    public List<LoteEntity> findByRegistroANSOperadora(String registroANS) {
        return list("registroANSOperadora", registroANS);
    }

    /**
     * Persiste um novo lote no banco, preenchendo o timestamp de criação.
     *
     * @param entity o lote a ser persistido
     */
    @Transactional
    public void salvar(LoteEntity entity) {
        entity.setCriadoEm(LocalDateTime.now().format(FMT));
        persist(entity);
    }
}
