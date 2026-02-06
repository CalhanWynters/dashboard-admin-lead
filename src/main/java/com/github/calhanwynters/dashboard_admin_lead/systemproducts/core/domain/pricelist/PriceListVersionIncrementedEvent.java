package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist;

import org.jmolecules.event.annotation.DomainEvent;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.*;

import java.util.Currency;

@DomainEvent(name = "Price List Version Incremented", namespace = "pricelist")
public record PriceListVersionIncrementedEvent(PriceListUuId id, PriceListVersion newVersion) {}
