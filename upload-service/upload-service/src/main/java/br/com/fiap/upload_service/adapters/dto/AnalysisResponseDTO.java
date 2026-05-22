package br.com.fiap.upload_service.adapters.dto;

import br.com.fiap.upload_service.domain.entity.AnalysisStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class AnalysisResponseDTO {
    private UUID id;
    private String fileName;
    private String fileType;
    private AnalysisStatus status;
    private LocalDateTime createdAt;

    public AnalysisResponseDTO(UUID id, String fileName, String fileType,
                               AnalysisStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public String getFileName() { return fileName; }
    public String getFileType() { return fileType; }
    public AnalysisStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
