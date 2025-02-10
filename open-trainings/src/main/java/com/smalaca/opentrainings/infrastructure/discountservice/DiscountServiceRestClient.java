package com.smalaca.opentrainings.infrastructure.discountservice;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.opentrainings.domain.discountservice.DiscountCodeDto;
import com.smalaca.opentrainings.domain.discountservice.DiscountResponse;
import com.smalaca.opentrainings.domain.discountservice.DiscountService;
import org.springframework.stereotype.Service;

@Service
@DrivenAdapter
public class DiscountServiceRestClient implements DiscountService {
    @Override
    public DiscountResponse calculatePriceFor(DiscountCodeDto dto) {
        return DiscountResponse.failed("DUMMY REASON");
    }
}
