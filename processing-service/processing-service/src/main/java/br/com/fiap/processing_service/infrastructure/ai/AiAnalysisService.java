package br.com.fiap.processing_service.infrastructure.ai;

import br.com.fiap.processing_service.domain.model.AnalysisReport;

public interface AiAnalysisService {
    AnalysisReport analyze(String analysisId, String fileName, String fileType);
}
