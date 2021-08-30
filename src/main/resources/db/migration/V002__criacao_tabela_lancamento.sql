CREATE TABLE IF NOT EXISTS lancamento (
	id INT PRIMARY KEY AUTO_INCREMENT,
	operacao VARCHAR(50) NOT NULL,
	status VARCHAR(50) NOT NULL,
	data_lancamento DATETIME NOT NULL,
	valor NUMERIC(11,2) NOT NULL,
	descricao VARCHAR(100),
	conta_favorecido_id BIGINT  NOT NULL,
	conta_remetente_id BIGINT NULL
);


ALTER TABLE lancamento
ADD CONSTRAINT fk_lancamento_conta_favorecido_id
FOREIGN KEY (conta_favorecido_id) REFERENCES conta(id);

ALTER TABLE lancamento
ADD CONSTRAINT fk_lancamento_conta_remetente_id
FOREIGN KEY (conta_remetente_id) REFERENCES conta(id);