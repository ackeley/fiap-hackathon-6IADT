package br.com.fiap.upload_service.infrastructure.persistence.repository;

import br.com.fiap.upload_service.infrastructure.persistence.entity.AnalysisJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AnalysisJpaRepository extends JpaRepository<AnalysisJpaEntity, UUID> {
}
