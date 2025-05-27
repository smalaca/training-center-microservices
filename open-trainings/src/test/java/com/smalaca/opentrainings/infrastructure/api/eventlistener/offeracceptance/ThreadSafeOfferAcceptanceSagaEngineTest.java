package com.smalaca.opentrainings.infrastructure.api.eventlistener.offeracceptance;

import com.smalaca.opentrainings.application.offeracceptancesaga.OfferAcceptanceSagaEngine;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ThreadSafeOfferAcceptanceSagaEngineTest {
    private static final Faker FAKER = new Faker();
    private final OfferAcceptanceSagaEngine engine = mock(OfferAcceptanceSagaEngine.class);
    private final ThreadSafeOfferAcceptanceSagaEngine threadSafeEngine = new ThreadSafeOfferAcceptanceSagaEngine(engine);

    @Test
    void shouldProcessEventsSequentiallyForSameOfferId() throws InterruptedException {
        // given
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(numberOfThreads);

        UUID offerId = UUID.randomUUID();
        List<OfferAcceptanceRequestedEvent> events = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            events.add(randomOfferAcceptanceRequestedEvent(offerId));
        }

        // Track the order of execution
        List<Long> executionTimes = Collections.synchronizedList(new ArrayList<>());

        // when
        for (OfferAcceptanceRequestedEvent event : events) {
            executorService.submit(() -> {
                try {
                    startLatch.await(); // Wait for all threads to be ready

                    // Record the time this execution started
                    long startTime = System.currentTimeMillis();
                    executionTimes.add(startTime);

                    threadSafeEngine.accept(event);

                    // Simulate some processing time
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // Start all threads
        finishLatch.await(10, TimeUnit.SECONDS); // Wait for all threads to finish
        executorService.shutdown();

        // then
        // Verify that engine.accept was called exactly numberOfThreads times
        verify(engine, times(numberOfThreads)).accept(any(OfferAcceptanceRequestedEvent.class));

        // Check that we have the expected number of execution times
        assertThat(executionTimes).hasSize(numberOfThreads);

        // Sort execution times to check sequential processing
        List<Long> sortedExecutionTimes = new ArrayList<>(executionTimes);
        Collections.sort(sortedExecutionTimes);

        // Check that the actual execution order matches the sorted order
        // This verifies that executions were sequential (one after another)
        assertThat(executionTimes).isEqualTo(sortedExecutionTimes);
    }

    @Test
    void shouldProcessEventsConcurrentlyForDifferentOfferIds() throws InterruptedException {
        // given
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(numberOfThreads);

        List<UUID> offerIds = new ArrayList<>();
        List<OfferAcceptanceRequestedEvent> events = new ArrayList<>();

        // Create events with different offerIds
        for (int i = 0; i < numberOfThreads; i++) {
            UUID offerId = UUID.randomUUID();
            offerIds.add(offerId);
            events.add(randomOfferAcceptanceRequestedEvent(offerId));
        }

        // Track when each event starts and finishes processing
        List<Long> startTimes = Collections.synchronizedList(new ArrayList<>());
        List<Long> endTimes = Collections.synchronizedList(new ArrayList<>());

        // when
        for (OfferAcceptanceRequestedEvent event : events) {
            executorService.submit(() -> {
                try {
                    startLatch.await(); // Wait for all threads to be ready

                    long startTime = System.currentTimeMillis();
                    startTimes.add(startTime);

                    threadSafeEngine.accept(event);

                    // Simulate some processing time
                    Thread.sleep(50);

                    long endTime = System.currentTimeMillis();
                    endTimes.add(endTime);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // Start all threads
        finishLatch.await(10, TimeUnit.SECONDS); // Wait for all threads to finish
        executorService.shutdown();

        // then
        // Verify that engine.accept was called exactly numberOfThreads times
        verify(engine, times(numberOfThreads)).accept(any(OfferAcceptanceRequestedEvent.class));

        // Calculate the total execution time (from first start to last end)
        long firstStart = Collections.min(startTimes);
        long lastEnd = Collections.max(endTimes);
        long totalExecutionTime = lastEnd - firstStart;

        // Calculate the sum of individual execution times
        long sumOfIndividualTimes = 0;
        for (int i = 0; i < numberOfThreads; i++) {
            sumOfIndividualTimes += 50; // Each thread sleeps for 50ms
        }

        // If events are processed concurrently, the total execution time should be
        // significantly less than the sum of individual execution times
        assertThat(totalExecutionTime).isLessThan((long)(sumOfIndividualTimes * 0.8));
    }

    @Test
    void shouldWaitForLockToBeReleased() throws InterruptedException {
        UUID offerId = UUID.randomUUID();
        CountDownLatch firstThreadStarted = new CountDownLatch(1);
        CountDownLatch firstThreadProcessing = new CountDownLatch(1);
        CountDownLatch secondThreadFinished = new CountDownLatch(1);

        OfferAcceptanceRequestedEvent firstEvent = randomOfferAcceptanceRequestedEvent(offerId);
        OfferAcceptanceRequestedEvent secondEvent = randomOfferAcceptanceRequestedEvent(offerId);

        // Configure the first call to engine.accept to signal when it's processing
        doAnswer(invocation -> {
            firstThreadStarted.countDown();
            firstThreadProcessing.await(1, TimeUnit.SECONDS); // Hold the lock
            return null;
        }).when(engine).accept(firstEvent);

        // Configure the second call to engine.accept to signal when it's done
        doAnswer(invocation -> {
            secondThreadFinished.countDown();
            return null;
        }).when(engine).accept(secondEvent);

        // when
        // First thread takes the lock
        Thread firstThread = new Thread(() -> {
            threadSafeEngine.accept(firstEvent);
        });
        firstThread.start();

        // Wait for the first thread to start
        firstThreadStarted.await(1, TimeUnit.SECONDS);

        // Second thread tries to take the lock while it's held by the first thread
        Thread secondThread = new Thread(() -> {
            threadSafeEngine.accept(secondEvent);
        });
        secondThread.start();

        // Let the first thread finish after a short delay
        Thread.sleep(100);
        firstThreadProcessing.countDown();

        // then
        // Second thread should finish after the first thread releases the lock
        assertThat(secondThreadFinished.await(1, TimeUnit.SECONDS)).isTrue();

        // Verify that engine.accept was called exactly twice
        verify(engine, times(1)).accept(firstEvent);
        verify(engine, times(1)).accept(secondEvent);
    }

    private OfferAcceptanceRequestedEvent randomOfferAcceptanceRequestedEvent(UUID offerId) {
        return OfferAcceptanceRequestedEvent.create(
                offerId, FAKER.name().firstName(), FAKER.name().lastName(), FAKER.internet().emailAddress(), FAKER.commerce().promotionCode());    }
}
