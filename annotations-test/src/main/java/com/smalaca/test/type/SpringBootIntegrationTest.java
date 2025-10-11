package com.smalaca.test.type;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@IntegrationTest
@DirtiesContext(classMode = AFTER_CLASS)
@SpringBootTest
public @interface SpringBootIntegrationTest {
}