package br.com.fiap.upload_service.application.usecase;

import br.com.fiap.upload_service.domain.entity.Analysis;
import br.com.fiap.upload_service.domain.port.AnalysisRepository;
import br.com.fiap.upload_service.infrastructure.messaging.AnalysisEventProducer;
import org.springframework.stereotype.Service;

@Service
public class CreateAnalysisUseCase {
    private final AnalysisRepository repository;
    private final AnalysisEventProducer producer;

    public CreateAnalysisUseCase(AnalysisRepository repository, AnalysisEventProducer producer) {
        this.repository = repository;
        this.producer = producer;
    }

    public Analysis execute(String fileName, String fileType) {
        if (!fileType.equals("IMAGE") && !fileType.equals("PDF")) {
            throw new IllegalArgumentException("Tipo de arquivo inválido.");
        }

        // Salva no banco
        Analysis analysis = new Analysis(fileName, fileType);
        Analysis saved = repository.save(analysis);

        // Publica evento no Kafka
        producer.sendAnalysisEvent(
                saved.getId().toString(),
                saved.getFileName(),
                saved.getFileType()
        );

        return saved;
    }
}
