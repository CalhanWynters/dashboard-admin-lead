package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist;

import org.jmolecules.event.annotation.DomainEvent;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.*;

import java.util.Currency;

@DomainEvent(name = "Base Currency Missing", namespace = "pricelist")
public record BaseCurrencyMissingEvent(PriceListUuId id, Currency expectedCurrency) {}