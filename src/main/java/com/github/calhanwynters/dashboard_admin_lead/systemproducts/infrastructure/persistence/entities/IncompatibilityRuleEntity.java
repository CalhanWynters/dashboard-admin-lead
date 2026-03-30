package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.TypedTrigger;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "feature_incompatibility_rules")
public class IncompatibilityRuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trigger_uuid", nullable = false)
    private UUID triggerUuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", nullable = false)
    private TypedTrigger.TriggerType triggerType;

    @Column(name = "forbidden_feature_uuid", nullable = false)
    private UUID forbiddenFeatureUuid;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    // --- Standard Getters & Setters ---
    public UUID getTriggerUuid() { return triggerUuid; }
    public void setTriggerUuid(UUID triggerUuid) { this.triggerUuid = triggerUuid; }

    public TypedTrigger.TriggerType getTriggerType() { return triggerType; }
    public void setTriggerType(TypedTrigger.TriggerType triggerType) { this.triggerType = triggerType; }

    public UUID getForbiddenFeatureUuid() { return forbiddenFeatureUuid; }
    public void setForbiddenFeatureUuid(UUID forbiddenFeatureUuid) { this.forbiddenFeatureUuid = forbiddenFeatureUuid; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
