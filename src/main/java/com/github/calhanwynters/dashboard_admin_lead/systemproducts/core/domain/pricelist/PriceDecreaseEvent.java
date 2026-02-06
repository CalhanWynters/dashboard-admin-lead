package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.*;

import java.util.Currency;

@DomainEvent(name = "Price Decrease Detected", namespace = "pricelist")
public record PriceDecreaseEvent(PriceListUuId id, UuId targetId, Currency currency, Actor actor) {}
