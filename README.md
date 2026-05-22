# FIAP Secure Systems — Analisador de Diagramas de Arquitetura

Sistema de análise automatizada de diagramas de arquitetura utilizando IA (Gemini 2.5 Flash), desenvolvido para o Hackathon FIAP PosTech — SOAT + IADT.

## Problema

Empresas que operam sistemas distribuídos possuem dezenas de diagramas de arquitetura analisados manualmente, demandando muito tempo e dependendo de especialistas. Este sistema automatiza essa análise usando Inteligência Artificial.

## Arquitetura

Cliente -> API Gateway (8080) -> Upload Service (8081) -> PostgreSQL
-> Kafka (diagram-analysis)
-> Processing Service (8082) -> Gemini 2.5 Flash
-> Kafka (diagram-report)
-> Report Service (8083) -> MongoDB

## Microsserviços

- api-gateway (8080): Porta de entrada única
- upload-service (8081): Recebe e registra diagramas — PostgreSQL
- processing-service (8082): Analisa com IA Gemini
- report-service (8083): Armazena e serve relatórios — MongoDB

## Padrões aplicados

- Clean Architecture em todos os serviços
- Comunicação síncrona via REST
- Comunicação assíncrona via Apache Kafka
- Cada serviço com banco de dados próprio

## Tecnologias utilizadas

- Java 21
- Spring Boot 4.0.6
- PostgreSQL 16
- Apache Kafka 7.5.0
- MongoDB 7
- Google Gemini 2.5 Flash
- Docker / Docker Compose

## Inteligência Artificial

Utiliza o Google Gemini 2.5 Flash para análise dos diagramas.

Pipeline de IA:
1. Processing Service consome evento do Kafka
2. Monta prompt estruturado com metadados do diagrama
3. Chama API do Gemini com guardrails de formato JSON
4. Parseia e valida a resposta
5. Em caso de falha usa relatório fallback
6. Publica resultado no Kafka

Guardrails implementados:
- Prompt com formato de resposta obrigatório em JSON
- Remoção de markdown da resposta
- Tratamento de exceções com fallback
- Log de erros para rastreabilidade

## Como executar

### Pré-requisitos
- Docker e Docker Compose instalados
- Chave de API do Google Gemini (aistudio.google.com)

### 1. Clone o repositório
git clone <url-do-repositorio>
cd hackathon-fiap

### 2. Configure as variáveis de ambiente
cp .env.example .env

Edite o .env e adicione sua chave:
GEMINI_API_KEY=sua-chave-aqui

### 3. Suba o sistema completo
docker compose up --build

Aguarde todos os containers subirem (cerca de 60 segundos).

### 4. Verifique os containers
docker ps

Deve aparecer 8 containers rodando:
- upload-db — PostgreSQL
- report-mongodb — MongoDB
- zookeeper — Kafka coordinator
- kafka — Message broker
- upload-service — porta 8081
- processing-service — porta 8082
- report-service — porta 8083
- api-gateway — porta 8080

## Endpoints

### Upload de diagrama
POST http://localhost:8080/analyses

curl -X POST http://localhost:8080/analyses -F "file=@diagrama.png"

Resposta 201:
{
"id": "uuid",
"fileName": "diagrama.png",
"fileType": "IMAGE",
"status": "RECEIVED",
"createdAt": "2026-05-22T01:00:00"
}

### Consulta de relatório
GET http://localhost:8080/reports/{analysisId}

curl http://localhost:8080/reports/uuid-aqui

Resposta 200:
{
"id": "mongo-id",
"analysisId": "uuid",
"components": ["API Gateway", "Load Balancer"],
"risks": ["Ausência de circuit breaker"],
"recommendations": ["Implementar Resilience4j"],
"summary": "Resumo da análise",
"createdAt": "2026-05-22T01:00:00"
}

### Status possíveis
- RECEIVED: Arquivo recebido
- PROCESSING: Sendo analisado pela IA
- ANALYZED: Análise concluída
- ERROR: Falha no processamento

### Health Check
curl http://localhost:8080/actuator/health

## Segurança

- Chaves de API gerenciadas via variáveis de ambiente
- Arquivo .env no .gitignore — nunca vai ao repositório
- Guardrails na IA: formato obrigatório, fallback em caso de erro
- Validação de tipo de arquivo no upload
- Comunicação entre serviços via rede interna Docker

## Parando o sistema

docker compose down

Para remover volumes:
docker compose down -v

## Estrutura do projeto
```
hackathon-fiap/
├── docker-compose.yml
├── .env.example
├── .gitignore
├── README.md
├── upload-service/
├── processing-service/
├── report-service/
└── api-gateway/
```
## Equipe

Hackathon FIAP PosTech — SOAT + IADT

- Ackeley        | RM 366072 | ackeley@hotmail.com
- Eduardo        | RM 366322 | eduardoagarbella@gmail.com
- Leandro        | RM 365755 | leandropsouza@gmail.com
- Thyago         | RM 365858 | thyagoborgescarvalho@gmail.com
