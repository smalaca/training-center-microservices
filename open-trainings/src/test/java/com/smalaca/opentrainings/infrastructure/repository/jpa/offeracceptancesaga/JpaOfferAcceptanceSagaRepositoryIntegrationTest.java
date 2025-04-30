package com.smalaca.opentrainings.infrastructure.repository.jpa.offeracceptancesaga;

import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSaga;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaAssertion;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaRepository;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RejectOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceChangedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceNotChangedEvent;
import com.smalaca.test.type.SpringBootIntegrationTest;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.collect.ImmutableMap.of;
import static com.smalaca.opentrainings.data.Random.randomAmount;
import static com.smalaca.opentrainings.data.Random.randomCurrency;
import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.data.Random.randomPrice;
import static com.smalaca.opentrainings.domain.eventid.EventId.newEventId;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaAssertion.assertThatOfferAcceptanceSaga;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand.acceptOfferCommandBuilder;

@SpringBootIntegrationTest
@Import(JpaOfferAcceptanceSagaRepositoryFactory.class)
class JpaOfferAcceptanceSagaRepositoryIntegrationTest {
    private static final Faker FAKER = new Faker();
    private static final LocalDateTime NOW = LocalDateTime.now();

    @Autowired
    private OfferAcceptanceSagaRepository repository;

    @Autowired
    private SpringOfferAcceptanceSagaEventCrudRepository springRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private final List<UUID> eventIds = new ArrayList<>();

    @AfterEach
    void deleteAllEvents() {
        if (!eventIds.isEmpty()) {
            springRepository.deleteAllById(eventIds);
        }
    }

    @Test
    void shouldFindEmptyOfferAcceptanceSagaWhenNoEventsStoredYet() {
        UUID offerId = randomId();

        OfferAcceptanceSaga actual = repository.findById(offerId);

        assertThatOfferAcceptanceSaga(actual)
                .isInProgress()
                .hasOfferId(offerId)
                .consumedNoEvents();
    }

    @Test
    void shouldCreateOfferAcceptanceSaga() {
        UUID offerId = randomId();
        OfferAcceptanceRequestedEvent event = randomOfferAcceptanceRequestedEvent(offerId);

        givenSavedOfferAcceptanceSaga(offerId, of(event, NOW));

        thenOfferAcceptanceSaga(offerId)
                .isInProgress()
                .hasOfferId(offerId)
                .consumedEvents(1)
                .consumedEventAt(event, NOW);
    }

    @Test
    void shouldAcceptOfferAcceptanceSaga() {
        UUID offerId = randomId();
        OfferAcceptanceRequestedEvent eventOne = randomOfferAcceptanceRequestedEvent(offerId);
        OfferAcceptedEvent eventTwo = randomOfferAcceptedEvent(offerId);
        Map<OfferAcceptanceSagaEvent, LocalDateTime> events = of(eventOne, NOW.minusSeconds(10), eventTwo, NOW.minusSeconds(5));

        givenSavedOfferAcceptanceSaga(offerId, events);

        thenOfferAcceptanceSaga(offerId)
                .isAccepted()
                .hasOfferId(offerId)
                .consumedEvents(2)
                .consumedEventAt(eventOne, NOW.minusSeconds(10))
                .consumedEventAt(eventTwo, NOW.minusSeconds(5));
    }

    @Test
    void shouldRejectOfferAcceptanceSaga() {
        UUID offerId = randomId();
        OfferAcceptanceRequestedEvent eventOne = randomOfferAcceptanceRequestedEvent(offerId);
        OfferRejectedEvent eventTwo = randomOfferRejectedEvent(offerId);
        Map<OfferAcceptanceSagaEvent, LocalDateTime> events = of(eventOne, NOW.minusSeconds(13), eventTwo, NOW.minusSeconds(3));

        givenSavedOfferAcceptanceSaga(offerId, events);

        thenOfferAcceptanceSaga(offerId)
                .isRejected()
                .hasOfferId(offerId)
                .consumedEvents(2)
                .consumedEventAt(eventOne, NOW.minusSeconds(13))
                .consumedEventAt(eventTwo, NOW.minusSeconds(3));
    }

    @Test
    void shouldFindOfferAcceptanceSagaById() {
        UUID offerIdOne = randomInProgressOfferAcceptanceSaga();
        UUID offerIdTwo = randomAcceptedOfferAcceptanceSaga();
        UUID offerIdThree = randomRejectedOfferAcceptanceSaga();

        thenOfferAcceptanceSaga(offerIdOne)
                .isInProgress()
                .hasOfferId(offerIdOne)
                .consumedEvents(1);
        thenOfferAcceptanceSaga(offerIdTwo)
                .isAccepted()
                .hasOfferId(offerIdTwo)
                .consumedEvents(2);
        thenOfferAcceptanceSaga(offerIdThree)
                .isRejected()
                .hasOfferId(offerIdThree)
                .consumedEvents(2);
    }

    private UUID randomInProgressOfferAcceptanceSaga() {
        UUID offerId = randomId();
        givenSavedOfferAcceptanceSaga(offerId, of(randomOfferAcceptanceRequestedEvent(offerId), NOW));

        return offerId;
    }

    private UUID randomAcceptedOfferAcceptanceSaga() {
        UUID offerId = randomId();
        givenSavedOfferAcceptanceSaga(offerId, of(
                randomOfferAcceptanceRequestedEvent(offerId), NOW.minusSeconds(5),
                randomOfferAcceptedEvent(offerId), NOW.minusSeconds(4)));
        return offerId;
    }

    private UUID randomRejectedOfferAcceptanceSaga() {
        UUID offerId = randomId();
        givenSavedOfferAcceptanceSaga(offerId, of(
                randomOfferAcceptanceRequestedEvent(offerId), NOW.minusSeconds(7),
                randomOfferRejectedEvent(offerId), NOW.minusSeconds(3)));
        return offerId;
    }

    private void givenSavedOfferAcceptanceSaga(UUID offerId, Map<OfferAcceptanceSagaEvent, LocalDateTime> events) {
        OfferAcceptanceSaga offerAcceptanceSaga = new OfferAcceptanceSaga(offerId);

        events.forEach((event, consumedAt) -> {
            offerAcceptanceSaga.load(event, consumedAt);
            eventIds.add(event.eventId().eventId());
            transactionTemplate.executeWithoutResult(status -> repository.save(offerAcceptanceSaga));
        });
    }

    private OfferAcceptedEvent randomOfferAcceptedEvent(UUID offerId) {
        OfferAcceptanceSagaEvent event = new TrainingPriceNotChangedEvent(newEventId(), offerId, randomId());
        AcceptOfferCommand command = acceptOfferCommandBuilder(event, randomId())
                .withDiscountCodeUsed(randomDiscountCode(), randomPrice())
                .build();

        return OfferAcceptedEvent.nextAfter(command, randomId(), randomPrice());
    }

    private String randomDiscountCode() {
        return FAKER.code().imei();
    }

    private OfferAcceptanceRequestedEvent randomOfferAcceptanceRequestedEvent(UUID offerId) {
        return OfferAcceptanceRequestedEvent.create(offerId, FAKER.name().firstName(), FAKER.name().lastName(), FAKER.internet().emailAddress(), FAKER.code().imei());
    }

    private OfferRejectedEvent randomOfferRejectedEvent(UUID offerId) {
        RejectOfferCommand command = RejectOfferCommand.nextAfter(trainingPriceChangedEvent(offerId));
        return OfferRejectedEvent.nextAfter(command);
    }

    private TrainingPriceChangedEvent trainingPriceChangedEvent(UUID offerId) {
        return new TrainingPriceChangedEvent(newEventId(), offerId, randomId(), randomAmount(), randomCurrency());
    }

    private OfferAcceptanceSagaAssertion thenOfferAcceptanceSaga(UUID offerId) {
        OfferAcceptanceSaga actual = repository.findById(offerId);
        return assertThatOfferAcceptanceSaga(actual);
    }
}