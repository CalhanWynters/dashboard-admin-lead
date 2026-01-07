package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.exceptions.DomainValidationException;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("FeatureFixedPriceEntity High-Integrity Domain Tests")
class FeatureFixedPriceEntityTest {

    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency JPY = Currency.getInstance("JPY");

    @Nested
    @DisplayName("Constructor Prologue (Java 25 JEP 513)")
    class ConstructorPrologueTests {

        @Test
        @DisplayName("should reject empty prices via fluent builder validation")
        void should_reject_empty_prices() {
            // 1. Get the builder and use parent methods fluently
            // No casting required! .featureName() now returns FeatureFixedPriceEntity.Builder
            FeatureFixedPriceEntity.Builder builder = FeatureFixedPriceEntity.builder()
                    .featureName(new NameVO("Standard Support"));

            // 2. Verify that build fails when cross-field rules are violated
            assertThatThrownBy(builder::build)
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("must define at least one price scheme");
        }

    }

    @Nested
    @DisplayName("Builder Business Rules & Hardened PriceVO")
    class BuilderValidationTests {

        @Test
        @DisplayName("should reject zero prices via PriceVO/Builder logic")
        void should_reject_zero_prices() {
            var usd = Currency.getInstance("USD");

            // Demonstrate fluency: Parent method -> Child method -> Action
            FeatureFixedPriceEntity.Builder builder = FeatureFixedPriceEntity.builder()
                    .featureName(new NameVO("Premium Feature"))
                    .addPrice(usd, new PriceVO(BigDecimal.ZERO, usd));

            assertThatThrownBy(builder::build)
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("cannot be zero");
        }


        @Test
        @DisplayName("should reject currency mismatch between key and PriceVO")
        void should_reject_currency_mismatch() {
            // Create the builder for the FeatureFixedPriceEntity
            FeatureFixedPriceEntity.Builder builder = FeatureFixedPriceEntity.builder();

            // Create a PriceVO for USD
            PriceVO usdPrice = new PriceVO(new BigDecimal("10.00"), Currency.getInstance("USD"));

            // Adding the USD price should work
            builder.addPrice(Currency.getInstance("USD"), usdPrice);

            // Now attempt to build the entity which should validate all prices
            // including the next price addition which refers to JPY
            assertThatThrownBy(() -> {
                builder.addPrice(Currency.getInstance("JPY"), usdPrice);
                builder.build(); // Trigger the build to validate
            })
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("Currency Mismatch");
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
            Map<Currency, PriceVO> mutableMap = new HashMap<>();
            mutableMap.put(Currency.getInstance("USD"), new PriceVO(new BigDecimal("49.99"), Currency.getInstance("USD")));

            // Create a builder instance, set all necessary attributes, and pass the mutableMap
            FeatureFixedPriceEntity entity = FeatureFixedPriceEntity.builder()
                    .featureUuId(UuIdVO.generate())
                    .featureName(new NameVO("Default Feature"))
                    .featureId(PkIdVO.of(1L))
                    .featureLabel(new LabelVO("Test Label"))
                    .featureDescription(new DescriptionVO("A default feature"))
                    .featureStatus(StatusEnums.ACTIVE)
                    .featureVersion(VersionVO.from("1.0.0"))
                    .lastModified(LastModifiedVO.now())
                    .fixedPrices(mutableMap) // Use mutableMap for initial fixedPrices
                    .build();

            // 1. Mutation of Source Guard
            mutableMap.clear(); // Clear the original map
            assertThat(entity.getFixedPrices()).hasSize(1); // Ensure the entity still has the price

            // 2. Mutation of Output Guard (2026 Standard)
            // Ensures the getter doesn't leak a mutable reference
            Map<Currency, PriceVO> pricesFromEntity = entity.getFixedPrices();
            assertThrows(UnsupportedOperationException.class, () ->
                    pricesFromEntity.put(Currency.getInstance("USD"), new PriceVO(BigDecimal.ZERO, Currency.getInstance("USD")))
            );
        }



        @Test
        @DisplayName("should return unmodifiable collections from getters")
        void should_return_unmodifiable_collections() {
            FeatureFixedPriceEntity entity = createValidEntity();

            assertThatThrownBy(() -> entity.getFixedPrices().put(JPY, new PriceVO(BigDecimal.TEN, JPY)))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("Base Class Invariants")
    class BaseClassTests {


        @Test
        @DisplayName("should prevent a feature from being incompatible with itself")
        void should_prevent_self_incompatibility() {
            UuIdVO selfId = UuIdVO.generate(); // Generate self ID
            Set<UuIdVO> incompatibles = Set.of(selfId); // Set of incompatible features

            // Setup builder with all mandatory fields + the self-incompatibility
            FeatureFixedPriceEntity.Builder builder = FeatureFixedPriceEntity.builder()
                    .featureUuId(selfId) // Feature UUID
                    .featureName(new NameVO("Default Feature")) // Feature name
                    .featureId(PkIdVO.of(1L)) // Example PK ID
                    .featureLabel(new LabelVO("Test Label")) // Mandatory field
                    .featureDescription(new DescriptionVO("A default feature")) // Mandatory field
                    .featureStatus(StatusEnums.ACTIVE) // Mandatory field
                    .featureVersion(VersionVO.from("1.0.0")) // Mandatory field
                    .lastModified(LastModifiedVO.now()) // Mandatory field
                    .addPrice(USD, new PriceVO(BigDecimal.TEN, USD)) // Mandatory child state
                    .addIncompatibleFeature(selfId); // Pass self ID directly (if method supports single ID)

            // Validate that instantiation throws an exception
            assertThatThrownBy(builder::build)
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("cannot be marked as incompatible with itself");
        }


    }

    private FeatureFixedPriceEntity createValidEntity() {
        // 1. Rename .withId to .featureUuId (assuming UuIdVO is for the UUID identifier)
        // 2. Add all other *mandatory* fields from FeatureAbstractClass (PkIdVO, LabelVO, StatusEnums, etc.)

        return FeatureFixedPriceEntity.builder()
                .featureUuId(UuIdVO.generate()) // Corrected syntax: added parentheses ()
                .featureName(new NameVO("Default Feature")) // Corrected name from withName
                .featureId(PkIdVO.of(1L)) // Example: assuming you need this PK ID
                .featureLabel(new LabelVO("Test Label")) // Mandatory Field
                .featureDescription(new DescriptionVO("A default feature")) // Mandatory Field
                .featureStatus(StatusEnums.ACTIVE) // Mandatory Field
                .featureVersion(VersionVO.from("1.0.0")) // Mandatory Field
                .lastModified(LastModifiedVO.now()) // Mandatory Field
                .addPrice(USD, new PriceVO(new BigDecimal("19.99"), USD))
                .build();
    }

}
