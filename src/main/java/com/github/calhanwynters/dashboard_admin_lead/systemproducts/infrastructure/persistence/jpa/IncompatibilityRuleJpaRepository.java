package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.jpa;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.IncompatibilityRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncompatibilityRuleJpaRepository extends JpaRepository<IncompatibilityRuleEntity, Long> {
    List<IncompatibilityRuleEntity> findAllByActiveTrue();
}