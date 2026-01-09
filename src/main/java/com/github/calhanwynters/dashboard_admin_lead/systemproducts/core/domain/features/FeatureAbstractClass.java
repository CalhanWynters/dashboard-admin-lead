package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.exceptions.DomainValidationException;

import java.time.Instant;
import java.util.*;

/**
 * Hardened Abstract Base for Product Features (2026 Edition).
 * Enforces a "Root-Valid" state using Java 25 Constructor Prologues.
 */
public abstract class FeatureAbstractClass {

    // --- 1. STATE ---
    private final PkIdVO featureId;
    private final UuIdVO featureUuId;
    private final UuIdVO featVariantId; // Nullable for Global Features
    private final NameVO featureName;
    private final LabelVO featureLabel;
    private final DescriptionVO featureDescription;
    private final StatusEnums featureStatus;
    private final VersionVO featureVersion;
    private final LastModifiedVO lastModified;
    private final Set<UuIdVO> incompatibleFeatures;

    // --- 2. CONSTRUCTOR ---
    protected FeatureAbstractClass(Builder<?> builder) {
        // PROLOGUE: Validate and Freeze core metadata before parent init
        // This ensures the root is valid before subclasses even begin their own prologue.
        var validated = validateAndFreezeCore(builder);

        // Parent constructor (Object)
        super();

        // EPILOGUE: Atomic assignment from the validated record
        this.featureId = validated.featureId();
        this.featureUuId = validated.featureUuId();
        this.featVariantId = validated.featVariantId();
        this.featureName = validated.featureName();
        this.featureLabel = validated.featureLabel();
        this.featureDescription = validated.featureDescription();
        this.featureStatus = validated.featureStatus();
        this.featureVersion = validated.featureVersion();
        this.lastModified = validated.lastModified();
        this.incompatibleFeatures = validated.incompatibleFeatures();
    }

    // --- 3. INTERNAL VALIDATION LOGIC ---

    /**
     * Recommended 2026 Pattern: Static helper returns a record of validated data.
     * Guards the 9 mandatory metadata fields.
     */
    private static ValidatedCore validateAndFreezeCore(Builder<?> b) {
        // Structural Guards
        Objects.requireNonNull(b.featureId, "Feature ID must not be null");
        Objects.requireNonNull(b.featureUuId, "Feature UUID must not be null");
        Objects.requireNonNull(b.featureName, "Feature Name must not be null");
        Objects.requireNonNull(b.featureLabel, "Feature Label must not be null");
        Objects.requireNonNull(b.featureDescription, "Feature Description must not be null");
        Objects.requireNonNull(b.featureStatus, "Feature Status must not be null");
        Objects.requireNonNull(b.featureVersion, "Feature Version must not be null");
        Objects.requireNonNull(b.lastModified, "Last Modified indicator must not be null");
        Objects.requireNonNull(b.incompatibleFeatures, "Incompatible features set must not be null");

        // Domain Guard: Self-incompatibility check
        if (b.incompatibleFeatures.contains(b.featureUuId)) {
            throw new DomainValidationException(
                    "Business Rule Violation: Feature [%s] cannot be marked as incompatible with itself."
                            .formatted(b.featureUuId)
            );
        }

        // Return the frozen snapshot
        return new ValidatedCore(
                b.featureId, b.featureUuId, b.featVariantId,
                b.featureName, b.featureLabel, b.featureDescription,
                b.featureStatus, b.featureVersion, b.lastModified,
                Set.copyOf(b.incompatibleFeatures)
        );
    }

    /**
     * Internal data carrier for the Return-and-Assign pattern.
     */
    private record ValidatedCore(
            PkIdVO featureId, UuIdVO featureUuId, UuIdVO featVariantId,
            NameVO featureName, LabelVO featureLabel, DescriptionVO featureDescription,
            StatusEnums featureStatus, VersionVO featureVersion, LastModifiedVO lastModified,
            Set<UuIdVO> incompatibleFeatures
    ) {}

    // --- 4. HIERARCHICAL BUILDER ---

    public abstract static class Builder<T extends Builder<T>> {
        protected PkIdVO featureId;
        protected UuIdVO featureUuId;
        protected UuIdVO featVariantId;
        protected NameVO featureName;
        protected LabelVO featureLabel;
        protected DescriptionVO featureDescription;
        protected StatusEnums featureStatus;
        protected VersionVO featureVersion;
        protected LastModifiedVO lastModified;
        protected Set<UuIdVO> incompatibleFeatures = new HashSet<>();

        protected abstract T self();

        public T featureId(PkIdVO val) { this.featureId = val; return self(); }
        public T featureUuId(UuIdVO val) { this.featureUuId = val; return self(); }
        public T featVariantId(UuIdVO val) { this.featVariantId = val; return self(); }
        public T featureName(NameVO val) { this.featureName = val; return self(); }
        public T featureLabel(LabelVO val) { this.featureLabel = val; return self(); }
        public T featureDescription(DescriptionVO val) { this.featureDescription = val; return self(); }
        public T featureStatus(StatusEnums val) { this.featureStatus = val; return self(); }
        public T featureVersion(VersionVO val) { this.featureVersion = val; return self(); }
        public T lastModified(LastModifiedVO val) { this.lastModified = val; return self(); }

        public T incompatibleFeatures(Set<UuIdVO> val) {
            this.incompatibleFeatures = (val == null) ? new HashSet<>() : new HashSet<>(val);
            return self();
        }

        public T addIncompatibleFeature(UuIdVO val) {
            if (val != null) this.incompatibleFeatures.add(val);
            return self();
        }

        public abstract FeatureAbstractClass build();
    }

    // --- 5. PUBLIC ACCESSORS ---

    public PkIdVO getFeatureId() { return featureId; }
    public UuIdVO getFeatureUuId() { return featureUuId; }
    public NameVO getFeatureName() { return featureName; }
    public LabelVO getFeatureLabel() { return featureLabel; }
    public DescriptionVO getFeatureDescription() { return featureDescription; }
    public StatusEnums getFeatureStatus() { return featureStatus; }
    public VersionVO getFeatureVersion() { return featureVersion; }
    public LastModifiedVO getLastModified() { return lastModified; }
    public Set<UuIdVO> getIncompatibleFeatures() { return incompatibleFeatures; }

    public final boolean isVariantSpecific() { return featVariantId != null; }
    public final Optional<UuIdVO> getFeatVariantId() { return Optional.ofNullable(featVariantId); }

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
