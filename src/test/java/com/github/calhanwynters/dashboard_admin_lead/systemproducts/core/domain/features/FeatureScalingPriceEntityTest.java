package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.exceptions.DomainValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/*
class FeatureScalingPriceEntityTest {

    private static final String FEATURE_ID = "feat-777";
    private static final Currency EUR = Currency.getInstance("EUR");

    @Test
    @DisplayName("Recursive Builder: Should chain base and subclass fields in one statement")
    void recursiveBuilderFluencyTest() {
        // We must provide ALL mandatory base fields to pass the 'Always-Valid' constructor check
        var entity = FeatureScalingPriceEntity.builder()
                // --- Base Abstract Fields ---
                .featureId(PkIdVO.fromString(1L))
                .featureUuId(UuIdVO.fromString(FEATURE_ID))
                .featureName(NameVO.from("Enterprise Storage"))
                .featureLabel(LabelVO.from("ENT-STORAGE"))
                .featureDescription(DescriptionVO.from("Scaling storage for enterprises"))
                .featureStatus(StatusEnums.ACTIVE)
                .featureVersion(VersionVO.from("1.0.0"))
                .lastModified(LastModifiedVO.now())

                // --- Subclass Specific Field ---
                // Using the correct method name from your ScalingPriceEntity: 'addScalingScheme'
                .addScalingScheme(Currency.getInstance("EUR"), createValidScalingPriceVO(100.0, 10.0, "EUR"))
                .build();

        assertAll(
                () -> assertEquals("Enterprise Storage", entity.getFeatureName().getValue()),
                () -> assertTrue(entity.supportsCurrency(Currency.getInstance("EUR"))),
                () -> assertEquals(1, entity.getScalingPriceSchemes().size())
        );
    }


    @Nested
    @DisplayName("Invariant Protection: Invalid Inputs")
    class InvariantTests {

        @Test
        @DisplayName("Should throw DomainValidationException if incompatible with itself")
        void selfIncompatibilityTest() {
            var selfId = UuIdVO.fromString(FEATURE_ID);
            var builder = FeatureScalingPriceEntity.builder()
                    .featureUuId(selfId)
                    .incompatibleFeatures(Set.of(selfId));

            assertThrows(DomainValidationException.class, builder::build);
        }

        @Test
        @DisplayName("Should throw DomainValidationException if Map key mismatch with VO Currency")
        void currencyKeyMismatchTest() {
            // Arrange: Create a valid ScalingPriceVO with USD currency directly within the test
            BigDecimal baseThreshold = BigDecimal.valueOf(10.0);
            BigDecimal basePrice = BigDecimal.valueOf(1.0);
            BigDecimal incrementStep = BigDecimal.valueOf(1.0);
            BigDecimal pricePerStep = BigDecimal.valueOf(0.5);
            Currency usdCurrency = Currency.getInstance("USD");

            var usdVO = new ScalingPriceVO(
                    "unitName",           // Example unit
                    baseThreshold,
                    basePrice,
                    incrementStep,
                    pricePerStep,
                    usdCurrency.getDefaultFractionDigits(),
                    usdCurrency
            );

            // Create the builder and add a scaling scheme with EUR as key (mismatch)
            var builder = FeatureScalingPriceEntity.builder()
                    .featureUuId(UuIdVO.fromString(FEATURE_ID))
                    .addScalingScheme(Currency.getInstance("EUR"), usdVO); // EUR key vs USD value

            // Act & Assert: Expecting DomainValidationException when building the entity
            DomainValidationException exception = assertThrows(DomainValidationException.class, builder::build);

            // Optional: Assert the message to ensure it is informative
            assertTrue(exception.getMessage().contains("Mismatch: Key [EUR] does not match VO currency [USD]"));
        }


    }

    @Test
    @DisplayName("Immutability: Internal state must remain unchanged if external map is modified")
    void defensiveCopyTest() {
        Map<Currency, ScalingPriceVO> mutableSchemes = new HashMap<>();
        mutableSchemes.put(EUR, createValidScalingPriceVO(50.0, 5.0, EUR));

        var entity = FeatureScalingPriceEntity.builder()
                .featureUuId(UuIdVO.fromString(FEATURE_ID))
                .scalingPriceSchemes(mutableSchemes)
                .build();

        mutableSchemes.clear(); // Modify external source

        assertFalse(entity.getScalingPriceSchemes().isEmpty(),
                "Entity must hold a defensive copy of the pricing schemes.");
    }

    @Test
    @DisplayName("Identity: Entities with same UuId are equal even if names differ")
    void identityEqualityTest() {
        var id = UuIdVO.fromString(FEATURE_ID);
        var entity1 = FeatureScalingPriceEntity.builder()
                .featureUuId(id).featureName(NameVO.from("A")).build();
        var entity2 = FeatureScalingPriceEntity.builder()
                .featureUuId(id).featureName(NameVO.from("B")).build();

        assertEquals(entity1, entity2, "DDD Entities should be equal based on identity (UuId).");
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Nested
    @DisplayName("Logic: Price Calculation")
    class LogicTests {

        @Test
        @DisplayName("Should calculate price for normal quantity")
        void calculatePriceNormal() {
            // Arrange: Create the entity with a valid scaling price scheme
            var entity = FeatureScalingPriceEntity.builder()
                    .featureUuId(UuIdVO.fromString(FEATURE_ID))
                    .addScalingScheme(EUR, createValidScalingPriceVO(100.0, 20.0, EUR)) // Base price is 100, step price is 20
                    .build();

            // Assuming that the quantity is above the base threshold
            BigDecimal quantity = BigDecimal.valueOf(120); // Example quantity to test
            // Logic: Base (100) + ((Quantity (120) - Base Threshold (100)) / Increment Step (20)) * Price per Step (20)
            BigDecimal expectedPrice = BigDecimal.valueOf(100).add(
                    quantity.subtract(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(20), RoundingMode.CEILING)
                            .multiply(BigDecimal.valueOf(20))
            );

            // Act: Calculate the price for the given quantity
            BigDecimal result = entity.calculatePrice(quantity);

            // Assert: Check if the calculated price matches the expected price
            assertEquals(0, expectedPrice.compareTo(result), "Calculated price does not match expected price.");
        }

        @Test
        @DisplayName("Should throw exception for unregistered currency")
        void calculatePriceMissingCurrency() {
            // Arrange: Create the entity with a valid scaling price scheme
            var entity = FeatureScalingPriceEntity.builder()
                    .featureUuId(UuIdVO.fromString(FEATURE_ID))
                    .addScalingScheme(EUR, createValidScalingPriceVO(10.0, 1.0, EUR))
                    .build();

            // Act & Assert: Expecting DomainValidationException for missing currency
            assertThrows(DomainValidationException.class, () ->
                    entity.calculatePrice(BigDecimal.ONE) // Attempting to calculate with JPY, which is unregistered
            );
        }

    }

    private ScalingPriceVO createValidScalingPriceVO(double base, double stepPrice, Currency curr) {
        return ScalingPriceVO.of("unit", BigDecimal.ZERO, BigDecimal.valueOf(base),
                BigDecimal.ONE, BigDecimal.valueOf(stepPrice), curr);
    }

}

 */