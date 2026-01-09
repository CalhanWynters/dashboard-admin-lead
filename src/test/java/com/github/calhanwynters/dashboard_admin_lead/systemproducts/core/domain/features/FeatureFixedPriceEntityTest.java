package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.exceptions.DomainValidationException;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.util.*;

import static java.util.Collections.fill;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


@DisplayName("FeatureFixedPriceEntity High-Integrity Domain Tests")
class FeatureFixedPriceEntityTest {

    // --- Test Data Factories ---
    private static final UuIdVO VALID_UUID = UuIdVO.generate();
    private static final UuIdVO VARIANT_UUID = UuIdVO.generate();
    private static final PkIdVO VALID_PK = PkIdVO.of(101L);
    private static final NameVO VALID_NAME = NameVO.from("Core Engine");
    private static final LabelVO VALID_LABEL = LabelVO.from("CORE-01");
    private static final DescriptionVO VALID_DESC = DescriptionVO.from("Primary logic processor");
    private static final StatusEnums VALID_STATUS = StatusEnums.ACTIVE;
    private static final VersionVO VALID_VERSION = VersionVO.from("1.0.0");
    private static final LastModifiedVO VALID_MODIFIED = LastModifiedVO.now();

    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency JPY = Currency.getInstance("JPY");

    protected <B extends FeatureAbstractClass.Builder<B>> B fill(B builder) {
        return builder
                .featureId(VALID_PK)
                .featureUuId(VALID_UUID)
                .featureName(VALID_NAME)
                .featureLabel(VALID_LABEL)
                .featureDescription(VALID_DESC)
                .featureStatus(VALID_STATUS)
                .featureVersion(VALID_VERSION)
                .lastModified(VALID_MODIFIED);
    }

    @Nested
    @DisplayName("1. Constructor Prologue Logic (Java 25 JEP 513)")
    class ConstructorPrologueTests {

        @Test
        @DisplayName("should fail validation in prologue when prices are missing")
        void should_reject_empty_prices() {
            // Under JEP 513, the 'prologue' (pre-super() calls) handles this validation.
            // We simulate this by checking if the builder's build() triggers the Prologue exception.
            var builder = FeatureFixedPriceEntity.builder().featureName(new NameVO("Standard Support"));

            assertThatThrownBy(builder::build)
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("Must define at least one price scheme.");
        }


    }

    @Nested
    @DisplayName("2. Fluent Builder & Recursive Generics")
    class BuilderTests {

        @Test
        @DisplayName("Should support full fluent chain with subclass-specific methods")
        void testRecursiveGenericBuilder() {
            // The fill() method returns B, allowing immediate chaining of .addPrice()
            FeatureFixedPriceEntity entity = fill(FeatureFixedPriceEntity.builder())
                    .featVariantId(VARIANT_UUID)
                    .addPrice(USD, new PriceVO(new BigDecimal("99.99"), USD))
                    .build();

            assertThat(entity).isNotNull();
            assertThat(entity.getPriceForCurrency(USD)).isPresent();
        }


    }


    @Nested
    @DisplayName("Builder Business Rules & Hardened PriceVO")
    class BuilderValidationTests {


        @Test
        @DisplayName("should reject zero prices via PriceVO/Builder logic")
        void should_reject_zero_prices() {
            // 1. Initialize builder with valid data first
            FeatureFixedPriceEntity.Builder builder = fill(FeatureFixedPriceEntity.builder());

            // 2. Wrap the specific action that triggers the violation
            assertThatThrownBy(() -> {
                builder.addPrice(USD, new PriceVO(BigDecimal.ZERO, USD));
                builder.build(); // Ensure validation is caught whether in addPrice or build
            })
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("cannot be zero");
        }

        @Test
        @DisplayName("should reject currency mismatch between key and PriceVO")
        void should_reject_currency_mismatch() {
            // 1. Arrange: Use the recursive generic fill() to populate mandatory base fields
            FeatureFixedPriceEntity.Builder builder = fill(FeatureFixedPriceEntity.builder());

            // 2. Prepare valid and invalid currency/price pairs
            Currency usd = Currency.getInstance("USD");
            Currency jpy = Currency.getInstance("JPY");
            PriceVO usdPrice = new PriceVO(new BigDecimal("10.00"), usd);

            // 3. Act & Assert: Adding a valid price should pass silently
            builder.addPrice(usd, usdPrice);

            // 4. Act & Assert: Adding a mismatched currency must fail fast
            // We expect the DomainValidationException to occur immediately in .addPrice()
            assertThatThrownBy(() -> builder.addPrice(jpy, usdPrice))
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("Currency Mismatch")
                    .hasMessageContaining("Key [JPY]")
                    .hasMessageContaining("PriceVO currency [USD]");
        }

        @Test
        @DisplayName("should respect currency scale (e.g., no fractional pennies for JPY)")
        void should_respect_currency_scale_logic() {
            // JPY does not allow decimal places (scale 0)
            assertThatThrownBy(() -> new PriceVO(new BigDecimal("100.50"), JPY))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("exceeds currency JPY allowed precision (0)");
        }

    }

    @Nested
    @DisplayName("Immutability & Defensive Copying")
    class ImmutabilityTests {

        @Test
        @DisplayName("should be immune to mutation of the source map AND return an immutable view")
        void should_perform_defensive_copying_and_enforce_immutability() {
            // 1. Arrange: Create mutable source to verify internal state isolation
            Map<Currency, PriceVO> mutableMap = new HashMap<>();
            mutableMap.put(USD, new PriceVO(new BigDecimal("49.99"), USD));

            // Use fill() to satisfy Abstract Class mandatory fields fluently
            FeatureFixedPriceEntity entity = fill(FeatureFixedPriceEntity.builder())
                    .fixedPrices(mutableMap)
                    .build();

            // 2. Act: Mutation of Source Guard
            mutableMap.clear();

            // 3. Assert Source Guard: Entity must retain its state (Defensive Copying)
            assertThat(entity.getFixedPrices())
                    .as("Check that entity performed a defensive copy of the source map")
                    .hasSize(1)
                    .containsEntry(USD, new PriceVO(new BigDecimal("49.99"), USD));

            // 4. Act & Assert: Output Guard (2026 Standard)
            Map<Currency, PriceVO> pricesFromEntity = entity.getFixedPrices();

            assertThatThrownBy(() -> pricesFromEntity.put(JPY, new PriceVO(BigDecimal.TEN, JPY)))
                    .as("The getter must return an unmodifiable view to prevent external mutation")
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("should return unmodifiable collections from getters")
        void should_return_unmodifiable_collections() {
            // 1. Arrange: Inline creation using fill() + specific state
            FeatureFixedPriceEntity entity = fill(FeatureFixedPriceEntity.builder())
                    .addPrice(USD, new PriceVO(new BigDecimal("19.99"), USD))
                    .build();

            // 2. Act & Assert: Verify domain boundary protection via unmodifiable view
            assertThatThrownBy(() ->
                    entity.getFixedPrices().put(JPY, new PriceVO(BigDecimal.TEN, JPY))
            )
                    .as("The domain entity must not leak mutable state via getters")
                    .isInstanceOf(UnsupportedOperationException.class);
        }


    }


    @Nested
    @DisplayName("Base Class Invariants")
    class BaseClassTests {


        @Test
        @DisplayName("should prevent a feature from being incompatible with itself")
        void should_prevent_self_incompatibility() {
            // 1. Arrange: Define the identity to be used for the collision
            UuIdVO selfId = UuIdVO.generate();

            // 2. Act: Use fill() for mandatory base fields, then override the UUID
            // and add the incompatible self-reference.
            FeatureFixedPriceEntity.Builder builder = fill(FeatureFixedPriceEntity.builder())
                    .featureUuId(selfId) // Overrides the default UUID from fill()
                    .addPrice(USD, new PriceVO(BigDecimal.TEN, USD))
                    .addIncompatibleFeature(selfId);

            // 3. Assert: Validation must trigger during the 'Constructor Prologue' (build phase)
            assertThatThrownBy(builder::build)
                    .as("Business Rule: A feature cannot be marked as incompatible with itself")
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("cannot be marked as incompatible with itself");
        }


    }

}

