package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.*;

import java.util.Currency;
/*
 * If you implement a "10% increase across all tiers" feature, you shouldn't just fire 100
 * PriceUpdatedEvents. A single bulk event allows the Audit Log to show the intent
 * (inflation adjustment) rather than just the result (new numbers).
 */

@DomainEvent(name = "Bulk Price Adjustment Applied", namespace = "pricelist")
public record BulkPriceAdjustmentEvent(PriceListUuId id, String adjustmentType, double percentage, Actor actor) {}