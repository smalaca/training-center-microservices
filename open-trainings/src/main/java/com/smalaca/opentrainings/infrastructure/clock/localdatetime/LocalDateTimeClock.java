package com.smalaca.opentrainings.infrastructure.clock.localdatetime;

import com.smalaca.architecture.portsandadapters.SecondaryAdapter;
import com.smalaca.opentrainings.domain.clock.Clock;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@SecondaryAdapter
public class LocalDateTimeClock implements Clock {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
