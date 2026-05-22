package br.com.fiap.report_service.infrastructure.messaging;

import br.com.fiap.report_service.application.usecase.SaveReportUseCase;
import br.com.fiap.report_service.domain.model.Report;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReportEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ReportEventConsumer.class);
    private final SaveReportUseCase saveReportUseCase;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ReportEventConsumer(SaveReportUseCase saveReportUseCase) {
        this.saveReportUseCase = saveReportUseCase;
    }

    @KafkaListener(topics = "diagram-report", groupId = "report-group")
    public void consume(String message) {
        log.info("Relatório recebido do Kafka: {}", message);
        try {
            JsonNode json = objectMapper.readTree(message);

            String analysisId = json.get("analysisId").asText();
            List<String> components = parseList(json.get("components"));
            List<String> risks = parseList(json.get("risks"));
            List<String> recommendations = parseList(json.get("recommendations"));
            String summary = json.get("summary").asText();

            Report report = new Report(analysisId, components, risks, recommendations, summary);
            saveReportUseCase.execute(report);
        } catch (Exception e) {
            log.error("Erro ao processar relatório: {}", e.getMessage());
        }
    }

    private List<String> parseList(JsonNode node) {
        List<String> list = new ArrayList<>();
        if (node != null && node.isArray()) {
            node.forEach(item -> list.add(item.asText()));
        }
        return list;
    }
}