package br.com.fiap.upload_service.infrastructure.persistence.repository;

import br.com.fiap.upload_service.domain.entity.Analysis;
import br.com.fiap.upload_service.domain.entity.AnalysisStatus;
import br.com.fiap.upload_service.domain.port.AnalysisRepository;
import br.com.fiap.upload_service.infrastructure.persistence.entity.AnalysisJpaEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Primary;

@Primary
@Component
public class AnalysisRepositoryImpl implements AnalysisRepository  {
    private final AnalysisJpaRepository jpaRepository;

    public AnalysisRepositoryImpl(AnalysisJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public Analysis save(Analysis analysis) {
        AnalysisJpaEntity entity = toJpaEntity(analysis);
        AnalysisJpaEntity saved = jpaRepository.saveAndFlush(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Analysis> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    // Domínio → JPA
    private AnalysisJpaEntity toJpaEntity(Analysis analysis) {
        AnalysisJpaEntity entity = new AnalysisJpaEntity();
        entity.setId(analysis.getId());
        entity.setFileName(analysis.getFileName());
        entity.setFileType(analysis.getFileType());
        entity.setStatus(analysis.getStatus());
        entity.setCreatedAt(analysis.getCreatedAt());
        entity.setUpdatedAt(analysis.getUpdatedAt());
        return entity;
    }

    // JPA → Domínio
    private Analysis toDomain(AnalysisJpaEntity entity) {
        return new Analysis(
                entity.getId(),
                entity.getFileName(),
                entity.getFileType(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
