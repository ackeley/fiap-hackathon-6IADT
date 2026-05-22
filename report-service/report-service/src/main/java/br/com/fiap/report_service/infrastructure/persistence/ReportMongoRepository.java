package br.com.fiap.report_service.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ReportMongoRepository extends MongoRepository<ReportDocument, String> {
    Optional<ReportDocument> findByAnalysisId(String analysisId);
}