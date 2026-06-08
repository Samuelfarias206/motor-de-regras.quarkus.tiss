# Fluxo de Processamento de Guias TISS

## 1. Receber Guia

### Objetivo

Receber a guia enviada pelo prestador para análise e processamento.

### Recebe

```json
{
  "numeroGuia": "123456",
  "tipoGuia": "INTERNACAO",
  "codigoPrestador": "987654",
  "codigoBeneficiario": "111222333",
  "dataAtendimento": "2026-06-01",
  "valorGuia": 10000.00
}
```

### Processamento

* Validar estrutura XML TISS.
* Validar schema XSD.
* Validar campos obrigatórios.
* Verificar existência do prestador.
* Verificar existência do beneficiário.
* Registrar protocolo de recebimento.

### Retorna

```json
{
  "numeroProtocolo": "PROTO-202600001",
  "status": "RECEBIDO",
  "mensagem": "Guia recebida com sucesso"
}
```

---

## 2. Receber Anexos

### Objetivo

Receber documentos necessários para análise da guia.

### Recebe

```json
{
  "numeroProtocolo": "PROTO-202600001",
  "anexos": [
    {
      "tipoDocumento": "RELATORIO_MEDICO",
      "arquivo": "base64..."
    },
    {
      "tipoDocumento": "NOTA_FISCAL",
      "arquivo": "base64..."
    }
  ]
}
```

### Processamento

* Validar formato do arquivo.
* Validar tamanho máximo permitido.
* Armazenar documentos.
* Vincular documentos ao protocolo.

### Retorna

```json
{
  "status": "ANEXOS_RECEBIDOS",
  "quantidadeAnexos": 2
}
```

---

## 3. Validar Documentação

### Objetivo

Verificar se todos os documentos obrigatórios foram enviados.

### Recebe

* Dados da guia.
* Lista de anexos recebidos.

### Processamento

Executar DMN:

```text
Validar Documentos Anexos
```

Exemplo:

| Tipo Guia  | Documento Obrigatório |
| ---------- | --------------------- |
| INTERNACAO | RELATORIO_MEDICO      |
| OPME       | NOTA_FISCAL           |
| OPME       | REGISTRO_ANVISA       |
| SADT       | SOLICITACAO_MEDICA    |

### Retorna

Sucesso:

```json
{
  "status": "DOCUMENTACAO_VALIDADA",
  "pendencias": []
}
```

Com pendências:

```json
{
  "status": "PENDENTE_DOCUMENTACAO",
  "pendencias": [
    "REGISTRO_ANVISA"
  ]
}
```

---

## 4. Aprovar Conta

### Objetivo

Executar auditoria técnica e administrativa.

### Recebe

* Guia.
* Anexos.
* Resultado da validação documental.

### Processamento

Executar DMNs:

```text
Validar Elegibilidade do Beneficiário
Verificar Necessidade de Autorização
Processar Glosa
Determinar Status do Protocolo
```

Verificações:

* Beneficiário elegível.
* Existência de autorização.
* Cobrança compatível.
* Procedimentos permitidos.
* Regras contratuais.
* Regras da ANS.

### Retorna

```json
{
  "statusConta": "APROVADA",
  "valorAprovado": 10000.00
}
```

ou

```json
{
  "statusConta": "GLOSADA",
  "tipoGlosa": "GLOSA_PARCIAL",
  "valorAprovado": 5000.00
}
```

---

## 5. Gerar Demonstrativo

### Objetivo

Gerar documento de resultado da análise.

### Recebe

Resultado da auditoria.

### Processamento

Executar DMN:

```text
Determinar Tipo de Demonstrativo
```

Tipos possíveis:

* Demonstrativo de Pagamento Médico
* Demonstrativo de Pagamento Odontológico
* Demonstrativo de Rejeição
* Demonstrativo de Análise de Conta

### Retorna

```json
{
  "tipoDemonstrativo": "DEMONSTRATIVO_PAGAMENTO_MEDICO",
  "arquivoPdf": "..."
}
```

---

## 6. Enviar para Financeiro

### Objetivo

Disponibilizar contas aprovadas para pagamento.

### Recebe

* Guia processada.
* Demonstrativo.
* Valor aprovado.

### Processamento

* Gerar título financeiro.
* Registrar obrigação de pagamento.
* Agendar pagamento.
* Registrar centro de custo.
* Registrar conta contábil.

### Retorna

```json
{
  "numeroTitulo": "FIN-202600100",
  "status": "AGUARDANDO_PAGAMENTO"
}
```

---

## 7. Pagamento

### Objetivo

Efetivar o pagamento ao prestador.

### Recebe

* Título financeiro.
* Dados bancários do prestador.
* Valor aprovado.

### Processamento

* Realizar transferência.
* Registrar baixa financeira.
* Atualizar protocolo.

### Retorna

```json
{
  "status": "PAGO",
  "valorPago": 5000.00,
  "dataPagamento": "2026-06-10"
}
```

---

# Resultado Final do Processo

## Fluxo sem glosa

```text
Recebido
    ↓
Documentação Validada
    ↓
Conta Aprovada
    ↓
Demonstrativo de Pagamento
    ↓
Financeiro
    ↓
Pago
```

## Fluxo com glosa

```text
Recebido
    ↓
Documentação Validada
    ↓
Glosa Parcial
    ↓
Demonstrativo de Glosa
    ↓
Financeiro
    ↓
Pagamento Parcial
```

## Fluxo rejeitado

```text
Recebido
    ↓
Pendência Documental
    ↓
Rejeitado
    ↓
Notificação ao Prestador
```
