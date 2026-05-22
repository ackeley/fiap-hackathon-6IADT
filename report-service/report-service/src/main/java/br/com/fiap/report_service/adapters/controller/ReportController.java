package br.com.fiap.report_service.adapters.controller;

import br.com.fiap.report_service.adapters.dto.ReportResponseDTO;
import br.com.fiap.report_service.domain.port.ReportRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportRepository repository;

    public ReportController(ReportRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{analysisId}")
    public ResponseEntity<ReportResponseDTO> getByAnalysisId(@PathVariable String analysisId) {
        return repository.findByAnalysisId(analysisId)
                .map(report -> ResponseEntity.ok(new ReportResponseDTO(
                        report.getId(),
                        report.getAnalysisId(),
                        report.getComponents(),
                        report.getRisks(),
                        report.getRecommendations(),
                        report.getSummary(),
                        report.getCreatedAt()
                )))
                .orElse(ResponseEntity.notFound().build());
    }
}