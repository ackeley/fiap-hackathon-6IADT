package br.com.fiap.processing_service.domain.model;

import java.util.List;

public class AnalysisReport {
    private String analysisId;
    private List<String> components;
    private List<String> risks;
    private List<String> recommendations;
    private String summary;

    public AnalysisReport(String analysisId, List<String> components,
                          List<String> risks, List<String> recommendations,
                          String summary) {
        this.analysisId = analysisId;
        this.components = components;
        this.risks = risks;
        this.recommendations = recommendations;
        this.summary = summary;
    }

    public String getAnalysisId() { return analysisId; }
    public List<String> getComponents() { return components; }
    public List<String> getRisks() { return risks; }
    public List<String> getRecommendations() { return recommendations; }
    public String getSummary() { return summary; }
}

