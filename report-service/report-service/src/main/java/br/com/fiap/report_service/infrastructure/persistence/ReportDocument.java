
package br.com.fiap.report_service.infrastructure.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "reports")
public class ReportDocument {
    @Id
    private String id;
    private String analysisId;
    private List<String> components;
    private List<String> risks;
    private List<String> recommendations;
    private String summary;
    private LocalDateTime createdAt;

    public ReportDocument() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    public List<String> getComponents() { return components; }
    public void setComponents(List<String> components) { this.components = components; }
    public List<String> getRisks() { return risks; }
    public void setRisks(List<String> risks) { this.risks = risks; }
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}