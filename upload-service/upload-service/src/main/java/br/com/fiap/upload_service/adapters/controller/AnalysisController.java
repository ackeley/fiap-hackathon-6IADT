package br.com.fiap.upload_service.adapters.controller;
import br.com.fiap.upload_service.adapters.dto.AnalysisResponseDTO;
import br.com.fiap.upload_service.application.usecase.CreateAnalysisUseCase;
import br.com.fiap.upload_service.domain.entity.Analysis;
import br.com.fiap.upload_service.domain.port.AnalysisRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/analyses")
public class AnalysisController {
    private final CreateAnalysisUseCase createAnalysisUseCase;
    private final AnalysisRepository repository;

    public AnalysisController(CreateAnalysisUseCase createAnalysisUseCase,
                              AnalysisRepository repository) {
        this.createAnalysisUseCase = createAnalysisUseCase;
        this.repository = repository;
    }

    // POST /analyses — recebe o arquivo
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<AnalysisResponseDTO> create(
            @RequestParam("file") MultipartFile file) {

        // Detecta se é PDF ou imagem
        String contentType = file.getContentType();
        String fileType = detectFileType(contentType);

        // Executa o caso de uso
        Analysis analysis = createAnalysisUseCase.execute(
                file.getOriginalFilename(),
                fileType
        );

        // Monta a resposta
        AnalysisResponseDTO response = new AnalysisResponseDTO(
                analysis.getId(),
                analysis.getFileName(),
                analysis.getFileType(),
                analysis.getStatus(),
                analysis.getCreatedAt()
        );

        return ResponseEntity.status(201).body(response);
    }

    // GET /analyses/{id} — consulta o status
    @GetMapping("/{id}")
    public ResponseEntity<AnalysisResponseDTO> getById(@PathVariable UUID id) {
        return repository.findById(id)
                .map(analysis -> ResponseEntity.ok(new AnalysisResponseDTO(
                        analysis.getId(),
                        analysis.getFileName(),
                        analysis.getFileType(),
                        analysis.getStatus(),
                        analysis.getCreatedAt()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    private String detectFileType(String contentType) {
        if (contentType == null) return "IMAGE";
        if (contentType.equals("application/pdf")) return "PDF";
        if (contentType.startsWith("image/")) return "IMAGE";
        throw new IllegalArgumentException("Tipo de arquivo não suportado: " + contentType);
    }
}
