package br.com.fiap.processing_service.infrastructure.ai;

import br.com.fiap.processing_service.domain.model.AnalysisReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MockAiAnalysisService implements AiAnalysisService {
    private static final Logger log = LoggerFactory.getLogger(MockAiAnalysisService.class);

    @Override
    public AnalysisReport analyze(String analysisId, String fileName, String fileType) {
        log.info("Analisando diagrama com IA Mock - analysisId: {}", analysisId);

        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        return new AnalysisReport(
                analysisId,
                List.of(
                        "API Gateway",
                        "Serviço de Autenticação",
                        "Banco de Dados PostgreSQL",
                        "Cache Redis",
                        "Fila de Mensagens Kafka"
                ),
                List.of(
                        "Ausência de circuit breaker entre serviços",
                        "Banco de dados sem replicação configurada",
                        "Falta de rate limiting no API Gateway"
                ),
                List.of(
                        "Implementar circuit breaker com Resilience4j",
                        "Configurar réplica de leitura no PostgreSQL",
                        "Adicionar rate limiting e autenticação JWT no Gateway",
                        "Implementar health checks em todos os serviços"
                ),
                "Arquitetura de microsserviços identificada com 5 componentes principais. " +
                        "Foram encontrados 3 riscos arquiteturais que requerem atenção."
        );
    }
}
