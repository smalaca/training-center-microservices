package com.smalaca.opentrainings.domain.clock;

import com.smalaca.architecture.portsandadapters.DrivenPort;

import java.time.LocalDateTime;

@DrivenPort
public interface Clock {
    LocalDateTime now();
}
