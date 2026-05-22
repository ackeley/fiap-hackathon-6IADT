package br.com.fiap.report_service.adapters.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ReportResponseDTO {
    private String id;
    private String analysisId;
    private List<String> components;
    private List<String> risks;
    private List<String> recommendations;
    private String summary;
    private LocalDateTime createdAt;

    public ReportResponseDTO(String id, String analysisId, List<String> components,
                             List<String> risks, List<String> recommendations,
                             String summary, LocalDateTime createdAt) {
        this.id = id;
        this.analysisId = analysisId;
        this.components = components;
        this.risks = risks;
        this.recommendations = recommendations;
        this.summary = summary;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getAnalysisId() { return analysisId; }
    public List<String> getComponents() { return components; }
    public List<String> getRisks() { return risks; }
    public List<String> getRecommendations() { return recommendations; }
    public String getSummary() { return summary; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}