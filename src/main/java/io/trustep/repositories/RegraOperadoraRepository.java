package io.trustep.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.trustep.entities.RegraOperadoraEntity;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class RegraOperadoraRepository implements PanacheRepository<RegraOperadoraEntity> {

    /**
     * Busca as regras que se aplicam a uma determinada operadora, podendo ter filtros
     * específicos por médico e procedimento, ou regras gerais da operadora.
     */
    public List<RegraOperadoraEntity> findRegrasAplicaveis(String operadora, String cpfExecutante, String tipoProcedimento) {
        // Query flexível:
        // Busca regras da operadora onde:
        // (cpf é igual OU cpf na regra é nulo) AND
        // (procedimento é igual OU procedimento na regra é nulo)
        
        return find("operadora = ?1 and " +
                        "(cpfExecutante = ?2 or cpfExecutante is null) and " +
                        "(tipoProcedimento = ?3 or tipoProcedimento is null)",
                operadora, cpfExecutante, tipoProcedimento).list();
    }
}
