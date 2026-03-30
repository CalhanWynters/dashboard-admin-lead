package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
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
                        new UuId(entity.getTriggerUuid().toString()),
                        entity.getTriggerType(),
                        null, // triggerTag (optional in your current record)
                        new FeatureUuId(new UuId(entity.getForbiddenFeatureUuid().toString()))
                ))
                .collect(Collectors.toUnmodifiableSet());
    }
}
