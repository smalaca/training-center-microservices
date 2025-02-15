package com.smalaca.opentrainings.infrastructure.repository.jpa.offeracceptancesaga;

import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSaga;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaAssertion;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaRepository;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import com.smalaca.test.type.IntegrationTest;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaAssertion.assertThatOfferAcceptanceSaga;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@IntegrationTest
@SpringBootTest
@Import(JpaOfferAcceptanceSagaRepositoryFactory.class)
class JpaOfferAcceptanceSagaRepositoryIntegrationTest {
    private static final Faker FAKER = new Faker();

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
    void shouldFindNoOfferAcceptanceSagaWhenDoesNotExist() {
        UUID offerId = randomId();

        RuntimeException actual = assertThrows(OfferAcceptanceSagaDoesNotExistException.class, () -> repository.findById(offerId));

        assertThat(actual).hasMessage("OfferAcceptanceSaga with id " + offerId + " does not exist.");
    }

    @Test
    void shouldSaveOfferAcceptanceSaga() {
        UUID offerId = randomId();
        OfferAcceptanceRequestedEvent event = randomOfferAcceptanceRequestedEvent(offerId);
        LocalDateTime consumedAt = LocalDateTime.now();

        givenSavedOfferAcceptanceSaga(offerId, event, consumedAt);

        thenOfferAcceptanceSaga(offerId)
                .isCompleted()
                .hasOfferId(offerId)
                .consumedEvents(1)
                .consumedEventAt(event, consumedAt);
    }

    @Test
    void shouldFindOfferAcceptanceSagaById() {
        UUID offerIdOne = randomId();
        LocalDateTime consumedAtOne = LocalDateTime.now();
        OfferAcceptanceRequestedEvent eventOne = randomOfferAcceptanceRequestedEvent(offerIdOne);
        UUID offerIdTwo = randomId();
        LocalDateTime consumedAtTwo = consumedAtOne.minusSeconds(10);
        OfferAcceptanceRequestedEvent eventTwo = randomOfferAcceptanceRequestedEvent(offerIdTwo);
        UUID offerIdThree = randomId();
        LocalDateTime consumedAtThree = consumedAtOne.minusSeconds(2);
        OfferAcceptanceRequestedEvent eventThree = randomOfferAcceptanceRequestedEvent(offerIdThree);

        givenSavedOfferAcceptanceSaga(offerIdOne, eventOne, consumedAtOne);
        givenSavedOfferAcceptanceSaga(offerIdTwo, eventTwo, consumedAtTwo);
        givenSavedOfferAcceptanceSaga(offerIdThree, eventThree, consumedAtThree);

        thenOfferAcceptanceSaga(offerIdOne)
                .hasOfferId(offerIdOne)
                .consumedEvents(1)
                .consumedEventAt(eventOne, consumedAtOne);
        thenOfferAcceptanceSaga(offerIdTwo)
                .hasOfferId(offerIdTwo)
                .consumedEvents(1)
                .consumedEventAt(eventTwo, consumedAtTwo);
        thenOfferAcceptanceSaga(offerIdThree)
                .hasOfferId(offerIdThree)
                .consumedEvents(1)
                .consumedEventAt(eventThree, consumedAtThree);
    }

    private void givenSavedOfferAcceptanceSaga(UUID offerId, OfferAcceptanceRequestedEvent event, LocalDateTime consumedAt) {
        OfferAcceptanceSaga offerAcceptanceSaga = new OfferAcceptanceSaga(offerId);
        offerAcceptanceSaga.accept(event, () -> consumedAt);
        eventIds.add(event.eventId().eventId());

        transactionTemplate.executeWithoutResult(status -> repository.save(offerAcceptanceSaga));
    }

    private OfferAcceptanceRequestedEvent randomOfferAcceptanceRequestedEvent(UUID offerId) {
        return OfferAcceptanceRequestedEvent.create(offerId, FAKER.name().firstName(), FAKER.name().lastName(), FAKER.internet().emailAddress(), FAKER.code().imei());
    }

    private OfferAcceptanceSagaAssertion thenOfferAcceptanceSaga(UUID offerId) {
        OfferAcceptanceSaga actual = repository.findById(offerId);
        return assertThatOfferAcceptanceSaga(actual);
    }
}