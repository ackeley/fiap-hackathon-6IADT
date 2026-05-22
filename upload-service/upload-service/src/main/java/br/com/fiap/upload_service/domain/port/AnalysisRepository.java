package br.com.fiap.upload_service.domain.port;

import br.com.fiap.upload_service.domain.entity.Analysis;
import java.util.Optional;
import java.util.UUID;

public interface AnalysisRepository {
    Analysis save(Analysis analysis);
    Optional<Analysis> findById(UUID id);
}