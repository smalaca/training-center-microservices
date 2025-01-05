package com.smalaca.opentrainings.domain.discountservice;

import com.smalaca.architecture.portsandadapters.DrivenPort;

@DrivenPort
public interface DiscountService {
    DiscountResponse calculatePriceFor(DiscountCodeDto dto);
}
