package io.trustep.calculators.templates;

import io.trustep.calculators.OperadoraTemplate;
import io.trustep.drools.models.Procedimento;
import io.trustep.dto.ContaFato;
import io.trustep.dto.Glosa;
import io.trustep.entities.RegraOperadoraEntity;
import io.trustep.repositories.RegraOperadoraRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.List;

/**
 * Único template real da aplicação. Ele converte os dados do Procedimento em
 * consultas ao banco de dados e aplica as funções matemáticas configuradas.
 */
@ApplicationScoped
public class DynamicTemplate implements OperadoraTemplate {

    @Inject
    RegraOperadoraRepository repository;

    @Override
    public String getNomeOperadora() {
        return "DINAMICO"; 
    }

    @Override
    public void calcularProcedimento(Procedimento procedimento) {
        BigDecimal valorFinal = procedimento.getValorBase();

        if (procedimento.getOperadora() != null) {
            // Busca no banco as regras ativas
            List<RegraOperadoraEntity> regras = repository.findRegrasAplicaveis(
                    procedimento.getOperadora().toUpperCase(),
                    procedimento.getCpfExecutante(),
                    procedimento.getTipo()
            );

            // Aplica cada regra retornada em cascata
            for (RegraOperadoraEntity regra : regras) {
                switch (regra.getTipoAcao()) {
                    case DESCONTO_PERCENTUAL:
                        BigDecimal desconto = valorFinal.multiply(regra.getValorAcao());
                        valorFinal = valorFinal.subtract(desconto);
                        break;
                    case ACRESCIMO_PERCENTUAL:
                        BigDecimal acrescimo = valorFinal.multiply(regra.getValorAcao());
                        valorFinal = valorFinal.add(acrescimo);
                        break;
                    case VALOR_FIXO:
                        valorFinal = regra.getValorAcao();
                        break;
                }
                // Adiciona o log da regra para rastreabilidade
                procedimento.getRegrasAplicadas().add(regra.getDescricaoLog());
            }

            if (regras.isEmpty()) {
                procedimento.getRegrasAplicadas().add("Nenhuma regra específica encontrada. Valor base mantido.");
            }
        } else {
            procedimento.getRegrasAplicadas().add("Operadora não informada. Valor base mantido.");
        }

        procedimento.setValorApurado(valorFinal);
    }

    @Override
    public void calcularGlosa(ContaFato fato, Glosa glosa) {
        // Para simplificar a POC, a glosa continua com a regra padrão aqui.
        // Em um sistema completo, a glosa também teria sua tabela de Regras no BD.
        if (fato.isPossuiGlosa() && fato.getValorGuia() != null) {
            BigDecimal desconto = fato.getValorGuia().multiply(new BigDecimal("0.50"));
            BigDecimal valorFinal = fato.getValorGuia().subtract(desconto);
            String tipo = fato.getTipoGlosa() != null ? fato.getTipoGlosa() : "GLOSA_PARCIAL";

            glosa.setStatusGlosa("GLOSADO");
            glosa.setPermiteRecurso(true);
            glosa.setValorLiberado(valorFinal);
            glosa.setTipoGlosa(tipo);
            glosa.setValorAprovado(valorFinal);
        } else if (fato.getValorGuia() != null) {
            glosa.setValorLiberado(fato.getValorGuia());
            glosa.setValorAprovado(fato.getValorGuia());
        }
    }
}
