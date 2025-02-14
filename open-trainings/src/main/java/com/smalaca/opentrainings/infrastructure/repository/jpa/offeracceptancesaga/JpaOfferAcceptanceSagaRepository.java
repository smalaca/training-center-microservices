package com.smalaca.opentrainings.infrastructure.repository.jpa.offeracceptancesaga;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSaga;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaRepository;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;

import java.util.UUID;

@DrivenAdapter
public class JpaOfferAcceptanceSagaRepository implements OfferAcceptanceSagaRepository {
    private final OfferAcceptanceSagaJpaEventCrudRepository repository;
    private final OfferAcceptanceSagaJpaEventMapper mapper;

    JpaOfferAcceptanceSagaRepository(OfferAcceptanceSagaJpaEventCrudRepository repository, OfferAcceptanceSagaJpaEventMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public OfferAcceptanceSaga findById(UUID offerId) {
        OfferAcceptanceSaga saga = OfferAcceptanceSaga.create(offerId);
        repository.findAllByOfferIdOrderByConsumedAtAsc(offerId)
                .forEach(event -> {
                    OfferAcceptanceSagaEvent offerAcceptanceSagaEvent = mapper.asEvent(event);
                    saga.load(offerAcceptanceSagaEvent, event.getConsumedAt());
                });

        return saga;
    }

    @Override
    public void save(OfferAcceptanceSaga offerAcceptanceSaga) {
        offerAcceptanceSaga.forEachEvent((event, consumedAt) -> {
            repository.save(mapper.create(event, consumedAt));
        });
    }
}
