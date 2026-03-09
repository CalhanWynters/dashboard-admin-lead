package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

@Embeddable
public class PriceEntryEmbeddable {

    private UUID itemId;
    private String currencyCode;
    private String pricingType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "strategy_details", columnDefinition = "jsonb")
    private Map<String, Object> strategyDetails;

    // --- Standard Getters and Setters ---

    public UUID getItemId() { return itemId; }
    public void setItemId(UUID itemId) { this.itemId = itemId; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public String getPricingType() { return pricingType; }
    public void setPricingType(String pricingType) { this.pricingType = pricingType; }

    public Map<String, Object> getStrategyDetails() { return strategyDetails; }
    public void setStrategyDetails(Map<String, Object> strategyDetails) { this.strategyDetails = strategyDetails; }
}
