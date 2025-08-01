package com.smalaca.opentrainings.domain.offer;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.BeginOfferAcceptanceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RejectOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceChangedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceNotChangedEvent;
import com.smalaca.opentrainings.domain.price.Price;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingDto;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;
import net.datafaker.Faker;
import org.apache.commons.lang3.RandomUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomAmount;
import static com.smalaca.opentrainings.data.Random.randomCurrency;
import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.data.Random.randomPrice;
import static com.smalaca.opentrainings.domain.eventid.EventId.newEventId;
import static com.smalaca.opentrainings.domain.offer.OfferStatus.ACCEPTANCE_IN_PROGRESS;
import static com.smalaca.opentrainings.domain.offer.OfferStatus.ACCEPTED;
import static com.smalaca.opentrainings.domain.offer.OfferStatus.DECLINED;
import static com.smalaca.opentrainings.domain.offer.OfferStatus.INITIATED;
import static com.smalaca.opentrainings.domain.offer.OfferStatus.REJECTED;
import static com.smalaca.opentrainings.domain.offer.OfferStatus.TERMINATED;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand.acceptOfferCommandBuilder;
import static org.mockito.BDDMockito.given;

public class GivenOffer {
    private static final Faker FAKER = new Faker();

    private final OfferFactory offerFactory;
    private final Clock clock;
    private final TrainingOfferCatalogue trainingOfferCatalogue;

    private UUID trainingId = randomId();
    private LocalDateTime creationDateTime = LocalDateTime.now();
    private Price trainingPrice = randomPrice();
    private Offer offer;
    private OfferStatus status;

    GivenOffer(OfferFactory offerFactory, Clock clock, TrainingOfferCatalogue trainingOfferCatalogue) {
        this.offerFactory = offerFactory;
        this.clock = clock;
        this.trainingOfferCatalogue = trainingOfferCatalogue;
    }

    public GivenOffer trainingId(UUID trainingId) {
        this.trainingId = trainingId;
        return this;
    }

    public GivenOffer trainingPrice(Price trainingPrice) {
        this.trainingPrice = trainingPrice;
        return this;
    }

    public GivenOffer createdMinutesAgo(int minutes) {
        return createdAt(LocalDateTime.now().minusMinutes(minutes));
    }

    public GivenOffer createdAt(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
        return this;
    }

    public GivenOffer initiated() {
        status = INITIATED;
        given(clock.now()).willReturn(creationDateTime);
        given(trainingOfferCatalogue.detailsOf(trainingId)).willReturn(new TrainingDto(trainingId, randomAvailability(), trainingPrice));
        offer = offerFactory.create(trainingId);

        return this;
    }

    public GivenOffer acceptanceInProgress() {
        initiated();
        status = ACCEPTANCE_IN_PROGRESS;
        given(clock.now()).willReturn(creationDateTime.plusMinutes(1));
        offer.beginAcceptance(beginOfferAcceptanceCommand(), clock);

        return this;
    }

    private int randomAvailability() {
        return RandomUtils.secure().randomInt(1, 42);
    }

    public GivenOffer rejected() {
        acceptanceInProgress();
        status = REJECTED;
        offer.reject(rejectOfferCommand());

        return this;
    }

    public GivenOffer accepted() {
        acceptanceInProgress();
        status = ACCEPTED;
        UUID participantId = UUID.randomUUID();
        given(clock.now()).willReturn(creationDateTime.plusMinutes(1));
        offer.accept(acceptOfferCommand(participantId));

        return this;
    }

    public GivenOffer terminated() {
        initiated();
        status = TERMINATED;
        given(clock.now()).willReturn(LocalDateTime.now());
        offer.terminate(clock);

        return this;
    }

    public GivenOffer declined() {
        initiated();
        status = DECLINED;
        offer.decline();

        return this;
    }

    public Offer getOffer() {
        return offer;
    }

    public OfferTestDto getDto() {
        return OfferTestDto.builder()
                .offerId(getOfferId())
                .trainingId(trainingId)
                .trainingPrice(trainingPrice)
                .creationDateTime(creationDateTime)
                .status(status)
                .build();
    }

    protected UUID getOfferId() {
        return null;
    }

    private AcceptOfferCommand acceptOfferCommand(UUID participantId) {
        OfferAcceptanceSagaEvent event = trainingPriceNotChangedEvent();
        return acceptOfferCommandBuilder(event, participantId)
                .withDiscountCodeUsed(randomDiscountCode(), randomPrice())
                .build();
    }

    private TrainingPriceNotChangedEvent trainingPriceNotChangedEvent() {
        return new TrainingPriceNotChangedEvent(newEventId(), getOfferId(), trainingId);
    }

    private RejectOfferCommand rejectOfferCommand() {
        return RejectOfferCommand.nextAfter(trainingPriceChangedEvent());
    }

    private TrainingPriceChangedEvent trainingPriceChangedEvent() {
        return new TrainingPriceChangedEvent(newEventId(), getOfferId(), trainingId, randomAmount(), randomCurrency());
    }

    private BeginOfferAcceptanceCommand beginOfferAcceptanceCommand() {
        return BeginOfferAcceptanceCommand.nextAfter(offerAcceptanceRequestedEvent());
    }

    private OfferAcceptanceRequestedEvent offerAcceptanceRequestedEvent() {
        return OfferAcceptanceRequestedEvent.create(getOfferId(), FAKER.name().firstName(), FAKER.name().lastName(), FAKER.internet().emailAddress(), randomDiscountCode());
    }

    private String randomDiscountCode() {
        return FAKER.lorem().word();
    }
}
