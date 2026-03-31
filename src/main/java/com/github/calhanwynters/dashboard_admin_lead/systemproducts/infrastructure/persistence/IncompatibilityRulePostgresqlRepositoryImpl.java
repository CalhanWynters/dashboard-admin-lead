package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence;

import com.github.calhanwynters.dashboard_admin_lead.common.Label;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.IncompatibilityRule;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.IncompatibilityRuleRepository;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.jpa.IncompatibilityRuleJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class IncompatibilityRulePostgresqlRepositoryImpl implements IncompatibilityRuleRepository {

    private final IncompatibilityRuleJpaRepository jpaRepository;

    public IncompatibilityRulePostgresqlRepositoryImpl(IncompatibilityRuleJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Set<IncompatibilityRule> findAllActiveRules() {
        return jpaRepository.findAllByActiveTrue().stream()
                .map(entity -> new IncompatibilityRule(
                        // If triggerUuid is null, this rule is likely Tag-based
                        entity.getTriggerUuid() != null ? new UuId(entity.getTriggerUuid().toString()) : null,
                        entity.getTriggerType(),
                        // New: Mapping the String from DB to the FeatureLabel Value Object
                        entity.getTriggerTag() != null ? new FeaturesDomainWrapper.FeatureLabel(new Label(entity.getTriggerTag())) : null,
                        new FeatureUuId(new UuId(entity.getForbiddenFeatureUuid().toString()))
                ))
                .collect(Collectors.toUnmodifiableSet());
    }

}
