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
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.ConfirmTrainingPriceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.OfferAcceptanceSagaCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RejectOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeAlreadyUsedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeReturnedEvent;
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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@ApplicationLayer
public class OfferAcceptanceSagaEngine {
    private final Clock clock;
    private final OfferAcceptanceSagaRepository repository;
    private final CommandRegistry commandRegistry;
    private final Map<UUID, Object> locks = new ConcurrentHashMap<>();

    OfferAcceptanceSagaEngine(Clock clock, OfferAcceptanceSagaRepository repository, CommandRegistry commandRegistry) {
        this.clock = clock;
        this.repository = repository;
        this.commandRegistry = commandRegistry;
    }

    private <T> T synchronizedExecute(UUID offerId, SagaOperation<T> operation) {
        Object lock = locks.computeIfAbsent(offerId, k -> new Object());
        synchronized (lock) {
            try {
                return operation.execute();
            } finally {
                locks.remove(offerId);
            }
        }
    }

    private interface SagaOperation<T> {
        T execute();
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(OfferAcceptanceRequestedEvent event) {
        synchronizedExecute(event.offerId(), () -> {
            OfferAcceptanceSaga offerAcceptanceSaga = new OfferAcceptanceSaga(event.offerId());

            List<OfferAcceptanceSagaCommand> commands = offerAcceptanceSaga.accept(event, clock);

            commands.forEach(commandRegistry::publish);
            repository.save(offerAcceptanceSaga);
            return null;
        });
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(PersonRegisteredEvent event) {
        synchronizedExecute(event.offerId(), () -> {
            OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

            List<OfferAcceptanceSagaCommand> commands = offerAcceptanceSaga.accept(event, clock);

            commands.forEach(commandRegistry::publish);
            repository.save(offerAcceptanceSaga);
            return null;
        });
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(AlreadyRegisteredPersonFoundEvent event) {
        synchronizedExecute(event.offerId(), () -> {
            OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

            List<OfferAcceptanceSagaCommand> commands = offerAcceptanceSaga.accept(event, clock);

            commands.forEach(commandRegistry::publish);
            repository.save(offerAcceptanceSaga);
            return null;
        });
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(UnexpiredOfferAcceptanceRequestedEvent event) {
        synchronizedExecute(event.offerId(), () -> {
            OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

            List<OfferAcceptanceSagaCommand> commands = offerAcceptanceSaga.accept(event, clock);

            commands.forEach(commandRegistry::publish);
            repository.save(offerAcceptanceSaga);
            return null;
        });
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(ExpiredOfferAcceptanceRequestedEvent event) {
        synchronizedExecute(event.offerId(), () -> {
            OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

            ConfirmTrainingPriceCommand command = offerAcceptanceSaga.accept(event, clock);

            commandRegistry.publish(command);
            repository.save(offerAcceptanceSaga);
            return null;
        });
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(TrainingPriceNotChangedEvent event) {
        synchronizedExecute(event.offerId(), () -> {
            OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

            List<OfferAcceptanceSagaCommand> commands = offerAcceptanceSaga.accept(event, clock);

            commands.forEach(commandRegistry::publish);
            repository.save(offerAcceptanceSaga);
            return null;
        });
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(TrainingPriceChangedEvent event) {
        synchronizedExecute(event.offerId(), () -> {
            OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

            RejectOfferCommand command = offerAcceptanceSaga.accept(event, clock);

            commandRegistry.publish(command);
            repository.save(offerAcceptanceSaga);
            return null;
        });
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(DiscountCodeUsedEvent event) {
        synchronizedExecute(event.offerId(), () -> {
            OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

            Optional<OfferAcceptanceSagaCommand> command = offerAcceptanceSaga.accept(event, clock);

            command.ifPresent(commandRegistry::publish);
            repository.save(offerAcceptanceSaga);
            return null;
        });
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(DiscountCodeAlreadyUsedEvent event) {
        synchronizedExecute(event.offerId(), () -> {
            OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

            Optional<OfferAcceptanceSagaCommand> command = offerAcceptanceSaga.accept(event, clock);

            command.ifPresent(commandRegistry::publish);
            repository.save(offerAcceptanceSaga);
            return null;
        });
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(TrainingPlaceBookedEvent event) {
        synchronizedExecute(event.offerId(), () -> {
            OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

            Optional<OfferAcceptanceSagaCommand> command = offerAcceptanceSaga.accept(event, clock);

            command.ifPresent(commandRegistry::publish);
            repository.save(offerAcceptanceSaga);
            return null;
        });
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(NoAvailableTrainingPlacesLeftEvent event) {
        synchronizedExecute(event.offerId(), () -> {
            OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

            List<OfferAcceptanceSagaCommand> commands = offerAcceptanceSaga.accept(event, clock);

            commands.forEach(commandRegistry::publish);
            repository.save(offerAcceptanceSaga);
            return null;
        });
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(NotAvailableOfferAcceptanceRequestedEvent event) {
        synchronizedExecute(event.offerId(), () -> {
            OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

            offerAcceptanceSaga.accept(event, clock);

            repository.save(offerAcceptanceSaga);
            return null;
        });
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(OfferAcceptedEvent event) {
        synchronizedExecute(event.offerId(), () -> {
            OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

            offerAcceptanceSaga.accept(event, clock);

            repository.save(offerAcceptanceSaga);
            return null;
        });
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(OfferRejectedEvent event) {
        synchronizedExecute(event.offerId(), () -> {
            OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

            offerAcceptanceSaga.accept(event, clock);

            repository.save(offerAcceptanceSaga);
            return null;
        });
    }

    @Transactional
    @DrivenPort
    @CommandOperation
    public void accept(DiscountCodeReturnedEvent event) {
        synchronizedExecute(event.offerId(), () -> {
            OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(event.offerId());

            offerAcceptanceSaga.accept(event, clock);

            repository.save(offerAcceptanceSaga);
            return null;
        });
    }

    @DrivenPort
    @QueryOperation
    public OfferAcceptanceSagaDto statusOf(UUID offerId) {
        return synchronizedExecute(offerId, () -> {
            OfferAcceptanceSaga offerAcceptanceSaga = repository.findById(offerId);
            return offerAcceptanceSaga.asDto();
        });
    }
}
