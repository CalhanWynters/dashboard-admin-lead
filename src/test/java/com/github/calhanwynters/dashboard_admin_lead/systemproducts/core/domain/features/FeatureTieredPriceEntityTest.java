package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.exceptions.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.TieredPriceVO.PriceTier;

class FeatureTieredPriceEntityTest {

    // --- Test Data Factories ---
    private static final UuIdVO VALID_UUID = UuIdVO.generate();
    private static final PkIdVO VALID_PK = PkIdVO.of(201L);
    private static final NameVO VALID_NAME = NameVO.from("Premium Compute Tier");
    private static final LabelVO VALID_LABEL = LabelVO.from("TIER-PRM");
    private static final DescriptionVO VALID_DESC = DescriptionVO.from("High-performance compute tier");
    private static final StatusEnums VALID_STATUS = StatusEnums.ACTIVE;
    private static final VersionVO VALID_VERSION = VersionVO.from("1.0.0");
    private static final LastModifiedVO VALID_MODIFIED = LastModifiedVO.now();
    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency EUR = Currency.getInstance("EUR");

    // Helper to create a valid TieredPriceVO used across tests
    private static TieredPriceVO createValidTieredPriceVO(Currency currency) {
        return new TieredPriceVO(
                "unit-usage",
                List.of(PriceTier.of(BigDecimal.ZERO, BigDecimal.TEN, currency)),
                currency
        );
    }


    @Test
    @DisplayName("Recursive Builder: Should chain base and subclass fields in one statement")
    void recursiveBuilderFluencyTest() {
        var entity = FeatureTieredPriceEntity.builder()
                // Shared Base Fields
                .featureId(VALID_PK)
                .featureUuId(VALID_UUID)
                .featureName(VALID_NAME)
                .featureLabel(VALID_LABEL)
                .featureDescription(VALID_DESC)
                .featureStatus(VALID_STATUS)
                .featureVersion(VALID_VERSION)
                .lastModified(VALID_MODIFIED)
                // Subclass Specific Fields
                .addTieredScheme(USD, createValidTieredPriceVO(USD))
                .build();

        assertAll(
                () -> assertEquals(VALID_NAME.value(), entity.getFeatureName().value()),
                () -> assertTrue(entity.getTieredPriceForCurrency(USD).isPresent()),
                () -> assertEquals(1, entity.getTieredPriceSchemes().size())
        );
    }


    @ParameterizedTest(name = "Quantity {0} results in price {1}")
    @CsvSource({
            "0, 10.00",
            "9, 10.00",
            "10, 50.00",
            "100, 50.00"
    })
    void calculatePriceAcrossTiers(double quantity, String expectedPriceStr) {
        BigDecimal qty = BigDecimal.valueOf(quantity);
        BigDecimal expected = new BigDecimal(expectedPriceStr);

        var multiTierVO = new TieredPriceVO(
                "unit-usage",
                List.of(
                        PriceTier.of(BigDecimal.ZERO, new BigDecimal("10.00"), USD),
                        PriceTier.of(BigDecimal.TEN, new BigDecimal("50.00"), USD)
                ),
                USD
        );

        // FIX: Include all mandatory base fields to satisfy the Abstract Class requirements
        var entity = FeatureTieredPriceEntity.builder()
                .featureId(VALID_PK)
                .featureUuId(VALID_UUID)
                .featureName(VALID_NAME)
                .featureLabel(VALID_LABEL)       // <--- ADDED: Fixes NullPointerException
                .featureDescription(VALID_DESC)  // <--- ADDED: Best practice for consistency
                .featureStatus(VALID_STATUS)     // <--- ADDED
                .featureVersion(VALID_VERSION)   // <--- ADDED
                .lastModified(VALID_MODIFIED)    // <--- ADDED
                .addTieredScheme(USD, multiTierVO)
                .build();

        BigDecimal result = entity.getTieredPriceForCurrency(USD)
                .orElseThrow()
                .calculate(qty);

        assertEquals(0, expected.compareTo(result), "Tiered price calculation mismatch");
    }




    @Nested
    @DisplayName("Invariant Protection: Invalid Inputs")
    class InvariantTests {

        @Test
        @DisplayName("Should throw exception for currency mismatch between map key and VO internal currency")
        void build_CurrencyMismatchInMapKey_ThrowsException() {
            // Arrange: EUR is the key, but the VO is USD
            var mismatchedVO = createValidTieredPriceVO(USD);

            // Act & Assert
            // FIX 1: Expect DomainValidationException (thrown by the Entity during .build())
            assertThrows(DomainValidationException.class, () -> {
                FeatureTieredPriceEntity.builder()
                        // FIX 2: Add mandatory base fields to avoid NPE
                        .featureId(VALID_PK)
                        .featureUuId(VALID_UUID)
                        .featureName(VALID_NAME)
                        .featureLabel(VALID_LABEL)
                        .featureStatus(VALID_STATUS)
                        .featureVersion(VALID_VERSION)
                        .lastModified(VALID_MODIFIED)
                        .addTieredScheme(EUR, mismatchedVO)
                        .build();
            }, "Mismatch: Map key [EUR] does not match TieredPriceVO internal currency [USD]");
        }

        @Test
        @DisplayName("Should throw exception if schemes are missing to prevent 'Automatic Defaults'")
        void build_MissingMandatorySchemes_ThrowsException() {
            var builder = FeatureTieredPriceEntity.builder()
                    .featureUuId(VALID_UUID).featureName(VALID_NAME);

            assertThrows(DomainValidationException.class, builder::build,
                    "Pricing schemes are missing. You must intentionally define at least one currency and price.");
        }


    }

    @Test
    @DisplayName("Immutability: Internal state must remain unchanged")
    void entity_PricingMapImmutability_ThrowsOnModification() {
        // FIX: Populate all mandatory base fields to satisfy FeatureAbstractClass.Builder validation
        var entity = FeatureTieredPriceEntity.builder()
                .featureId(VALID_PK)           // Added: Fixes NPE "Feature ID must not be null"
                .featureUuId(VALID_UUID)
                .featureName(VALID_NAME)
                .featureLabel(VALID_LABEL)     // Added: Prevents next NPE in the chain
                .featureDescription(VALID_DESC)
                .featureStatus(VALID_STATUS)   // Added
                .featureVersion(VALID_VERSION) // Added
                .lastModified(VALID_MODIFIED)  // Added
                .addTieredScheme(USD, createValidTieredPriceVO(USD))
                .build();

        // Expose the map (which uses Map.copyOf() internally)
        Map<Currency, TieredPriceVO> exposedPricing = entity.getTieredPriceSchemes();

        // Verify that modification attempts throw UnsupportedOperationException
        assertThrows(UnsupportedOperationException.class, () ->
                exposedPricing.put(EUR, createValidTieredPriceVO(EUR))
        );
    }

    @Test
    @DisplayName("Identity: Entities with same UuId are equal even if names differ")
    void identityEqualityTest() {
        var scheme = createValidTieredPriceVO(USD);

        // Entity 1: Unique PK, same UUID, mandatory fields populated
        var entity1 = FeatureTieredPriceEntity.builder()
                .featureId(PkIdVO.of(201L))
                .featureUuId(VALID_UUID)
                .featureName(NameVO.from("Alpha"))
                .featureLabel(VALID_LABEL)     // Added: Fixes NPE
                .featureDescription(VALID_DESC)
                .featureStatus(VALID_STATUS)   // Added
                .featureVersion(VALID_VERSION) // Added
                .lastModified(VALID_MODIFIED)  // Added
                .addTieredScheme(USD, scheme)
                .build();

        // Entity 2: Different PK and Name, but SAME UUID
        var entity2 = FeatureTieredPriceEntity.builder()
                .featureId(PkIdVO.of(202L))
                .featureUuId(VALID_UUID)
                .featureName(NameVO.from("Beta"))
                .featureLabel(VALID_LABEL)     // Added: Fixes NPE
                .featureDescription(VALID_DESC)
                .featureStatus(VALID_STATUS)   // Added
                .featureVersion(VALID_VERSION) // Added
                .lastModified(VALID_MODIFIED)  // Added
                .addTieredScheme(USD, scheme)
                .build();

        // Assert that identity is based solely on UuIdVO
        assertAll(
                () -> assertEquals(entity1, entity2, "Entities with the same UUID must be equal"),
                () -> assertEquals(entity1.hashCode(), entity2.hashCode(), "HashCodes must match for equal entities")
        );
    }



}


