package br.com.fiap.upload_service.domain.entity;

public enum AnalysisStatus {
    RECEIVED,       // Arquivo recebido
    PROCESSING,     // Sendo analisado pela IA
    ANALYZED,       // Análise concluída
    ERROR           // Falha no processamento
}
