package com.smalaca.opentrainings.domain.clock;

import com.smalaca.architecture.portsandadapters.SecondaryPort;

import java.time.LocalDateTime;

@SecondaryPort
public interface Clock {
    LocalDateTime now();
}
