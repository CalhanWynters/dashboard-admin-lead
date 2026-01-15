package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.legacyfeatures;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.exceptions.DomainRuleViolationException;
import java.util.*;
import java.math.BigDecimal;

/**
 * Tiered Price Entity - Java 25 / 2026 Production Ready.
 * Enforces intentional pricing by rejecting defaults and ensuring cross-field consistency.
 */
/*
public class FeatureTieredPriceEntity extends FeatureAbstractClass {

    // --- 1. PUBLIC BUILDER API ---


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends FeatureAbstractClass.Builder<Builder> {

        private final Map<Currency, TieredPriceVO> tieredPriceSchemes = new HashMap<>();

        @Override
        protected Builder self() {
            return this;
        }

        public Builder addTieredScheme(Currency currency, TieredPriceVO scheme) {
            this.tieredPriceSchemes.put(
                    Objects.requireNonNull(currency, "Currency must be provided"),
                    Objects.requireNonNull(scheme, "TieredPriceVO scheme must be provided")
            );
            return this;
        }

        public Builder tieredPriceSchemes(Map<Currency, TieredPriceVO> schemes) {
            this.tieredPriceSchemes.clear();
            if (schemes != null) {
                this.tieredPriceSchemes.putAll(schemes);
            }
            return this;
        }

        @Override
        public FeatureTieredPriceEntity build() {
            return new FeatureTieredPriceEntity(this);
        }
    }

    // --- 2. STATE & CONSTRUCTOR ---

    private final Map<Currency, TieredPriceVO> tieredPriceSchemes;

    private FeatureTieredPriceEntity(Builder builder) {
        // PROLOGUE: Java 25 validation before parent init
        var validatedMap = processAndValidateSchemes(builder.tieredPriceSchemes);

        super(builder);

        // EPILOGUE: Final assignment
        this.tieredPriceSchemes = validatedMap;
    }

    // --- 3. INTERNAL VALIDATION LOGIC ---

    private static Map<Currency, TieredPriceVO> processAndValidateSchemes(Map<Currency, TieredPriceVO> inputSchemes) {
        // 1. REJECT empty or null schemes to prevent "Automatic Defaults"
        if (inputSchemes == null || inputSchemes.isEmpty()) {
            throw new DomainRuleViolationException(
                    "Pricing schemes are missing. You must intentionally define at least one currency and price."
            );
        }

        // 2. Cross-Field Consistency Check
        inputSchemes.forEach((currency, vo) -> {
            if (!currency.equals(vo.currency())) {
                throw new DomainRuleViolationException("Mismatch: Map key [%s] does not match TieredPriceVO internal currency [%s]"
                        .formatted(currency.getCurrencyCode(), vo.currency().getCurrencyCode()));
            }
        });

        // 3. Immutability: Map.copyOf is the preferred 2026 way to freeze a map
        return Map.copyOf(inputSchemes);
    }

    // --- 4. PUBLIC ACCESSORS & DOMAIN LOGIC ---


    public Map<Currency, TieredPriceVO> getTieredPriceSchemes() {
        return tieredPriceSchemes;
    }

    public Optional<TieredPriceVO> getTieredPriceForCurrency(Currency currency) {
        return Optional.ofNullable(tieredPriceSchemes.get(currency));
    }

    public Optional<BigDecimal> calculatePrice(Currency currency, BigDecimal quantity) {
        return getTieredPriceForCurrency(currency)
                .map(scheme -> scheme.calculate(quantity));
    }
}

 */
