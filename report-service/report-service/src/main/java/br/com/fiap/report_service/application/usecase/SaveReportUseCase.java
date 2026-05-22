package br.com.fiap.report_service.application.usecase;

import br.com.fiap.report_service.domain.model.Report;
import br.com.fiap.report_service.domain.port.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SaveReportUseCase {

    private static final Logger log = LoggerFactory.getLogger(SaveReportUseCase.class);
    private final ReportRepository repository;

    public SaveReportUseCase(ReportRepository repository) {
        this.repository = repository;
    }

    public Report execute(Report report) {
        log.info("Salvando relatório - analysisId: {}", report.getAnalysisId());
        Report saved = repository.save(report);
        log.info("Relatório salvo com sucesso - id: {}", saved.getId());
        return saved;
    }
}