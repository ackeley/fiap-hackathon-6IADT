# Upload Service — Hackathon FIAP

Microsserviço responsável por receber diagramas de arquitetura (imagem ou PDF),
persistir no banco de dados e publicar eventos no Apache Kafka para processamento assíncrono.

## Tecnologias

- Java 21 -  	<img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg" width="30"/>
- Spring Boot 4.0.6   <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg" width="30"/>
- PostgreSQL 16   <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/postgresql/postgresql-original.svg" width="30"/>
- Apache Kafka 7.5.0   <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/apachekafka/apachekafka-original.svg" width="30"/>
- Docker / Docker Compose   <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/docker/docker-original.svg" width="30"/>

## Pré-requisitos

- Java 21 instalado
- Docker e Docker Compose instalados
- Maven (o projeto já inclui o wrapper `./mvnw`)

## Como executar

### 1. Clone o repositório

```bash
git clone 
cd upload-service
```

### 2. Suba a infraestrutura (banco + Kafka)

```bash
docker compose up -d
```

Aguarde cerca de 20 segundos para o Kafka inicializar completamente.

Confirme que os 3 containers estão rodando:

```bash
docker ps
```

Você deve ver:
- `upload-db` — PostgreSQL na porta 5433
- `zookeeper` — na porta 2181
- `kafka` — na porta 9092

### 3. Configure as variáveis de ambiente

O arquivo `src/main/resources/application.properties` já vem configurado para o ambiente local.
Não é necessário nenhuma alteração para rodar localmente.

### 4. Execute a aplicação

Pelo Maven:
```bash
./mvnw spring-boot:run
```

Ou pelo IntelliJ: clique no botão ▶ na classe `UploadServiceApplication`.

A aplicação sobe na porta **8081**.

## Endpoints

### POST /analyses
Recebe um diagrama para análise.

**Request:**
```bash
curl -X POST http://localhost:8081/analyses \
  -F "file=@/caminho/para/diagrama.png"
```

**Response (201 Created):**
```json
{
  "id": "04b9a19c-a7d6-428e-9d33-8f663d756926",
  "fileName": "diagrama.png",
  "fileType": "IMAGE",
  "status": "RECEIVED",
  "createdAt": "2026-05-11T08:00:00"
}
```

**Tipos de arquivo suportados:**
- Imagens: `image/png`, `image/jpeg`, `image/gif`
- Documentos: `application/pdf`

---

### GET /analyses/{id}
Consulta o status de uma análise.

**Request:**
```bash
curl http://localhost:8081/analyses/04b9a19c-a7d6-428e-9d33-8f663d756926
```

**Response (200 OK):**
```json
{
  "id": "04b9a19c-a7d6-428e-9d33-8f663d756926",
  "fileName": "diagrama.png",
  "fileType": "IMAGE",
  "status": "RECEIVED",
  "createdAt": "2026-05-11T08:00:00"
}
```

**Status possíveis:**
| Status | Descrição |
|--------|-----------|
| `RECEIVED` | Arquivo recebido e aguardando processamento |
| `PROCESSING` | Sendo analisado pela IA |
| `ANALYZED` | Análise concluída com sucesso |
| `ERROR` | Falha durante o processamento |

---

### GET /actuator/health
Verifica se o serviço está saudável.

```bash
curl http://localhost:8081/actuator/health
```

**Response:**
```json
{"status": "UP"}
```

## Arquitetura

O serviço segue os princípios da **Clean Architecture**:

```src/main/java/br/com/fiap/upload_service/
├── domain/
│   ├── entity/         # Entidades de domínio (Analysis, AnalysisStatus)
│   └── port/           # Interfaces/contratos (AnalysisRepository)
├── application/
│   └── usecase/        # Casos de uso (CreateAnalysisUseCase)
├── infrastructure/
│   ├── persistence/    # JPA entities e repositórios
│   └── messaging/      # Kafka producer
└── adapters/
├── controller/     # REST controllers
└── dto/            # Objetos de transferência de dados
```

## Parando o ambiente

```bash
docker compose down
```

Para remover os volumes (apaga os dados):
```bash
docker compose down -v
```
