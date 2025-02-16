package com.smalaca.opentrainings.domain.offer;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataManagement;
import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataRequest;
import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataResponse;
import com.smalaca.opentrainings.domain.price.Price;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingBookingDto;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingBookingResponse;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingDto;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;
import net.datafaker.Faker;
import org.apache.commons.lang3.RandomUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.data.Random.randomPrice;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class GivenOffer {
    private static final Faker FAKER = new Faker();

    private final OfferFactory offerFactory;
    private final Clock clock;
    private final TrainingOfferCatalogue trainingOfferCatalogue;

    private UUID trainingId = randomId();
    private LocalDateTime creationDateTime = LocalDateTime.now();
    private Price trainingPrice = randomPrice();
    private Offer offer;

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
        this.creationDateTime = LocalDateTime.now().minusMinutes(minutes);
        return this;
    }

    public GivenOffer initiated() {
        given(clock.now()).willReturn(creationDateTime);
        given(trainingOfferCatalogue.detailsOf(trainingId)).willReturn(new TrainingDto(randomAvailability(), trainingPrice));
        offer = offerFactory.create(trainingId);

        return this;
    }

    private int randomAvailability() {
        return RandomUtils.secure().randomInt(1, 42);
    }

    public GivenOffer rejected() {
        initiated();
        given(clock.now()).willReturn(creationDateTime.plusMinutes(20));
        given(trainingOfferCatalogue.detailsOf(trainingId)).willReturn(new TrainingDto(randomAvailability(), randomPrice()));
        offer.accept(null, null, trainingOfferCatalogue, null, clock);
        return this;
    }

    public GivenOffer accepted() {
        initiated();
        String firstName = FAKER.name().firstName();
        String lastName = FAKER.name().lastName();
        String email = FAKER.address().mailBox();
        UUID participantId = UUID.randomUUID();
        AcceptOfferCommand command = new AcceptOfferCommand(CommandId.nextAfter(EventId.newEventId()), getOfferId(), firstName, lastName, email, null);
        PersonalDataManagement projectDataManagement = mock(PersonalDataManagement.class);
        PersonalDataRequest personalDataRequest = PersonalDataRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .build();
        given(projectDataManagement.save(personalDataRequest)).willReturn(PersonalDataResponse.successful(participantId));
        given(clock.now()).willReturn(creationDateTime.plusMinutes(1));
        given(trainingOfferCatalogue.book(new TrainingBookingDto(trainingId, participantId))).willReturn(TrainingBookingResponse.successful(trainingId, participantId));

        offer.accept(command, projectDataManagement, trainingOfferCatalogue, null, clock);
        return this;
    }

    public GivenOffer terminated() {
        initiated();
        given(clock.now()).willReturn(LocalDateTime.now());
        offer.terminate(clock);

        return this;
    }

    public GivenOffer declined() {
        initiated();
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
                .build();
    }

    protected UUID getOfferId() {
        return null;
    }
}
