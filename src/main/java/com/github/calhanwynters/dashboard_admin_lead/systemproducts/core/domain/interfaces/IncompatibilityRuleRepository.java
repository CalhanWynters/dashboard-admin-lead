package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.interfaces;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.IncompatibilityRule;

import java.util.Set;

/**
 * Domain Interface (Contract) for retrieving compatibility constraints.
 * The implementation lives in the Infrastructure layer (JPA, Mongo, etc).
 */
public interface IncompatibilityRuleRepository {
    Set<IncompatibilityRule> findAllActiveRules();
}
