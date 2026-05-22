package br.com.fiap.processing_service.application.usecase;

import br.com.fiap.processing_service.domain.model.AnalysisReport;
import br.com.fiap.processing_service.infrastructure.ai.AiAnalysisService;
import br.com.fiap.processing_service.infrastructure.messaging.ReportEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProcessAnalysisUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessAnalysisUseCase.class);
    private final AiAnalysisService aiAnalysisService;
    private final ReportEventProducer reportEventProducer;

    public ProcessAnalysisUseCase(AiAnalysisService aiAnalysisService,
                                  ReportEventProducer reportEventProducer) {
        this.aiAnalysisService = aiAnalysisService;
        this.reportEventProducer = reportEventProducer;
    }

    public void execute(String analysisId, String fileName, String fileType) {
        log.info("Iniciando processamento - analysisId: {}", analysisId);
        try {
            AnalysisReport report = aiAnalysisService.analyze(analysisId, fileName, fileType);
            reportEventProducer.sendReportEvent(report);
            log.info("Processamento concluído - analysisId: {}", analysisId);
        } catch (Exception e) {
            log.error("Erro no processamento - analysisId: {} | erro: {}", analysisId, e.getMessage());
        }
    }
}