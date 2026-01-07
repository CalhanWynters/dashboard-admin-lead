package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.exceptions.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Revised Domain Architecture Tests for 2026 System Standards.
 * Fixes: AssertJ fluent descriptions, nested class visibility, and factory method resolution.
 */
@DisplayName("FeatureBasicEntity Domain Architecture Tests")
class FeatureBasicEntityTest {

    // --- Test Data Factories ---
    private static final UuIdVO VALID_UUID = UuIdVO.generate();
    private static final UuIdVO VARIANT_UUID = UuIdVO.generate();
    private static final PkIdVO VALID_PK = PkIdVO.fromString(101L);
    private static final NameVO VALID_NAME = NameVO.from("Core Engine");
    private static final LabelVO VALID_LABEL = LabelVO.from("CORE-01");
    private static final DescriptionVO VALID_DESC = DescriptionVO.from("Primary logic processor");
    private static final StatusEnums VALID_STATUS = StatusEnums.ACTIVE;
    private static final VersionVO VALID_VERSION = VersionVO.from("1.0.0");
    private static final LastModifiedVO VALID_MODIFIED = LastModifiedVO.now();

    @Nested
    @DisplayName("1. Fail-Fast Validation & Constraints")
    class ValidationTests {


        @Test
        @DisplayName("Should throw NullPointerException when mandatory fields are missing")
        void shouldThrowNpeOnMissingMandatoryFields() {
            assertThatThrownBy(() -> FeatureBasicEntity.builder()
                    .featureId(null)
                    .build())
                    .isExactlyInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Feature ID");
        }


        @Test
        @DisplayName("Should throw DomainValidationException if entity is incompatible with itself")
        void shouldPreventSelfIncompatibility() {
            UuIdVO selfId = UuIdVO.generate();
            Set<UuIdVO> incompatibles = Set.of(selfId);

            assertThatThrownBy(() -> FeatureBasicEntity.builder()
                    .featureId(VALID_PK)
                    .featureUuId(selfId)
                    .featureName(VALID_NAME)
                    .featureLabel(VALID_LABEL)
                    .featureDescription(VALID_DESC)
                    .featureStatus(VALID_STATUS)
                    .featureVersion(VALID_VERSION)
                    .lastModified(VALID_MODIFIED)
                    .incompatibleFeatures(incompatibles)
                    .build())
                    .isExactlyInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("cannot be marked as incompatible with itself");
        }


    }

    @Nested
    @DisplayName("2. Deep Immutability & Defensive Copying")
    class ImmutabilityTests {

        @Test
        @DisplayName("Input Defense: Entity must be shielded from external mutation of input sets")
        void shouldBeImmuneToExternalInputMutation() {
            Set<UuIdVO> mutableSet = new HashSet<>();
            mutableSet.add(UuIdVO.generate());

            FeatureBasicEntity entity = createValidBaseBuilder()
                    .incompatibleFeatures(mutableSet)
                    .build();

            int initialSize = entity.getIncompatibleFeatures().size();

            // Attempt to mutate original source set
            mutableSet.add(UuIdVO.generate());

            assertThat(entity.getIncompatibleFeatures())
                    .as("Entity state should not change when external set is modified")
                    .hasSize(initialSize);
        }

        @Test
        @DisplayName("Output Defense: Getter must return an unmodifiable view")
        void shouldReturnUnmodifiableSet() {
            FeatureBasicEntity entity = createValidBaseBuilder().build();
            Set<UuIdVO> exposedSet = entity.getIncompatibleFeatures();

            assertThatThrownBy(() -> exposedSet.add(UuIdVO.generate()))
                    .as("Direct modification of returned set must be prohibited")
                    .isExactlyInstanceOf(UnsupportedOperationException.class);
        }
    }


    @Nested
    @DisplayName("3. Identity Logic (Value-Object Style)")
    class IdentityTests {


        @Test
        @DisplayName("Equals/HashCode: Equality must be strictly bound to featureUuId")
        void shouldObeyIdentityEqualityRules() {
            UuIdVO sharedId = UuIdVO.generate();

            FeatureBasicEntity entity1 = createValidBaseBuilder().featureUuId(sharedId).featureName(NameVO.from("Aa")).build();
            FeatureBasicEntity entity2 = createValidBaseBuilder().featureUuId(sharedId).featureName(NameVO.from("Bb")).build();
            FeatureBasicEntity entity3 = createValidBaseBuilder().featureUuId(UuIdVO.generate()).build();

            assertThat(entity1).isEqualTo(entity2);
            assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
            assertThat(entity1).isNotEqualTo(entity3);
        }
    }





    @Nested
    @DisplayName("4. Semantic Helpers & Variant Logic")
    class SemanticHelperTests {

        @Test
        @DisplayName("isVariantSpecific: Returns true only when featVariantId is present")
        void testIsVariantSpecific() {
            FeatureBasicEntity variantEntity = createValidBaseBuilder()
                    .featVariantId(VARIANT_UUID)
                    .build();

            FeatureBasicEntity genericEntity = createValidBaseBuilder()
                    .featVariantId(null)
                    .build();

            assertThat(variantEntity.isVariantSpecific()).isTrue();
            assertThat(genericEntity.isVariantSpecific()).isFalse();
        }

        @Test
        @DisplayName("getFeatVariantId: Returns Optional wrapper")
        void testVariantIdOptionalWrapping() {
            FeatureBasicEntity entity = createValidBaseBuilder().featVariantId(VARIANT_UUID).build();

            assertThat(entity.getFeatVariantId())
                    .as("The variant ID should be wrapped in an Optional")
                    .isExactlyInstanceOf(Optional.class)
                    .contains(VARIANT_UUID);
        }
    }

    @Nested
    @DisplayName("5. Fluent Builder & Recursive Generics")
    class BuilderTests {

        @Test
        @DisplayName("Should support full fluent chain with subclass-specific methods")
        void testRecursiveGenericBuilder() {
            FeatureBasicEntity entity = createValidBaseBuilder()
                    .featVariantId(VARIANT_UUID)
                    .build();

            assertThat(entity).isNotNull();
            assertThat(entity.getFeatureName()).isEqualTo(VALID_NAME);
        }
    }

    /**
     * Helper to provide a pre-filled builder.
     * Visibility is 'protected' to ensure seamless access from @Nested inner classes.
     */
    protected FeatureBasicEntity.Builder createValidBaseBuilder() {
        return FeatureBasicEntity.builder()
                .featureId(VALID_PK)
                .featureUuId(VALID_UUID)
                .featureName(VALID_NAME)
                .featureLabel(VALID_LABEL)
                .featureDescription(VALID_DESC)
                .featureStatus(VALID_STATUS)
                .featureVersion(VALID_VERSION)
                .lastModified(VALID_MODIFIED)
                .incompatibleFeatures(new HashSet<>());
    }
}