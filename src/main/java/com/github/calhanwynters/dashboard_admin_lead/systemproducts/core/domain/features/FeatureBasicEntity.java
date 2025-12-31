package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import java.util.Objects;

/**
 * A concrete implementation of FeatureAbstractClass representing a standard system feature.
 * Optimized for 2025 Domain-Driven Design (DDD) patterns.
 */
public class FeatureBasicEntity extends FeatureAbstractClass {

    private FeatureBasicEntity(Builder builder) {
        super(
                builder.featureId,
                builder.featureUuId,
                builder.featureName,
                builder.featureLabel,
                builder.featureDescription,
                builder.featureStatus,
                builder.featureVersion,
                builder.lastModified,
                builder.isUnique
        );
    }


    /**
     * Provides a standard builder to handle the 9 mandatory domain parameters.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PkIdVO featureId;
        private UuIdVO featureUuId;
        private NameVO featureName;
        private LabelVO featureLabel;
        private DescriptionVO featureDescription;
        private StatusEnums featureStatus;
        private VersionVO featureVersion;
        private LastModifiedVO lastModified;
        private Boolean isUnique = false; // Defaulting for a "Basic" entity

        public Builder featureId(PkIdVO id) { this.featureId = id; return this; }
        public Builder featureUuId(UuIdVO uuid) { this.featureUuId = uuid; return this; }
        public Builder featureName(NameVO name) { this.featureName = name; return this; }
        public Builder featureLabel(LabelVO label) { this.featureLabel = label; return this; }
        public Builder featureDescription(DescriptionVO desc) { this.featureDescription = desc; return this; }
        public Builder featureStatus(StatusEnums status) { this.featureStatus = status; return this; }
        public Builder featureVersion(VersionVO version) { this.featureVersion = version; return this; }
        public Builder lastModified(LastModifiedVO modified) { this.lastModified = modified; return this; }
        public Builder isUnique(Boolean unique) { this.isUnique = unique; return this; }

        /**
         * Final validation before the entity is instantiated.
         */
        public FeatureBasicEntity build() {
            validate();
            return new FeatureBasicEntity(this);
        }

        private void validate() {
            // Business Invariant: A 'Basic' feature should rarely be unique by default.
            // You can add logic here to warn or throw errors if rules are violated.
            if (featureName == null || featureLabel == null) {
                throw new IllegalStateException("Core naming VOs must be initialized before building.");
            }
        }
    }
}