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

-- 4. Acréscimo de 10% para Urgência (carater_atendimento = '02')
INSERT INTO regra_operadora (operadora, cpf_executante, tipo_procedimento, tipo_acao, valor_acao, carater_atendimento, descricao_log)
VALUES ('UNIMED', null, null, 'ACRESCIMO_PERCENTUAL', 0.10, '02', 'Acréscimo de Urgência (10%) aplicado para Unimed.');

INSERT INTO regra_operadora (operadora, cpf_executante, tipo_procedimento, tipo_acao, valor_acao, carater_atendimento, descricao_log)
VALUES ('AMIL', null, null, 'ACRESCIMO_PERCENTUAL', 0.10, '02', 'Acréscimo de Urgência (10%) aplicado para Amil.');

-- 5. Acréscimo de 2% para maiores de 60 anos (idade_minima = 60)
INSERT INTO regra_operadora (operadora, cpf_executante, tipo_procedimento, tipo_acao, valor_acao, idade_minima, descricao_log)
VALUES ('UNIMED', null, null, 'ACRESCIMO_PERCENTUAL', 0.02, 60, 'Acréscimo de Risco por Idade (maior que 60 anos) (2%) aplicado para Unimed.');

INSERT INTO regra_operadora (operadora, cpf_executante, tipo_procedimento, tipo_acao, valor_acao, idade_minima, descricao_log)
VALUES ('AMIL', null, null, 'ACRESCIMO_PERCENTUAL', 0.02, 60, 'Acréscimo de Risco por Idade (maior que 60 anos) (2%) aplicado para Amil.');
