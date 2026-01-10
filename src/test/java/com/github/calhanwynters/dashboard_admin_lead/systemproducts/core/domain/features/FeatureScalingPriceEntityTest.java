package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.exceptions.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


class FeatureScalingPriceEntityTest {

    // --- Test Data Factories ---
    private static final UuIdVO VALID_UUID = UuIdVO.generate();
    private static final PkIdVO VALID_PK = PkIdVO.of(101L);
    private static final NameVO VALID_NAME = NameVO.from("Core Engine");
    private static final LabelVO VALID_LABEL = LabelVO.from("CORE-01");
    private static final DescriptionVO VALID_DESC = DescriptionVO.from("Primary logic processor");
    private static final StatusEnums VALID_STATUS = StatusEnums.ACTIVE;
    private static final VersionVO VALID_VERSION = VersionVO.from("1.0.0");
    private static final LastModifiedVO VALID_MODIFIED = LastModifiedVO.now();

    private static final Currency EUR = Currency.getInstance("EUR");

    @Test
    @DisplayName("Recursive Builder: Should chain base and subclass fields in one statement")
    void recursiveBuilderFluencyTest() {
        var entity = FeatureScalingPriceEntity.builder()
                // All 8 mandatory base fields + subclass fields
                .featureId(VALID_PK)
                .featureUuId(VALID_UUID)
                .featureName(VALID_NAME)
                .featureLabel(VALID_LABEL)
                .featureDescription(VALID_DESC)
                .featureStatus(VALID_STATUS)
                .featureVersion(VALID_VERSION)
                .lastModified(VALID_MODIFIED)
                .addScalingScheme(EUR, createValidScalingPriceVO(0.0, 100.0, 1.0, 10.0, EUR))

                .build();

        assertAll(
                () -> assertEquals(VALID_NAME.value(), entity.getFeatureName().value()),
                () -> assertTrue(entity.supportsCurrency(EUR)),
                () -> assertEquals(1, entity.getScalingPriceSchemes().size())
        );
    }

    @Nested
    @DisplayName("Logic: Robust Multi-Currency Calculation")
    class LogicTests {

        @ParameterizedTest(name = "Currency {0} handles decimals correctly")
        @CsvSource({
                "EUR, 120, 140.00", // EUR: Expected price has 2 decimal places
                "JPY, 120, 140",    // JPY: Expected price has 0 decimal places
                "BHD, 120, 140.000" // BHD: Expected price has 3 decimal places
        })
        void calculatePriceAcrossCurrencies(String currencyCode, double quantity, String expectedPriceStr) {
            Currency currency = Currency.getInstance(currencyCode);
            BigDecimal expectedPrice = new BigDecimal(expectedPriceStr).setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);

            // Build the entity with a valid scaling price
            var entity = FeatureScalingPriceEntity.builder()
                    .featureId(VALID_PK)
                    .featureUuId(VALID_UUID)
                    .featureName(VALID_NAME)
                    .featureLabel(VALID_LABEL)
                    .featureDescription(VALID_DESC)
                    .featureStatus(VALID_STATUS)
                    .featureVersion(VALID_VERSION)
                    .lastModified(VALID_MODIFIED)
                    .addScalingScheme(currency, createValidScalingPriceVO(100.0, 100.0, 10.0, 20.0, currency))
                    .build();

            // Calculate the price
            BigDecimal result = entity.calculatePrice(currency, BigDecimal.valueOf(quantity));

            // Debugging output for comparison
            System.out.printf("Currency: %s, Quantity: %.2f, Expected Price: %s, Calculated Price: %s%n",
                    currencyCode, quantity, expectedPrice, result);

            // Assertions
            assertAll(
                    () -> assertEquals(0, expectedPrice.compareTo(result),
                            String.format("Expected price %s does not match calculated price %s for currency %s", expectedPrice, result, currencyCode)),
                    () -> assertEquals(currency.getDefaultFractionDigits(), result.scale(),
                            String.format("The scale of the calculated price %s is incorrect for currency %s", result, currencyCode))
            );
        }


        @Test
        @DisplayName("Should throw exception for unregistered currency")
        void calculatePriceMissingCurrency() {
            var entity = FeatureScalingPriceEntity.builder()
                    .featureId(VALID_PK)
                    .featureUuId(VALID_UUID)
                    .featureName(VALID_NAME)
                    .featureLabel(VALID_LABEL)
                    .featureDescription(VALID_DESC)
                    .featureStatus(VALID_STATUS)
                    .featureVersion(VALID_VERSION)
                    .lastModified(VALID_MODIFIED)
                    .addScalingScheme(EUR, createValidScalingPriceVO(0.0, 100.0, 1.0, 10.0, EUR))
                    .build();

            assertThrows(DomainValidationException.class, () ->
                    entity.calculatePrice(Currency.getInstance("JPY"), BigDecimal.ONE));
        }


    }

    @Nested
    @DisplayName("Invariant Protection: Invalid Inputs")
    class InvariantTests {

        @Test
        void selfIncompatibilityTest() {
            var builder = FeatureScalingPriceEntity.builder()
                    .featureId(VALID_PK)
                    .featureUuId(VALID_UUID)
                    .featureName(VALID_NAME)
                    .featureLabel(VALID_LABEL)
                    .featureDescription(VALID_DESC)
                    .featureStatus(VALID_STATUS)
                    .featureVersion(VALID_VERSION)
                    .lastModified(VALID_MODIFIED)
                    .addScalingScheme(EUR, createValidScalingPriceVO(0.0, 100.0, 1.0, 10.0, EUR))
                    .incompatibleFeatures(Set.of(VALID_UUID));

            // Now correctly fails build due to domain invariant, not null checks
            assertThrows(DomainValidationException.class, builder::build);
        }


        @Test
        void currencyKeyMismatchTest() {
            Currency usd = Currency.getInstance("USD");
            var usdVO = ScalingPriceVO.of("unit-usage", BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE, usd);

            var builder = FeatureScalingPriceEntity.builder()
                    .featureId(VALID_PK)
                    .featureUuId(VALID_UUID)
                    .featureName(VALID_NAME)
                    .featureLabel(VALID_LABEL)
                    .featureDescription(VALID_DESC)
                    .featureStatus(VALID_STATUS)
                    .featureVersion(VALID_VERSION)
                    .lastModified(VALID_MODIFIED)
                    .addScalingScheme(EUR, usdVO);

            assertThrows(DomainValidationException.class, builder::build);
        }

    }

    @Test
    @DisplayName("Immutability: Internal state must remain unchanged")
    void defensiveCopyTest() {
        Map<Currency, ScalingPriceVO> mutableSchemes = new HashMap<>();
        mutableSchemes.put(EUR, createValidScalingPriceVO(10.0, 50.0, 5.0, 20.0, EUR));

        var entity = FeatureScalingPriceEntity.builder()
                .featureId(VALID_PK)
                .featureUuId(VALID_UUID)
                .featureName(VALID_NAME)
                .featureLabel(VALID_LABEL)
                .featureDescription(VALID_DESC)
                .featureStatus(VALID_STATUS)
                .featureVersion(VALID_VERSION)
                .lastModified(VALID_MODIFIED)
                .scalingPriceSchemes(mutableSchemes)
                .build();

        mutableSchemes.clear();
        assertFalse(entity.getScalingPriceSchemes().isEmpty());
    }

    @Test
    @DisplayName("Identity: Entities with same UuId are equal even if names differ")
    void identityEqualityTest() {
        var scheme = createValidScalingPriceVO(10.0, 50.0, 5.0, 20.0, EUR);

        var entity1 = FeatureScalingPriceEntity.builder()
                .featureId(PkIdVO.of(101L))
                .featureUuId(VALID_UUID)
                .featureName(NameVO.from("Feature-Alpha"))
                .featureLabel(VALID_LABEL)
                .featureDescription(VALID_DESC)
                .featureStatus(VALID_STATUS)
                .featureVersion(VALID_VERSION)
                .lastModified(VALID_MODIFIED)
                .addScalingScheme(EUR, scheme)
                .build();

        var entity2 = FeatureScalingPriceEntity.builder()
                .featureId(PkIdVO.of(102L)) // Distinct PK, same UUID
                .featureUuId(VALID_UUID)
                .featureName(NameVO.from("Feature-Beta"))
                .featureLabel(VALID_LABEL)
                .featureDescription(VALID_DESC)
                .featureStatus(VALID_STATUS)
                .featureVersion(VALID_VERSION)
                .lastModified(VALID_MODIFIED)
                .addScalingScheme(EUR, scheme)
                .build();

        assertAll(
                () -> assertEquals(entity1, entity2, "Entities must be equal based on identity (UUID)"),
                () -> assertEquals(entity1.hashCode(), entity2.hashCode())
        );
    }



    private ScalingPriceVO createValidScalingPriceVO(
            double threshold,
            double basePrice,
            double step,
            double stepPrice,
            Currency currency) {

        int scale = currency.getDefaultFractionDigits();
        return new ScalingPriceVO(
                "unit-usage",
                BigDecimal.valueOf(threshold),
                BigDecimal.valueOf(basePrice).setScale(scale, RoundingMode.HALF_UP),
                BigDecimal.valueOf(step),
                BigDecimal.valueOf(stepPrice).setScale(scale, RoundingMode.HALF_UP),
                scale,
                currency
        );
    }


}
