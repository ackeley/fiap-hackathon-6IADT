package br.com.fiap.processing_service.infrastructure.messaging;

import br.com.fiap.processing_service.domain.model.AnalysisReport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReportEventProducer {

    private static final Logger log = LoggerFactory.getLogger(ReportEventProducer.class);
    private static final String TOPIC = "diagram-report";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ReportEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendReportEvent(AnalysisReport report) {
        try {
            String message = objectMapper.writeValueAsString(report);
            kafkaTemplate.send(TOPIC, report.getAnalysisId(), message);
            log.info("Relatório publicado no Kafka - topic: {} | analysisId: {}",
                    TOPIC, report.getAnalysisId());
        } catch (Exception e) {
            log.error("Erro ao publicar relatório: {}", e.getMessage());
        }
    }
}