-- Regras para UNIMED
-- 1. Qualquer CIRURGIA tem desconto de 50%
INSERT INTO regra_operadora (operadora, cpf_executante, tipo_procedimento, tipo_acao, valor_acao, descricao_log)
VALUES ('UNIMED', null, 'CIRURGIA', 'DESCONTO_PERCENTUAL', 0.50, 'Desconto padrão UNIMED (50%) aplicado para CIRURGIA.');

-- 2. Desconto específico para o médico João Castro (CPF 123) na UNIMED
INSERT INTO regra_operadora (operadora, cpf_executante, tipo_procedimento, tipo_acao, valor_acao, descricao_log)
VALUES ('UNIMED', '123', null, 'DESCONTO_PERCENTUAL', 0.10, 'Desconto específico UNIMED para médico João Castro (10%) aplicado.');

-- Regras para AMIL
-- 3. Acréscimo específico para o médico João Castro (CPF 123) na AMIL
INSERT INTO regra_operadora (operadora, cpf_executante, tipo_procedimento, tipo_acao, valor_acao, descricao_log)
VALUES ('AMIL', '123', null, 'ACRESCIMO_PERCENTUAL', 0.10, 'Acréscimo específico AMIL para médico João Castro (10%) aplicado.');
