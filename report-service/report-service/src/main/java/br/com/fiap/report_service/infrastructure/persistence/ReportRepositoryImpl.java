package br.com.fiap.report_service.infrastructure.persistence;

import br.com.fiap.report_service.domain.model.Report;
import br.com.fiap.report_service.domain.port.ReportRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Primary
@Component
public class ReportRepositoryImpl implements ReportRepository {

    private final ReportMongoRepository mongoRepository;

    public ReportRepositoryImpl(ReportMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Report save(Report report) {
        ReportDocument doc = toDocument(report);
        ReportDocument saved = mongoRepository.save(doc);
        return toDomain(saved);
    }

    @Override
    public Optional<Report> findByAnalysisId(String analysisId) {
        return mongoRepository.findByAnalysisId(analysisId).map(this::toDomain);
    }

    private ReportDocument toDocument(Report report) {
        ReportDocument doc = new ReportDocument();
        doc.setAnalysisId(report.getAnalysisId());
        doc.setComponents(report.getComponents());
        doc.setRisks(report.getRisks());
        doc.setRecommendations(report.getRecommendations());
        doc.setSummary(report.getSummary());
        doc.setCreatedAt(report.getCreatedAt());
        return doc;
    }

    private Report toDomain(ReportDocument doc) {
        Report report = new Report(
                doc.getAnalysisId(),
                doc.getComponents(),
                doc.getRisks(),
                doc.getRecommendations(),
                doc.getSummary()
        );
        report.setId(doc.getId());
        return report;
    }
}