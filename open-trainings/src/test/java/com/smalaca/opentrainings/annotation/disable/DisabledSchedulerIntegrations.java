package com.smalaca.opentrainings.annotation.disable;

import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@TestPropertySource(properties = "spring.task.scheduling.enabled=false")
public @interface DisabledSchedulerIntegrations {
}
