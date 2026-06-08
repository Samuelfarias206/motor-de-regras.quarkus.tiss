# Padrão TISS 2025 - Recorte de Dados

## Fluxo: Recebimento de Lote de Anexos (recebimentoAnexo)

### Informações do Lote

| Campo                               | Tipo    | Tamanho | Formato    | Obrigatoriedade |
| ----------------------------------- | ------- | ------- | ---------- | --------------- |
| numeroProtocolo                     | String  | 12      | -          | Obrigatório     |
| dataEnvioLoteAnexos                 | Date    | 8       | AAAA-MM-DD | Obrigatório     |
| numeroLote                          | String  | 12      | -          | Obrigatório     |
| registroANS                         | String  | 6       | -          | Obrigatório     |
| codigoContratadoExecutanteOperadora | String  | 14      | -          | Obrigatório     |
| quantidadeAnexosClinicos            | Integer | 3       | -          | Obrigatório     |
| observacaoJustificativa             | String  | 500     | -          | Opcional        |

### Identificação da Guia

| Campo                   | Tipo   | Tamanho | Formato    | Obrigatoriedade |
| ----------------------- | ------ | ------- | ---------- | --------------- |
| numeroGuiaPrestador     | String | 20      | -          | Obrigatório     |
| numeroGuiaOperadora     | String | 20      | -          | Condicionado    |
| ausenciaCodigoValidacao | String | 2       | -          | Condicionado    |
| codigoValidacao         | String | 10      | -          | Opcional        |
| dataAutorizacao         | Date   | 8       | AAAA-MM-DD | Condicionado    |
| senha                   | String | 20      | -          | Condicionado    |
| dataValidadeSenha       | Date   | 8       | AAAA-MM-DD | Condicionado    |

### Beneficiário

| Campo                      | Tipo   | Tamanho | Obrigatoriedade |
| -------------------------- | ------ | ------- | --------------- |
| numeroCarteiraBeneficiario | String | 20      | Obrigatório     |
| indicadorRecemNato         | String | 1       | Obrigatório     |
| nomeBeneficiario           | String | 70      | Obrigatório     |
| nomeSocial                 | String | 70      | Condicionado    |

### Dados Clínicos (Exemplo: Quimioterapia)

| Campo                           | Tipo     | Tamanho | Obrigatoriedade |
| ------------------------------- | -------- | ------- | --------------- |
| pesoBeneficiario                | Numérico | 3,2     | Obrigatório     |
| alturaBeneficiario              | Numérico | 3,2     | Obrigatório     |
| superficieCorporal              | Numérico | 2,2     | Obrigatório     |
| idadeBeneficiario               | Integer  | 3       | Obrigatório     |
| sexoBeneficiario                | String   | 1       | Obrigatório     |
| nomeProfissionalSolicitante     | String   | 70      | Obrigatório     |
| telefoneProfissionalSolicitante | String   | 11      | Obrigatório     |
| emailProfissionalSolicitante    | String   | 60      | Condicionado    |
| dataDiagnostico                 | Date     | 8       | Condicionado    |
| diagnosticoPrincipal            | String   | 4       | Opcional        |
| diagnosticoSecundario           | String   | 4       | Opcional        |
| terceiroDiagnostico             | String   | 4       | Opcional        |
| quartoDiagnostico               | String   | 4       | Opcional        |
| estadiamentoTumor               | String   | 1       | Obrigatório     |
| finalidadeTratamento            | String   | 1       | Obrigatório     |
| escalaCapacidadeFuncional       | String   | 1       | Obrigatório     |

---

# Fluxo: Envio de Lote de Guias para Cobrança (loteGuias)

## Cabeçalho da Guia

| Campo                   | Tipo   | Tamanho | Formato    | Obrigatoriedade |
| ----------------------- | ------ | ------- | ---------- | --------------- |
| registroANS             | String | 6       | -          | Obrigatório     |
| numeroGuiaPrestador     | String | 20      | -          | Obrigatório     |
| dataAutorizacao         | Date   | 8       | AAAA-MM-DD | Obrigatório     |
| senha                   | String | 20      | -          | Obrigatório     |
| dataValidadeSenha       | Date   | 8       | AAAA-MM-DD | Condicionado    |
| ausenciaCodigoValidacao | String | 2       | -          | Condicionado    |
| codigoValidacao         | String | 10      | -          | Opcional        |

## Beneficiário

| Campo                         | Tipo    | Tamanho | Obrigatoriedade |
| ----------------------------- | ------- | ------- | --------------- |
| numeroCarteiraBeneficiario    | String  | 20      | Obrigatório     |
| indicadorRecemNato            | String  | 1       | Obrigatório     |
| tipoIdentificacaoBeneficiario | String  | 2       | Condicionado    |
| identificadorBeneficiario     | Binário | -       | Opcional        |

## Prestador Executante

| Campo                               | Tipo   | Tamanho | Obrigatoriedade |
| ----------------------------------- | ------ | ------- | --------------- |
| codigoContratadoExecutanteOperadora | String | 14      | Obrigatório     |
| codigoCNESExecutante                | String | 7       | Obrigatório     |
| nomeProfissionalExecutante          | String | 70      | Condicionado    |
| conselhoProfissional                | String | 2       | Obrigatório     |
| numeroConselho                      | String | 15      | Obrigatório     |
| ufConselho                          | String | 2       | Obrigatório     |
| codigoCBO                           | String | 6       | Obrigatório     |

## Dados de Faturamento

| Campo                 | Tipo   | Tamanho | Obrigatoriedade |
| --------------------- | ------ | ------- | --------------- |
| caraterAtendimento    | String | 1       | Obrigatório     |
| tipoFaturamento       | String | 1       | Obrigatório     |
| dataInicioFaturamento | Date   | 8       | Obrigatório     |
| horaInicioFaturamento | Time   | 8       | Obrigatório     |
| dataFimFaturamento    | Date   | 8       | Obrigatório     |
| horaFimFaturamento    | Time   | 8       | Obrigatório     |
| tipoInternacao        | String | 1       | Obrigatório     |

## Procedimentos Realizados

| Campo                 | Tipo    | Tamanho | Obrigatoriedade |
| --------------------- | ------- | ------- | --------------- |
| sequencialReferencia  | Integer | 4       | Condicionado    |
| dataRealizacao        | Date    | 8       | Condicionado    |
| horaInicial           | Time    | 8       | Condicionado    |
| horaFinal             | Time    | 8       | Condicionado    |
| tabelaReferencia      | String  | 2       | Condicionado    |
| codigoProcedimento    | String  | 10      | Condicionado    |
| descricaoProcedimento | String  | 150     | Condicionado    |
| quantidadeRealizada   | Integer | 3       | Condicionado    |
| viaAcesso             | String  | 1       | Condicionado    |
| tecnicaUtilizada      | String  | 1       | Condicionado    |

### Regra de Negócio

* O campo `sequencialReferencia` não pode se repetir dentro da mesma guia.
* A regra também se aplica aos itens do Anexo de Outras Despesas vinculados à guia.

## Valores Financeiros

| Campo                        | Tipo     | Tamanho | Obrigatoriedade |
| ---------------------------- | -------- | ------- | --------------- |
| fatorReducaoAcrescimo        | Numérico | 1,2     | Obrigatório     |
| valorUnitario                | Numérico | 8,2     | Obrigatório     |
| valorTotalProcedimento       | Numérico | 8,2     | Obrigatório     |
| centroConsumo                | String   | 2       | Condicionado    |
| grauParticipacaoProfissional | String   | 2       | Condicionado    |

---

# Modelo Conceitual Simplificado

```text
LoteAnexo
├── numeroProtocolo
├── dataEnvioLoteAnexos
├── numeroLote
├── registroANS
└── quantidadeAnexosClinicos

AnexoClinico
├── numeroGuiaPrestador
├── dadosBeneficiario
└── dadosClinicos

GuiaCobranca
├── dadosAutorizacao
├── beneficiario
├── prestadorExecutante
├── faturamento
└── procedimentosRealizados

ProcedimentoRealizado
├── sequencialReferencia
├── codigoProcedimento
├── quantidadeRealizada
├── valorUnitario
└── valorTotalProcedimento
```

## Relacionamentos

```text
LoteAnexo
 └── 1:N AnexoClinico

GuiaCobranca
 ├── 1:1 Beneficiario
 ├── 1:1 PrestadorExecutante
 ├── 1:1 Faturamento
 └── 1:N ProcedimentoRealizado
```
