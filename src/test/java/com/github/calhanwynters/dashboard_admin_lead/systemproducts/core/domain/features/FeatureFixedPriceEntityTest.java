package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

public class FeatureFixedPriceEntityTest {

    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency GBP = Currency.getInstance("GBP");

    @Nested
    @DisplayName("Normal & Boundary Input Testing")
    class HappyPathTests {
        @Test
        @DisplayName("Should build entity with multiple valid currencies")
        void shouldBuildWithMultipleCurrencies() {
            Map<Currency, PriceVO> prices = Map.of(
                    USD, new PriceVO(new BigDecimal("10.00"), 2, USD),
                    EUR, new PriceVO(new BigDecimal("9.00"), 2, EUR),
                    GBP, new PriceVO(new BigDecimal("8.00"), 2, GBP)
            );

            FeatureFixedPriceEntity entity = createBaseBuilder()
                    .fixedPrices(prices)
                    .isUnique(false)
                    .build();

            assertThat(entity.getFixedPrices()).hasSize(3)
                    .containsEntry(USD, new PriceVO(new BigDecimal("10.00"), 2, USD));
        }

        @Test
        @DisplayName("Verify Defaulting logic: Empty prices default to USD 0.00")
        void shouldDefaultToZeroUsd() {
            FeatureFixedPriceEntity entity = createBaseBuilder()
                    .fixedPrices(null)
                    .build();

            assertThat(entity.getFixedPrices()).hasSize(1);
            PriceVO usdPrice = entity.getFixedPrices().get(USD);
            assertThat(usdPrice.price()).isEqualByComparingTo("0.00");
            assertThat(usdPrice.currency()).isEqualTo(USD);
        }

        @Test
        @DisplayName("Boundary: building with isUnique(true) and one price succeeds")
        void shouldSucceedWithSinglePriceWhenUnique() {
            Map<Currency, PriceVO> prices = Map.of(USD, new PriceVO(BigDecimal.TEN, 2, USD));

            assertThatCode(() -> createBaseBuilder()
                    .isUnique(true)
                    .fixedPrices(prices)
                    .build()
            ).doesNotThrowAnyException();
        }

    }

    @Nested
    @DisplayName("Invalid Input Testing (Defensive Fortress)")
    class DefensiveTesting {

        @Test
        @DisplayName("Throw IllegalStateException if PriceVO currency mismatched with Map Key")
        void shouldThrowOnCurrencyMismatch() {
            // Put EUR price record under USD map key
            Map<Currency, PriceVO> mismatched = Map.of(USD, new PriceVO(BigDecimal.TEN, 2, EUR));

            assertThatThrownBy(() -> createBaseBuilder().fixedPrices(mismatched).build())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("mismatch")
                    .hasMessageContaining("USD");
        }

        @Test
        @DisplayName("Verify null keys or PriceVO values trigger NullPointerException")
        void shouldThrowOnNullEntries() {
            Map<Currency, PriceVO> nullKeyMap = new HashMap<>();
            nullKeyMap.put(null, new PriceVO(BigDecimal.TEN, 2, USD));

            assertThatThrownBy(() -> createBaseBuilder().fixedPrices(nullKeyMap).build())
                    .isInstanceOf(NullPointerException.class);
        }

        @ParameterizedTest(name = "Should throw {2} when {1} is null")
        @MethodSource("provideMandatoryFieldMutators")
        @DisplayName("Verify 9 base fields enforce null safety via Builder")
        void shouldEnforceMandatoryFields(
                Consumer<FeatureFixedPriceEntity.Builder> mutator,
                String fieldName,
                Class<? extends Throwable> expectedException,
                String expectedMessage) {

            FeatureFixedPriceEntity.Builder builder = createBaseBuilder();
            mutator.accept(builder);

            assertThatThrownBy(builder::build)
                    .isInstanceOf(expectedException)
                    .hasMessageContaining(expectedMessage);
        }

        private static Stream<Arguments> provideMandatoryFieldMutators() {
            return Stream.of(
                    // Fields validated by Builder.validate() (IllegalStateException)
                    Arguments.of((Consumer<FeatureFixedPriceEntity.Builder>) b -> b.featureName(null),
                            "featureName", IllegalStateException.class, "Naming VOs are required"),
                    Arguments.of((Consumer<FeatureFixedPriceEntity.Builder>) b -> b.featureLabel(null),
                            "featureLabel", IllegalStateException.class, "Naming VOs are required"),

                    // Fields validated by FeatureAbstractClass constructor (NullPointerException)
                    Arguments.of((Consumer<FeatureFixedPriceEntity.Builder>) b -> b.featureId(null),
                            "featureId", NullPointerException.class, "Feature ID must not be null"),
                    Arguments.of((Consumer<FeatureFixedPriceEntity.Builder>) b -> b.featureUuId(null),
                            "featureUuId", NullPointerException.class, "Feature UUID must not be null"),
                    Arguments.of((Consumer<FeatureFixedPriceEntity.Builder>) b -> b.featureDescription(null),
                            "featureDescription", NullPointerException.class, "Feature Description must not be null"),
                    Arguments.of((Consumer<FeatureFixedPriceEntity.Builder>) b -> b.featureStatus(null),
                            "featureStatus", NullPointerException.class, "Feature Status must not be null"),
                    Arguments.of((Consumer<FeatureFixedPriceEntity.Builder>) b -> b.featureVersion(null),
                            "featureVersion", NullPointerException.class, "Feature Version must not be null"),
                    Arguments.of((Consumer<FeatureFixedPriceEntity.Builder>) b -> b.lastModified(null),
                            "lastModified", NullPointerException.class, "Last Modified indicator must not be null"),
                    Arguments.of((Consumer<FeatureFixedPriceEntity.Builder>) b -> b.isUnique(null),
                            "isUnique", NullPointerException.class, "isUnique flag must not be null")
            );
        }
    }

    @Nested
    @DisplayName("Cross-Field Consistency (Semantic Rules)")
    class SemanticRules {

        @Test
        @DisplayName("Throw if isUnique is true but map contains multiple currencies")
        void shouldThrowWhenUniqueHasMultiCurrency() {
            Map<Currency, PriceVO> multiCurrency = Map.of(
                    USD, new PriceVO(BigDecimal.TEN, 2, USD),
                    EUR, new PriceVO(BigDecimal.ONE, 2, EUR)
            );

            assertThatThrownBy(() -> createBaseBuilder()
                    .isUnique(true)
                    .fixedPrices(multiCurrency)
                    .build())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Unique (variant-specific) features cannot have multi-currency price lists.");
        }
    }

    @Nested
    @DisplayName("Extreme Input & Immutability")
    class ImmutabilityTests {

        @Test
        @DisplayName("Verify getFixedPrices() returns an immutable map")
        void returnedMapShouldBeImmutable() {
            FeatureFixedPriceEntity entity = createBaseBuilder().build();
            Map<Currency, PriceVO> prices = entity.getFixedPrices();

            assertThatThrownBy(() -> prices.put(GBP, new PriceVO(BigDecimal.ONE, 2, GBP)))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("Defensive Copying: Modifying external map shouldn't affect Entity")
        void shouldPerformDefensiveCopying() {
            Map<Currency, PriceVO> mutableMap = new HashMap<>();
            mutableMap.put(USD, new PriceVO(BigDecimal.TEN, 2, USD));

            FeatureFixedPriceEntity entity = createBaseBuilder()
                    .fixedPrices(mutableMap)
                    .build();

            mutableMap.put(EUR, new PriceVO(BigDecimal.ONE, 2, EUR));

            assertThat(entity.getFixedPrices()).hasSize(1)
                    .doesNotContainKey(EUR);
        }

    }

    @Nested
    @DisplayName("Identity Contract")
    class IdentityTests {

        @Test
        @DisplayName("Entities equal if featureUuId matches, regardless of prices")
        void shouldBeEqualByUuId() {
            UuIdVO commonUuid = UuIdVO.generate();

            FeatureFixedPriceEntity entity1 = createBaseBuilder()
                    .featureUuId(commonUuid)
                    .fixedPrices(Map.of(USD, new PriceVO(BigDecimal.TEN, 2, USD)))
                    .build();

            FeatureFixedPriceEntity entity2 = createBaseBuilder()
                    .featureUuId(commonUuid)
                    .fixedPrices(Map.of(EUR, new PriceVO(BigDecimal.ONE, 2, EUR)))
                    .build();

            assertThat(entity1).isEqualTo(entity2);
            assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
        }
    }

    /**
     * Helper to create a builder with all mandatory fields pre-populated.
     */
    private static FeatureFixedPriceEntity.Builder createBaseBuilder() {
        return FeatureFixedPriceEntity.builder()
                .featureId(new PkIdVO(1L))
                .featureUuId(UuIdVO.generate())
                .featureName(new NameVO("Test Feature"))
                .featureLabel(new LabelVO("Label"))
                .featureDescription(new DescriptionVO("Description"))
                .featureStatus(StatusEnums.ACTIVE)
                .featureVersion(new VersionVO(1))
                .lastModified(LastModifiedVO.now())
                .isUnique(false);
    }
}
