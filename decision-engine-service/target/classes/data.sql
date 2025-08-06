-- Regras padrão (DEFAULT) aplicáveis a todos os tipos de transação

-- Regras de faixa de valor
INSERT INTO rules (name, description, tx_type, condition_json, points, active) VALUES
('Valor 0.01-300', 'Transação entre R$0,01 e R$300', 'DEFAULT', '{"type": "value_range", "min": "0.01", "max": "300.00"}', 200, true),
('Valor 301-5000', 'Transação entre R$301 e R$5.000', 'DEFAULT', '{"type": "value_range", "min": "301.00", "max": "5000.00"}', 300, true),
('Valor 5001-20000', 'Transação entre R$5.001 e R$20.000', 'DEFAULT', '{"type": "value_range", "min": "5001.00", "max": "20000.00"}', 400, true),
('Valor acima 20000', 'Transação acima de R$20.000', 'DEFAULT', '{"type": "value_range", "min": "20000.01", "max": "999999999.99"}', 500, true);

-- Regras de listas
INSERT INTO rules (name, description, tx_type, condition_json, points, active) VALUES
('CPF Lista Permissiva', 'CPF está na lista permissiva', 'DEFAULT', '{"type": "cpf_permissive_list"}', -200, true),
('CPF Lista Restritiva', 'CPF está na lista restritiva', 'DEFAULT', '{"type": "cpf_restrictive_list"}', 400, true),
('IP Lista Restritiva', 'IP está na lista restritiva', 'DEFAULT', '{"type": "ip_restrictive_list"}', 400, true),
('Device Lista Restritiva', 'Device está na lista restritiva', 'DEFAULT', '{"type": "device_restrictive_list"}', 400, true);

-- Regras específicas para transações do tipo CARTAO
INSERT INTO rules (name, description, tx_type, condition_json, points, active) VALUES
('Cartão Valor 0.01-300', 'Transação de cartão entre R$0,01 e R$300', 'CARTAO', '{"type": "value_range", "min": "0.01", "max": "300.00"}', 300, true),
('Cartão CPF Lista Permissiva', 'CPF está na lista permissiva para cartão', 'CARTAO', '{"type": "cpf_permissive_list"}', -300, true);

