# FIAP Secure Systems — Analisador de Diagramas de Arquitetura

Sistema de análise automatizada de diagramas de arquitetura utilizando IA (Google Gemini 2.5 Flash), desenvolvido para o Hackathon FIAP PosTech — SOAT + IADT.

## Problema

Empresas que operam sistemas distribuídos possuem dezenas de diagramas de arquitetura analisados manualmente, demandando muito tempo e dependendo de especialistas. Este sistema automatiza essa análise usando Inteligência Artificial, gerando relatórios técnicos com componentes identificados, riscos arquiteturais e recomendações.

## Solução

MVP de um sistema back-end capaz de:
- Receber diagramas de arquitetura (Imagem ou PDF)
- Processar e analisar com IA (Google Gemini 2.5 Flash)
- Gerar relatório técnico estruturado
- Consultar status do processamento em tempo real
- Interface web para upload e visualização dos resultados

## Arquitetura

O sistema é composto por 4 microsserviços que se comunicam via REST e Apache Kafka:

  Cliente (Browser)
       |
  API Gateway :8080
       |
  Upload Service :8081 --> PostgreSQL
       |
  Kafka (diagram-analysis)
       |
  Processing Service :8082 --> Google Gemini 2.5 Flash
       |
  Kafka (diagram-report)
       |
  Report Service :8083 --> MongoDB

## Microsserviços

  api-gateway        | 8080 | Porta de entrada única e interface web
  upload-service     | 8081 | Recebe e registra diagramas          | PostgreSQL
  processing-service | 8082 | Analisa com IA Gemini                | -
  report-service     | 8083 | Armazena e serve relatórios          | MongoDB

## Padrões de Arquitetura

- Clean Architecture em todos os serviços
- Comunicação síncrona via REST
- Comunicação assíncrona via Apache Kafka
- Cada serviço com banco de dados próprio (polyglot persistence)
- Fallback em caso de falha da IA

## Tecnologias Utilizadas

- Java 21
- Spring Boot 4.0.6
- Apache Kafka 7.5.0 (Confluent)
- PostgreSQL 16
- MongoDB 7
- Google Gemini 2.5 Flash
- Docker / Docker Compose
- OkHttp (cliente HTTP para IA)
- Spring Data JPA
- Spring Data MongoDB
- Lombok

## Inteligência Artificial

Utiliza o Google Gemini 2.5 Flash para análise dos diagramas.

Pipeline de IA:
  1. Processing Service consome evento do tópico diagram-analysis no Kafka
  2. Monta prompt estruturado com metadados do diagrama
  3. Chama API do Gemini exigindo resposta em formato JSON
  4. Parseia e valida a resposta removendo possíveis markdowns
  5. Em caso de falha usa relatório fallback automaticamente
  6. Publica resultado no tópico diagram-report no Kafka

Guardrails implementados:
  - Prompt com formato de resposta obrigatório em JSON
  - Remoção de markdown da resposta da IA
  - Tratamento de exceções com fallback estruturado
  - Timeout e retry via polling no frontend
  - Log de erros para rastreabilidade

Justificativa da abordagem:
  Optamos pelo uso de LLM (Gemini 2.5 Flash) com prompt engineering
  para análise textual baseada no nome e tipo do arquivo, com
  validação de formato de saída e fallback em caso de alucinações
  ou falhas na API.

Limitações conhecidas:
  - A análise é baseada no nome do arquivo, não no conteúdo visual
  - Sujeito a limites de quota da API gratuita do Gemini
  - Análise de imagens reais requer upgrade para multimodal

## Como Executar

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

### 4. Acesse a interface web
  http://localhost:8080

### 5. Verifique os containers
  docker ps

  Deve aparecer 8 containers:
  - upload-db        PostgreSQL na porta 5433
  - report-mongodb   MongoDB na porta 27017
  - zookeeper        Kafka coordinator na porta 2181
  - kafka            Message broker na porta 9092
  - upload-service   porta 8081
  - processing-service porta 8082
  - report-service   porta 8083
  - api-gateway      porta 8080

## Endpoints

### Upload de diagrama
 
   POST http://localhost:8080/analyses
  Content-Type: multipart/form-data

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
    "components": ["API Gateway", "Load Balancer", "..."],
    "risks": ["Ausência de circuit breaker", "..."],
    "recommendations": ["Implementar Resilience4j", "..."],
    "summary": "Resumo da análise arquitetural",
    "createdAt": "2026-05-22T01:00:00"
  }

### Status possíveis
  RECEIVED    Arquivo recebido e aguardando processamento
  PROCESSING  Sendo analisado pela IA
  ANALYZED    Análise concluída com sucesso
  ERROR       Falha durante o processamento

### Health Check
  curl http://localhost:8080/actuator/health


## Interface Web

Acesse http://localhost:8080 no navegador para usar a interface gráfica.

Funcionalidades:
  - Arraste ou selecione um diagrama (PNG, JPG ou PDF ate 10MB)
  - Clique em ANALISAR DIAGRAMA
  - Acompanhe o progresso em tempo real pelas 4 etapas:
    01 UPLOAD    -> arquivo enviado ao sistema
    02 KAFKA     -> evento publicado na fila
    03 IA        -> Gemini 2.5 Flash analisando
    04 RELATORIO -> resultado disponivel
  - Visualize o relatorio com componentes, riscos e recomendacoes

Exemplo de uso via curl:
  1. Envie um diagrama:
     curl -X POST http://localhost:8080/analyses -F "file=@meu-diagrama.png"

  2. Copie o id da resposta e consulte o relatorio apos alguns segundos:
     curl http://localhost:8080/reports/{id}
     
## Segurança

- Chaves de API gerenciadas exclusivamente via variáveis de ambiente
- Arquivo .env no .gitignore — nunca vai ao repositório Git
- Arquivo .env.example documenta as variáveis necessárias sem expor valores
- Guardrails na IA: formato obrigatório, fallback em caso de erro ou alucinação
- Validação de tipo de arquivo no upload (IMAGE ou PDF)
- Comunicação entre serviços via rede interna Docker (não exposta)
- Logs estruturados para auditoria e rastreabilidade

Riscos e limitações de segurança identificados:
- Ausência de autenticação/autorização nos endpoints (JWT não implementado no MVP)
- Sem rate limiting no API Gateway
- Sem validação do conteúdo do arquivo (apenas tipo MIME)
- Chave Gemini exposta em variável de ambiente do container

## Parando o Sistema

  docker compose down

  Para remover volumes (apaga todos os dados):
  docker compose down -v

## Estrutura do Projeto
```
  hackathon-fiap/
  |-- docker-compose.yml
  |-- .env.example
  |-- .gitignore
  |-- README.md
  |-- upload-service/
  |   |-- Dockerfile
  |   |-- pom.xml
  |   └-- src/
  |-- processing-service/
  |   |-- Dockerfile
  |   |-- pom.xml
  |   └-- src/
  |-- report-service/
  |   |-- Dockerfile
  |   |-- pom.xml
  |   └-- src/
  └-- api-gateway/
      |-- Dockerfile
      |-- pom.xml
      └-- src/
```
## Equipe

Hackathon FIAP PosTech — SOAT + IADT — 2026
```
  Ackeley   | RM 366072 | ackeley@hotmail.com
  Eduardo   | RM 366322 | eduardoagarbella@gmail.com
  Leandro   | RM 365755 | leandropsouza@gmail.com
  Thyago    | RM 365858 | thyagoborgescarvalho@gmail.com
```
