package com.smalaca.opentrainings.infrastructure.api.rest.offer;

import com.smalaca.opentrainings.client.opentrainings.OpenTrainingsTestClient;
import com.smalaca.opentrainings.client.opentrainings.offer.RestAcceptOfferTestCommand;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.offer.OfferTestDto;
import com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.offer.OfferAcceptanceCommandPublisher;
import com.smalaca.opentrainings.infrastructure.repository.jpa.offer.SpringOfferCrudRepository;
import com.smalaca.test.type.SystemTest;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.awaitility.Awaitility.await;

@SystemTest
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "kafka.bootstrap-servers")
@Import({OpenTrainingsTestClient.class, OfferAcceptanceSagaEventTestListener.class})
@TestPropertySource(properties = {
        "kafka.topics.offer-acceptance.events.person-registered=offer-acceptance-person-registered-event-topic",
})
class OfferRestControllerOfferAcceptanceSystemTest {
    private static final Faker FAKER = new Faker();

    private static final String FIRST_NAME = FAKER.name().firstName();
    private static final String LAST_NAME = FAKER.name().lastName();
    private static final String EMAIL = FAKER.internet().emailAddress();
    private static final String DISCOUNT_CODE = UUID.randomUUID().toString();
    private static final String NO_DISCOUNT_CODE = null;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private SpringOfferCrudRepository springOfferCrudRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private OpenTrainingsTestClient client;

    @Autowired
    private OfferAcceptanceSagaEventTestListener testListener;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @MockBean
    private OfferAcceptanceCommandPublisher offerAcceptanceCommandPublisher;

    private GivenOfferAcceptance given;
    private OfferTestDto dto;
    private ThenOfferAcceptance then;

    @BeforeEach
    void givenOfferFactory() {
        given = GivenOfferAcceptance.create(offerRepository, testListener);
        then = new ThenOfferAcceptance(client);
        kafkaListenerEndpointRegistry.getAllListenerContainers().forEach(
                listenerContainer -> ContainerTestUtils.waitForAssignment(listenerContainer, 1));
    }

    @AfterEach
    void deleteOffers() {
        transactionTemplate.executeWithoutResult(transactionStatus -> springOfferCrudRepository.deleteById(dto.getOfferId()));
    }

    @Test
    void shouldRejectWhenOfferNotAvailableAnymore() {
        given
                .declinedOffer()
                .personRegistered();
        dto = given.getOffer();

        client.offers().accept(command(dto));

        await().untilAsserted(() -> then
                .offerAcceptanceRejected(dto.getOfferId(), "Offer already DECLINED")
                .offerDeclined(dto)
                .orderNotInitiated(dto));
    }

    @Test
    void shouldRejectWhenOfferExpiredAndTrainingPriceChanged() {
        given
                .expiredOffer()
                .personRegistered()
                .trainingPriceChanged(BigDecimal.valueOf(439.21), "EUR");
        dto = given.getOffer();

        client.offers().accept(command(dto));

        await().untilAsserted(() -> then
                .offerAcceptanceRejected(dto.getOfferId(), "Training price changed to: 439.21 EUR")
                .offerRejected(dto)
                .orderNotInitiated(dto));
    }

    @Test
    void shouldRejectWhenTrainingNoLongerAvailable() {
        given
                .initiatedOffer()
                .personRegistered()
                .trainingPriceNotChanged()
                .discountUsed(DISCOUNT_CODE)
                .nonBookableTraining()
                .discountCodeReturned(DISCOUNT_CODE);
        dto = given.getOffer();

        client.offers().accept(command(dto));

        await().untilAsserted(() -> then
                .offerAcceptanceRejected(dto.getOfferId(), "No available training places left")
                .offerRejected(dto)
                .orderNotInitiated(dto));
    }

    @Test
    void shouldAcceptWhenDiscountNotGiven() {
        given
                .initiatedOffer()
                .personRegistered()
                .trainingPriceNotChanged()
                .bookableTraining();
        dto = given.getOffer();

        client.offers().accept(commandWithoutDiscount(dto));

        await().untilAsserted(() -> then
                .offerAcceptanceAccepted(dto.getOfferId())
                .offerAccepted(dto)
                .orderInitiated(dto));
    }

    @Test
    void shouldAcceptWhenDiscountCodeAlreadyUsed() {
        given
                .initiatedOffer()
                .personRegistered()
                .trainingPriceNotChanged()
                .discountAlreadyUsed(DISCOUNT_CODE)
                .bookableTraining();
        dto = given.getOffer();

        client.offers().accept(command(dto));

        await().untilAsserted(() -> then
                .offerAcceptanceAccepted(dto.getOfferId())
                .offerAccepted(dto)
                .orderInitiated(dto));
    }

    @Test
    void shouldAcceptWhenPersonRegistered() {
        given
                .initiatedOffer()
                .personRegistered()
                .trainingPriceNotChanged()
                .bookableTraining()
                .discountUsed(DISCOUNT_CODE);
        dto = given.getOffer();

        client.offers().accept(command(dto));

        await().untilAsserted(() -> then
                .offerAcceptanceAccepted(dto.getOfferId())
                .offerAccepted(dto)
                .orderInitiated(dto));
    }

    @Test
    void shouldAcceptWhenRegisteredPersonAlreadyFound() {
        given
                .initiatedOffer()
                .alreadyRegisteredPersonFound()
                .trainingPriceNotChanged()
                .bookableTraining()
                .discountUsed(DISCOUNT_CODE);
        dto = given.getOffer();

        client.offers().accept(command(dto));

        await().untilAsserted(() -> then
                .offerAcceptanceAccepted(dto.getOfferId())
                .offerAccepted(dto)
                .orderInitiated(dto));
    }

    @Test
    void shouldAcceptWhenOfferExpiredAndTrainingPriceNotChanged() {
        given
                .expiredOffer()
                .personRegistered()
                .trainingPriceNotChanged()
                .bookableTraining()
                .discountUsed(DISCOUNT_CODE);
        dto = given.getOffer();

        client.offers().accept(command(dto));

        await().untilAsserted(() -> then
                .offerAcceptanceAccepted(dto.getOfferId())
                .offerAccepted(dto)
                .orderInitiated(dto));
    }

    private RestAcceptOfferTestCommand command(OfferTestDto dto) {
        return new RestAcceptOfferTestCommand(dto.getOfferId(), FIRST_NAME, LAST_NAME, EMAIL, DISCOUNT_CODE);
    }

    private RestAcceptOfferTestCommand commandWithoutDiscount(OfferTestDto dto) {
        return new RestAcceptOfferTestCommand(dto.getOfferId(), FIRST_NAME, LAST_NAME, EMAIL, NO_DISCOUNT_CODE);
    }
}