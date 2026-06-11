package io.trustep.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.trustep.entities.GuiaEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Repository para a entidade {@link GuiaEntity}.
 * <p>
 * Intermediário de comunicação com o banco para operações de Guia SP/SADT TISS.
 * Estende {@link PanacheRepository} que já fornece métodos prontos
 * como {@code persist}, {@code findById}, {@code listAll}, etc.
 * </p>
 */
@ApplicationScoped
public class GuiaRepository implements PanacheRepository<GuiaEntity> {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Busca uma guia pelo número do prestador.
     *
     * @param numeroGuiaPrestador o número da guia do prestador
     * @return Optional contendo a guia, ou vazio se não encontrada
     */
    public Optional<GuiaEntity> findByNumeroGuiaPrestador(String numeroGuiaPrestador) {
        return find("numeroGuiaPrestador", numeroGuiaPrestador).firstResultOptional();
    }

    /**
     * Busca todas as guias pertencentes a um determinado lote.
     *
     * @param loteId o ID do lote
     * @return lista de guias do lote
     */
    public List<GuiaEntity> findByLoteId(Long loteId) {
        return list("lote.id", loteId);
    }

    /**
     * Busca guias pelo número da carteira do beneficiário.
     *
     * @param numeroCarteira o número da carteira do beneficiário
     * @return lista de guias do beneficiário
     */
    public List<GuiaEntity> findByBeneficiario(String numeroCarteira) {
        return list("numeroCarteiraBeneficiario", numeroCarteira);
    }

    /**
     * Busca guias pela operadora.
     *
     * @param operadora o nome da operadora
     * @return lista de guias da operadora
     */
    public List<GuiaEntity> findByOperadora(String operadora) {
        return list("operadora", operadora);
    }

    /**
     * Persiste uma nova guia no banco, preenchendo o timestamp de criação.
     *
     * @param entity a guia a ser persistida
     */
    @Transactional
    public void salvar(GuiaEntity entity) {
        entity.setCriadoEm(LocalDateTime.now().format(FMT));
        persist(entity);
    }
}
