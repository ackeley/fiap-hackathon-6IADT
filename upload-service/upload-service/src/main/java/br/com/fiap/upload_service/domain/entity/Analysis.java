package br.com.fiap.upload_service.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;
public class Analysis {

    private UUID id;
    private String fileName;
    private String fileType;
    private AnalysisStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Construtor para criar nova análise
    public Analysis(String fileName, String fileType) {
        this.id = UUID.randomUUID();
        this.fileName = fileName;
        this.fileType = fileType;
        this.status = AnalysisStatus.RECEIVED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Construtor para reconstruir do banco ← NOVO
    public Analysis(UUID id, String fileName, String fileType,
                    AnalysisStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters e updateStatus iguais ao anterior
    public UUID getId() { return id; }
    public String getFileName() { return fileName; }
    public String getFileType() { return fileType; }
    public AnalysisStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void updateStatus(AnalysisStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
}
