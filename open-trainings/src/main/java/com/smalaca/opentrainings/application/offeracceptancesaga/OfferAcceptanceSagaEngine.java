package com.smalaca.opentrainings.application.offeracceptancesaga;

import com.smalaca.architecture.cqrs.CommandOperation;
import com.smalaca.architecture.cqrs.QueryOperation;
import com.smalaca.architecture.portsandadapters.DrivenPort;
import com.smalaca.domaindrivendesign.ApplicationLayer;
import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.commandregistry.CommandRegistry;
import com.smalaca.opentrainings.domain.offer.events.ExpiredOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offer.events.NotAvailableOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent;
import com.smalaca.opentrainings.domain.offer.events.UnexpiredOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSaga;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaDto;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaRepository;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.ConfirmTrainingPriceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.OfferAcceptanceSagaCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RejectOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeAlreadyUsedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeUsedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.NoAvailableTrainingPlacesLeftEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPlaceBookedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceChangedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceNotChangedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@ApplicationLayer
public class OfferAcceptanceSagaEngine {
    private final Clock clock;
    private final OfferAcceptanceSagaRepository repository;
    private final CommandRegistry commandRegistry;

    OfferAcceptanceSagaEngine(Clock clock, OfferAcceptanceSagaRepository repository, CommandRegistry commandRegistry) {
        this.clock = clock;
        this.repository = repository;
        this.commandRegistry = commandRegistry;
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(OfferAcceptanceRequestedEvent event) {
        OfferAcceptanceSaga offerAcceptanceSaga = new OfferAcceptanceSaga(event.offerId());

        List<OfferAcceptanceSagaCommand> command = offerAcceptanceSaga.accept(event, clock);

        command.forEach(commandRegistry::publish);
        repository.save(offerAcceptanceSaga);
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(PersonRegisteredEvent event) {
        OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

        Optional<AcceptOfferCommand> command = offerAcceptanceSaga.accept(event, clock);

        command.ifPresent(commandRegistry::publish);
        repository.save(offerAcceptanceSaga);
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(AlreadyRegisteredPersonFoundEvent event) {
        OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

        Optional<AcceptOfferCommand> command = offerAcceptanceSaga.accept(event, clock);

        command.ifPresent(commandRegistry::publish);
        repository.save(offerAcceptanceSaga);
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(UnexpiredOfferAcceptanceRequestedEvent event) {
        OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

        Optional<AcceptOfferCommand> command = offerAcceptanceSaga.accept(event, clock);

        command.ifPresent(commandRegistry::publish);
        repository.save(offerAcceptanceSaga);
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(ExpiredOfferAcceptanceRequestedEvent event) {
        OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

        ConfirmTrainingPriceCommand command = offerAcceptanceSaga.accept(event, clock);

        commandRegistry.publish(command);
        repository.save(offerAcceptanceSaga);
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(TrainingPriceNotChangedEvent event) {
        OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

        Optional<AcceptOfferCommand> command = offerAcceptanceSaga.accept(event, clock);

        command.ifPresent(commandRegistry::publish);
        repository.save(offerAcceptanceSaga);
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(TrainingPriceChangedEvent event) {
        OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

        RejectOfferCommand command = offerAcceptanceSaga.accept(event, clock);

        commandRegistry.publish(command);
        repository.save(offerAcceptanceSaga);
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(DiscountCodeUsedEvent event) {
        OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

        offerAcceptanceSaga.accept(event, clock);

        repository.save(offerAcceptanceSaga);
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(DiscountCodeAlreadyUsedEvent event) {
        OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

        offerAcceptanceSaga.accept(event, clock);

        repository.save(offerAcceptanceSaga);
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(TrainingPlaceBookedEvent event) {
        OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

        offerAcceptanceSaga.accept(event, clock);

        repository.save(offerAcceptanceSaga);
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(NoAvailableTrainingPlacesLeftEvent event) {
        OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

        offerAcceptanceSaga.accept(event, clock);

        repository.save(offerAcceptanceSaga);
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(NotAvailableOfferAcceptanceRequestedEvent event) {
        OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

        offerAcceptanceSaga.accept(event, clock);

        repository.save(offerAcceptanceSaga);
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(OfferAcceptedEvent event) {
        OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

        offerAcceptanceSaga.accept(event, clock);

        repository.save(offerAcceptanceSaga);
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(OfferRejectedEvent event) {
        OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

        offerAcceptanceSaga.accept(event, clock);

        repository.save(offerAcceptanceSaga);
    }

    @DrivenPort
    @QueryOperation
    public OfferAcceptanceSagaDto statusOf(UUID offerId) {
        OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(offerId);

        return offerAcceptanceSaga.asDto();
    }
}
