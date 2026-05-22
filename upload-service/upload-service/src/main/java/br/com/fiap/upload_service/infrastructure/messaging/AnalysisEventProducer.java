package br.com.fiap.upload_service.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AnalysisEventProducer {
    private static final Logger log = LoggerFactory.getLogger(AnalysisEventProducer.class);
    private static final String TOPIC = "diagram-analysis";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public AnalysisEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAnalysisEvent(String analysisId, String fileName, String fileType) {
        // Monta a mensagem em formato JSON simples
        String message = String.format(
                "{\"analysisId\":\"%s\",\"fileName\":\"%s\",\"fileType\":\"%s\"}",
                analysisId, fileName, fileType
        );

        kafkaTemplate.send(TOPIC, analysisId, message);
        log.info("Evento publicado no Kafka - topic: {} | analysisId: {}", TOPIC, analysisId);
    }
}
