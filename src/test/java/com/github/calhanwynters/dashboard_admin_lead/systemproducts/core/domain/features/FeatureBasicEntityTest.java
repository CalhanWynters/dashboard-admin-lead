package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for FeatureBasicEntity and FeatureAbstractClass.
 * Validated for 2025 Java 25 standards.
 */
class FeatureBasicEntityTest {

    private static final String ERROR_MSG = "Field must not be null";

    // --- Happy Path & Defaults ---

    @Test
    @DisplayName("Should build entity successfully where all getters return expected VOs")
    void shouldBuildEntityWithAllFields() {
        UuIdVO uuid = UuIdVO.generate();

        // 1. To test that isUnique DEFAULTS to false, we MUST omit it from the builder.
        // 2. We use valid VO instantiations for all other fields.
        FeatureBasicEntity entity = FeatureBasicEntity.builder()
                .featureId(new PkIdVO(1L))
                .featureUuId(uuid)
                .featureName(new NameVO("Test Name"))
                .featureLabel(new LabelVO("A test label"))
                .featureDescription(new DescriptionVO("Test Description is at least 10 characters"))
                .featureStatus(StatusEnums.ACTIVE)
                .featureVersion(new VersionVO(1))
                .lastModified(LastModifiedVO.now())
                // .isUnique(...) is omitted here to verify the default value logic
                .build();

        // Assertions
        assertThat(entity.getFeatureUuId()).isEqualTo(uuid);
        assertThat(entity.getFeatureName().value()).isEqualTo("Test Name");

        // Requirement: Must default to false if not specified
        assertThat(entity.getIsUnique())
                .withFailMessage("The isUnique flag should default to false when not provided to the builder")
                .isFalse();
    }


    @Test
    @DisplayName("Should throw NPE if isUnique is explicitly set to null")
    void shouldThrowNpeWhenIsUniqueIsNull() {
        // Requirement: "isUnique flag must not be null"
        // In Java, use Boolean.valueOf(null) or simply pass null to the builder method
        assertThatThrownBy(() -> {
            FeatureBasicEntity.builder()
                    .featureId(new PkIdVO(1L))
                    .featureUuId(UuIdVO.generate())
                    .featureName(new NameVO("Test"))
                    .featureLabel(new LabelVO("Label"))
                    .featureDescription(new DescriptionVO("Description Long Enough"))
                    .featureStatus(StatusEnums.ACTIVE)
                    .featureVersion(new VersionVO(1))
                    .lastModified(LastModifiedVO.now())
                    .isUnique(null) // Explicitly passing null to trigger guard clause
                    .build();
        }).isInstanceOf(NullPointerException.class)
                .hasMessageContaining("isUnique flag must not be null");
    }

    // --- Mandatory Field Enforcement (Guard Clauses) ---

    @ParameterizedTest(name = "Should throw {2} when {1} is null")
    @MethodSource("provideMandatoryFields")
    @DisplayName("Verify guard clauses in Abstract Class and Builder")
    void mandatoryFieldsShouldThrowCorrectException(FeatureBasicEntity.Builder builder, String fieldName, Class<? extends Throwable> expectedException, String expectedMessage) {
        assertThatThrownBy(builder::build)
                .isInstanceOf(expectedException)
                .hasMessageContaining(expectedMessage);
    }

    private static Stream<Arguments> provideMandatoryFields() {
        return Stream.of(
                // Fields validated by FeatureAbstractClass constructor (NullPointerException)
                Arguments.of(fullBuilder().featureId(null), "featureId", NullPointerException.class, "Feature ID must not be null"),
                Arguments.of(fullBuilder().featureUuId(null), "featureUuId", NullPointerException.class, "Feature UUID must not be null"),
                Arguments.of(fullBuilder().featureDescription(null), "featureDescription", NullPointerException.class, "Feature Description must not be null"),
                Arguments.of(fullBuilder().featureStatus(null), "featureStatus", NullPointerException.class, "Feature Status must not be null"),
                Arguments.of(fullBuilder().featureVersion(null), "featureVersion", NullPointerException.class, "Feature Version must not be null"),
                Arguments.of(fullBuilder().lastModified(null), "lastModified", NullPointerException.class, "Last Modified indicator must not be null"),
                Arguments.of(fullBuilder().isUnique(null), "isUnique", NullPointerException.class, "isUnique flag must not be null"),

                // Fields validated by Builder.validate() first (IllegalStateException)
                Arguments.of(fullBuilder().featureName(null), "featureName", IllegalStateException.class, "Core naming VOs must be initialized"),
                Arguments.of(fullBuilder().featureLabel(null), "featureLabel", IllegalStateException.class, "Core naming VOs must be initialized")
        );
    }

    @Test
    @DisplayName("Builder validate() should throw IllegalStateException if featureName or featureLabel are missing")
    void builderValidateShouldThrowISE() {
        // Test featureName missing
        FeatureBasicEntity.Builder builderNoName = fullBuilder().featureName(null);
        assertThatThrownBy(builderNoName::build)
                .isInstanceOf(IllegalStateException.class);

        // Test featureLabel missing
        FeatureBasicEntity.Builder builderNoLabel = fullBuilder().featureLabel(null);
        assertThatThrownBy(builderNoLabel::build)
                .isInstanceOf(IllegalStateException.class);
    }

    // --- DDD Identity Contract ---

    @Test
    @DisplayName("Equality: Entities with same UuIdVO but different attributes must be equal")
    void entitiesWithSameUuidShouldBeEqual() {
        UuIdVO sameUuid = UuIdVO.generate();

        FeatureBasicEntity entity1 = fullBuilder()
                .featureUuId(sameUuid)
                .featureName(new NameVO("Alpha"))
                .featureStatus(StatusEnums.ACTIVE)
                .build();

        FeatureBasicEntity entity2 = fullBuilder()
                .featureUuId(sameUuid)
                .featureName(new NameVO("Beta"))
                .featureStatus(StatusEnums.INACTIVE)
                .build();

        assertThat(entity1).isEqualTo(entity2);
        assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
    }

    @Test
    @DisplayName("Inequality: Entities with different UuIdVO values must not be equal")
    void entitiesWithDifferentUuidsShouldNotBeEqual() {
        FeatureBasicEntity entity1 = fullBuilder().featureUuId(UuIdVO.generate()).build();
        FeatureBasicEntity entity2 = fullBuilder().featureUuId(UuIdVO.generate()).build();

        assertThat(entity1).isNotEqualTo(entity2);
    }

    @Test
    @DisplayName("Collection Integrity: HashSet should treat entities with same UUID as one entry")
    void hashSetShouldEnforceUuidUniqueness() {
        UuIdVO commonUuid = UuIdVO.generate();
        Set<FeatureAbstractClass> entitySet = new HashSet<>();

        entitySet.add(fullBuilder().featureUuId(commonUuid).build());
        entitySet.add(fullBuilder().featureUuId(commonUuid).build());

        assertThat(entitySet).hasSize(1);
    }

    // --- Helper for Boilerplate-free Builders ---

    private static FeatureBasicEntity.Builder fullBuilder() {
        return FeatureBasicEntity.builder()
                .featureId(new PkIdVO(100L))
                .featureUuId(UuIdVO.generate())
                .featureName(new NameVO("Test Feature"))
                .featureLabel(new LabelVO("Label"))
                .featureDescription(new DescriptionVO("Description"))
                .featureStatus(StatusEnums.ACTIVE)
                .featureVersion(new VersionVO(1))
                .lastModified(LastModifiedVO.now())
                .isUnique(true);
    }
}