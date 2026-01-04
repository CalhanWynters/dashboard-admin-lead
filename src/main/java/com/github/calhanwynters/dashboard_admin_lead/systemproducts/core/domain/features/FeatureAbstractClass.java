package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.exceptions.DomainValidationException;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Abstract base class for all Product Features.
 * Implements an "Always-Valid" domain model with a fail-fast builder.
 */
public abstract class FeatureAbstractClass {
    private final PkIdVO featureId;
    private final UuIdVO featureUuId;
    private final UuIdVO featVariantId; // Explicit link (Nullable for Global Features)
                                        // Also should be handled externally by Aggregate Root or Service
    private final NameVO featureName;
    private final LabelVO featureLabel;
    private final DescriptionVO featureDescription;
    private final StatusEnums featureStatus;
    private final VersionVO featureVersion;
    private final LastModifiedVO lastModified;
    private final Set<UuIdVO> incompatibleFeatures;

    protected FeatureAbstractClass(Builder<?> builder) {
        // 1. Fail-fast validation before state assignment
        validate(builder);

        // 2. State assignment
        this.featureId = builder.featureId;
        this.featureUuId = builder.featureUuId;
        this.featVariantId = builder.featVariantId;
        this.featureName = builder.featureName;
        this.featureLabel = builder.featureLabel;
        this.featureDescription = builder.featureDescription;
        this.featureStatus = builder.featureStatus;
        this.featureVersion = builder.featureVersion;
        this.lastModified = builder.lastModified;

        // 3. Defensive copy to ensure true immutability of the domain object
        this.incompatibleFeatures = Set.copyOf(builder.incompatibleFeatures);
    }

    private void validate(Builder<?> builder) {
        // 1. Mandatory Presence Checks (Structural Guard)
        // These ensure the object is technically sound before we look at business rules.
        Objects.requireNonNull(builder.featureId, "Feature ID must not be null");
        Objects.requireNonNull(builder.featureUuId, "Feature UUID must not be null");
        Objects.requireNonNull(builder.featureName, "Feature Name must not be null");
        Objects.requireNonNull(builder.featureLabel, "Feature Label must not be null");
        Objects.requireNonNull(builder.featureDescription, "Feature Description must not be null");
        Objects.requireNonNull(builder.featureStatus, "Feature Status must not be null");
        Objects.requireNonNull(builder.featureVersion, "Feature Version must not be null");
        Objects.requireNonNull(builder.lastModified, "Last Modified indicator must not be null");
        Objects.requireNonNull(builder.incompatibleFeatures, "Incompatible features set must not be null");

        // 2. Business Logic Validation (Domain Guard)
        // These enforce the specific rules of your product management domain.
        if (builder.incompatibleFeatures.contains(builder.featureUuId)) {
            throw new DomainValidationException(
                    String.format("Business Rule Violation: Feature [%s] cannot be marked as incompatible with itself.",
                            builder.featureUuId)
            );
        }
    }


    /**
     * Generic Builder using recursive type parameters to allow fluent chaining in subclasses.
     */
    public abstract static class Builder<T extends Builder<T>> {
        protected PkIdVO featureId;
        protected UuIdVO featureUuId;
        protected UuIdVO featVariantId; // Nullable
        protected NameVO featureName;
        protected LabelVO featureLabel;
        protected DescriptionVO featureDescription;
        protected StatusEnums featureStatus;
        protected VersionVO featureVersion;
        protected LastModifiedVO lastModified;

        // Mutable HashSet used for construction phase
        protected Set<UuIdVO> incompatibleFeatures = new HashSet<>();

        protected abstract T self();

        public T featureId(PkIdVO featureId) { this.featureId = featureId; return self(); }
        public T featureUuId(UuIdVO featureUuId) { this.featureUuId = featureUuId; return self(); }
        public T featVariantId(UuIdVO variantId) { this.featVariantId = variantId; return self(); }
        public T featureName(NameVO featureName) { this.featureName = featureName; return self(); }
        public T featureLabel(LabelVO featureLabel) { this.featureLabel = featureLabel; return self(); }
        public T featureDescription(DescriptionVO featureDescription) { this.featureDescription = featureDescription; return self(); }
        public T featureStatus(StatusEnums featureStatus) { this.featureStatus = featureStatus; return self(); }
        public T featureVersion(VersionVO featureVersion) { this.featureVersion = featureVersion; return self(); }
        public T lastModified(LastModifiedVO lastModified) { this.lastModified = lastModified; return self(); }

        /**
         * Bulk setter for incompatible features.
         */
        public T incompatibleFeatures(Set<UuIdVO> incompatibleFeatures) {
            this.incompatibleFeatures = (incompatibleFeatures == null)
                    ? new HashSet<>()
                    : new HashSet<>(incompatibleFeatures);
            return self();
        }

        /**
         * Incremental adder for incompatible features.
         */
        public T addIncompatibleFeature(UuIdVO uuid) {
            if (uuid != null) {
                this.incompatibleFeatures.add(uuid);
            }
            return self();
        }

        public abstract FeatureAbstractClass build();
    }

    // Getters
    public PkIdVO getFeatureId() { return featureId; }
    public UuIdVO getFeatureUuId() { return featureUuId; }
    public NameVO getFeatureName() { return featureName; }
    public LabelVO getFeatureLabel() { return featureLabel; }
    public DescriptionVO getFeatureDescription() { return featureDescription; }
    public StatusEnums getFeatureStatus() { return featureStatus; }
    public VersionVO getFeatureVersion() { return featureVersion; }
    public LastModifiedVO getLastModified() { return lastModified; }
    public Set<UuIdVO> getIncompatibleFeatures() { return incompatibleFeatures; }

    public boolean isIncompatibleWith(UuIdVO otherFeatureUuId) {
        return incompatibleFeatures.contains(otherFeatureUuId);
    }

    /**
     * Semantic helper to determine if this feature is variant-specific.
     */
    public final boolean isVariantSpecific() {
        return featVariantId != null;
    }

    public final Optional<UuIdVO> getFeatVariantId() {
        return Optional.ofNullable(featVariantId);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeatureAbstractClass that)) return false;
        return Objects.equals(this.featureUuId, that.featureUuId);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(featureUuId);
    }
}
