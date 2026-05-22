package br.com.fiap.report_service.domain.port;

import br.com.fiap.report_service.domain.model.Report;
import java.util.Optional;

public interface ReportRepository {
    Report save(Report report);
    Optional<Report> findByAnalysisId(String analysisId);
}