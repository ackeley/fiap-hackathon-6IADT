package br.com.fiap.processing_service.infrastructure.messaging;

import br.com.fiap.processing_service.application.usecase.ProcessAnalysisUseCase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AnalysisEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(AnalysisEventConsumer.class);
    private final ProcessAnalysisUseCase processAnalysisUseCase;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AnalysisEventConsumer(ProcessAnalysisUseCase processAnalysisUseCase) {
        this.processAnalysisUseCase = processAnalysisUseCase;
    }

    @KafkaListener(topics = "diagram-analysis", groupId = "processing-group")
    public void consume(String message) {
        log.info("Mensagem recebida do Kafka: {}", message);
        try {
            JsonNode json = objectMapper.readTree(message);
            String analysisId = json.get("analysisId").asText();
            String fileName = json.get("fileName").asText();
            String fileType = json.get("fileType").asText();

            processAnalysisUseCase.execute(analysisId, fileName, fileType);
        } catch (Exception e) {
            log.error("Erro ao processar mensagem: {}", e.getMessage());
        }
    }
}