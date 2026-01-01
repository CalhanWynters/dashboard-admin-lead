package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.exceptions.DomainValidationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Enterprise-grade test suite for FeatureScalingPriceEntity.
 * Updated for Java 25 (LTS) Baseline - Jan 2026.
 */
@DisplayName("FeatureScalingPriceEntity Domain Logic Tests")
@MockitoSettings(strictness = Strictness.LENIENT)
class FeatureScalingPriceEntityTest {

    @Mock private PkIdVO mockId;
    @Mock private UuIdVO mockUuid;
    @Mock private NameVO mockName;
    @Mock private LabelVO mockLabel;
    @Mock private DescriptionVO mockDesc;
    @Mock private VersionVO mockVersion;
    @Mock private LastModifiedVO mockModified;

    private static final Currency USD = Currency.getInstance("USD");
    private Map<Currency, ScalingPriceVO> validSchemes;

    @BeforeEach
    void setUp() {
        validSchemes = new HashMap<>();
        // Using ScalingPriceVO.of factory to initialize standard test data
        var standardVo = ScalingPriceVO.of(
                "units",
                new BigDecimal("10.00"), // baseThreshold
                new BigDecimal("100.00"), // basePrice
                new BigDecimal("5.00"),   // incrementStep
                new BigDecimal("20.00"),  // pricePerStep
                USD
        );
        validSchemes.put(USD, standardVo);
    }

    // --- A. Invariants & Constructor Validation ---

    @Test
    @DisplayName("A.1: Existence - Fail if schemes Map is null or empty")
    void shouldFailWhenMapIsInvalid() {
        assertAll("Constructor Validation",
                () -> assertThrows(DomainValidationException.class,
                        () -> createEntity(null), "Should throw DomainValidationException for null map"),

                () -> assertThrows(DomainValidationException.class,
                        () -> createEntity(Map.of()), "Should throw DomainValidationException for empty map")
        );
    }

    @Test
    @DisplayName("A.2: Consistency - Fail if Map key doesn't match VO currency")
    void shouldFailOnCurrencyMismatch() {
        var eur = Currency.getInstance("EUR");
        var mismatchMap = Map.of(USD, ScalingPriceVO.of("units", BigDecimal.TEN, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE, eur));

        // Update this to DomainValidationException as well
        assertThrows(DomainValidationException.class, () -> createEntity(mismatchMap));
    }

    /*

    @Test
    @DisplayName("A.3: Immutability - Internal state must be isolated from external changes")
    void shouldProtectInternalStateByDefensiveCopy() {
        Map<Currency, ScalingPriceVO> sourceMap = new HashMap<>(validSchemes);
        var entity = createEntity(sourceMap);

        // Modify original map
        sourceMap.clear();

        // Entity must still function using its internal copy
        assertDoesNotThrow(() -> {
            BigDecimal price = entity.calculatePrice(USD, BigDecimal.TEN);
            assertEquals(0, new BigDecimal("100.00").compareTo(price));
        });
        assertTrue(entity.supportsCurrency(USD));
    }

     */



    // --- B. Semantic & Boundary Testing ---

    /*
    @ParameterizedTest(name = "Quantity {0} -> Expect Price {1}")
    @CsvSource({
            "10.0,    100.00", // Exact threshold: returns basePrice
            "10.0001, 120.00", // Micro-step over: triggers +1 pricePerStep
            "15.0,    120.00", // Exactly one incrementStep
            "15.0001, 140.00", // Micro-step over first step: triggers +2 pricePerStep
            "0.0,     100.00", // Below threshold: returns basePrice
            "25.0,    160.00"  // Multi-step calculation
    })
    @DisplayName("B.1-3: Calculation Stepped Pivot Points")
    void calculationLogicTests(String qty, String expected) {
        var entity = createEntity(validSchemes);
        var actualPrice = entity.calculatePrice(USD, new BigDecimal(qty));

        // Using compareTo == 0 for numerical equivalence in 2026 standards
        assertEquals(0, new BigDecimal(expected).compareTo(actualPrice),
                "Calculation mismatch for quantity: " + qty);
    }

     */

    @Test
    @DisplayName("B.4: Missing Currency - Throw if currency not in Map")
    void shouldThrowForMissingCurrency() {
        var entity = createEntity(validSchemes);
        var jpy = Currency.getInstance("JPY");

        // Your calculatePrice method throws DomainValidationException for missing keys
        assertThrows(DomainValidationException.class, () -> entity.calculatePrice(jpy, BigDecimal.TEN));
    }

    // --- C. Extreme & Invalid Input Testing (Security) ---

    /*
    @Test
    @DisplayName("C.1: Negative Input - Quantities must be non-negative")
    void shouldFailOnNegativeQuantity() {
        var entity = createEntity(validSchemes);
        assertThrows(IllegalArgumentException.class, () -> entity.calculatePrice(USD, new BigDecimal("-1.0")));
    }

     */

    @Test
    @DisplayName("C.2: Arithmetic DoS - Prevent scales exceeding 100")
    void shouldPreventExtremeScales() {
        // BigDecimal with scale 101 exceeds MAX_ARITHMETIC_SCALE limit in ScalingPriceVO
        var extremeScale = new BigDecimal("1.0").setScale(101);

        assertThrows(IllegalArgumentException.class, () ->
                ScalingPriceVO.of("unit", extremeScale, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, USD));
    }

    @ParameterizedTest
    @ValueSource(strings = {"unit 🚀", "sql'--", "this-unit-name-is-too-long-limit-is-20"})
    @DisplayName("C.3: Lexical Check - Unit must match pattern ^[a-zA-Z0-9\\-/]{1,20}$")
    void shouldRejectInvalidUnitSyntax(String badUnit) {
        assertThrows(IllegalArgumentException.class, () ->
                ScalingPriceVO.of(badUnit, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, USD));
    }

    // Private helper for valid entity creation
    // REVISED: Parameter 'schemes' must be the ONLY source for the factory call
    private FeatureScalingPriceEntity createEntity(Map<Currency, ScalingPriceVO> schemes) {
        return FeatureScalingPriceEntity.create(
                mockId, mockUuid, mockName, mockLabel, mockDesc,
                StatusEnums.ACTIVE, mockVersion, mockModified, true,
                schemes // DO NOT use validSchemes here
        );
    }

}
