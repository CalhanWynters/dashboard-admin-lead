package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.LastModifiedVO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.NameVO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuIdVO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.VersionVO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.exceptions.DomainValidationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

/*
@DisplayName("FeatureTieredPriceEntity Domain Validation Suite")
class FeatureTieredPriceEntityTest {

    // Common Test Data
    private static final UuIdVO FEATURE_UUID = new UuIdVO(UUID.randomUUID());
    private static final NameVO VALID_NAME = new NameVO("Premium Compute Tier");
    private static final Currency USD = Currency.getInstance("USD");
    private static final VersionVO INITIAL_VERSION = new VersionVO(1);
    private static final LastModifiedVO NOW = new LastModifiedVO(LocalDateTime.now());

    @Nested
    @DisplayName("1. Existence & Nullability")
    class ExistenceAndNullability {

        @Test
        @DisplayName("Build_MissingMandatoryVOs_ThrowsException")
        void build_MissingMandatoryVOs_ThrowsException() {
            // Arrange / Act / Assert
            assertThatThrownBy(() -> FeatureTieredPriceEntity.builder()
                    .withUuId(null)
                    .withName(null)
                    .build())
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("Mandatory VOs (UUID, Name) are required");
        }
    }

    @Nested
    @DisplayName("2. Cross-Field Consistency")
    class CrossFieldConsistency {

        @Test
        @DisplayName("Build_CurrencyMismatchInMap_ThrowsException")
        void build_CurrencyMismatchInMap_ThrowsException() {
            // Arrange
            var pricingMap = Map.of(
                    1, new TieredPriceVO(new BigDecimal("10.00"), USD),
                    2, new TieredPriceVO(new BigDecimal("15.00"), Currency.getInstance("EUR")) // Mismatch
            );

            // Act & Assert
            assertThatThrownBy(() -> FeatureTieredPriceEntity.builder()
                    .withUuId(FEATURE_UUID)
                    .withName(VALID_NAME)
                    .withPricing(pricingMap)
                    .build())
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("All tiers must match the entity's base currency");
        }
    }

    @Nested
    @DisplayName("3. Semantic Validation")
    class SemanticValidation {

        @Test
        @DisplayName("Build_SelfIncompatibleFeature_ThrowsException")
        void build_SelfIncompatibleFeature_ThrowsException() {
            // Arrange
            Set<UuIdVO> incompatibles = Set.of(FEATURE_UUID); // Self-reference

            // Act & Assert
            assertThatThrownBy(() -> FeatureTieredPriceEntity.builder()
                    .withUuId(FEATURE_UUID)
                    .withName(VALID_NAME)
                    .withIncompatibleFeatures(incompatibles)
                    .build())
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("A feature cannot be incompatible with itself");
        }
    }

    @Nested
    @DisplayName("4. Lexical / Encoding")
    class LexicalEncoding {

        @Test
        @DisplayName("NameVO_InternationalCharacters_PreservesIntegrity")
        void nameVO_InternationalCharacters_PreservesIntegrity() {
            // Arrange
            String complexText = "Möbel-Überprüfung 🚀";
            NameVO internationalName = new NameVO(complexText);

            // Act
            var entity = FeatureTieredPriceEntity.builder()
                    .withUuId(FEATURE_UUID)
                    .withName(internationalName)
                    .build();

            // Assert
            assertThat(entity.getName().value()).isEqualTo(complexText);
        }
    }

    @Nested
    @DisplayName("5. Boundary & Extreme Input")
    class BoundaryInput {

        @Test
        @DisplayName("CalculatePrice_ZeroAndExtremeValues_HandledCorrectly")
        void calculatePrice_ZeroAndExtremeValues_HandledCorrectly() {
            // Arrange
            BigDecimal extremeValue = new BigDecimal("999999999999999.99");
            var entity = FeatureTieredPriceEntity.builder()
                    .withUuId(FEATURE_UUID)
                    .withName(VALID_NAME)
                    .withPricing(Map.of(0, new TieredPriceVO(BigDecimal.ZERO, USD),
                            100, new TieredPriceVO(extremeValue, USD)))
                    .build();

            // Act / Assert
            assertThat(entity.calculatePrice(0)).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(entity.calculatePrice(100)).isEqualByComparingTo(extremeValue);
        }
    }

    @Nested
    @DisplayName("6. Identity & Equality")
    class IdentityEquality {

        @Test
        @DisplayName("Equals_SameUuIdDifferentPrices_ReturnsTrue")
        void equals_SameUuIdDifferentPrices_ReturnsTrue() {
            // Arrange
            var entity1 = FeatureTieredPriceEntity.builder()
                    .withUuId(FEATURE_UUID)
                    .withName(new NameVO("Price A"))
                    .build();

            var entity2 = FeatureTieredPriceEntity.builder()
                    .withUuId(FEATURE_UUID)
                    .withName(new NameVO("Price B"))
                    .build();

            // Act & Assert
            assertThat(entity1).isEqualTo(entity2);
            assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
        }
    }

    @Nested
    @DisplayName("7. Technical Safety")
    class TechnicalSafety {

        @Test
        @DisplayName("Entity_PricingMapImmutability_ThrowsOnModification")
        void entity_PricingMapImmutability_ThrowsOnModification() {
            // Arrange
            var entity = FeatureTieredPriceEntity.builder()
                    .withUuId(FEATURE_UUID)
                    .withName(VALID_NAME)
                    .withPricing(new HashMap<>(Map.of(1, new TieredPriceVO(BigDecimal.ONE, USD))))
                    .build();

            // Act & Assert
            Map<Integer, TieredPriceVO> exposedPricing = entity.getPricing();
            assertThatThrownBy(() -> exposedPricing.put(2, new TieredPriceVO(BigDecimal.TEN, USD)))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}

 */