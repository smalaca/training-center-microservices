package com.smalaca.opentrainings.infrastructure.repository.jpa.offeracceptancesaga;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSaga;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaRepository;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;

import java.util.UUID;

@DrivenAdapter
public class JpaOfferAcceptanceSagaRepository implements OfferAcceptanceSagaRepository {
    private final SpringOfferAcceptanceSagaEventCrudRepository repository;
    private final OfferAcceptanceSagaJpaEventMapper mapper;

    JpaOfferAcceptanceSagaRepository(SpringOfferAcceptanceSagaEventCrudRepository repository, OfferAcceptanceSagaJpaEventMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public OfferAcceptanceSaga findById(UUID offerId) {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(offerId);
        repository.findAllByOfferIdOrderByConsumedAtAsc(offerId)
                .forEach(event -> {
                    OfferAcceptanceSagaEvent offerAcceptanceSagaEvent = mapper.offerAcceptanceSagaEventFrom(event);
                    saga.load(offerAcceptanceSagaEvent, event.getConsumedAt());
                });

        return saga;
    }

    @Override
    public void save(OfferAcceptanceSaga offerAcceptanceSaga) {
        offerAcceptanceSaga.readEachEvent((event, consumedAt) -> {
            repository.save(mapper.offerAcceptanceSagaJpaEventFrom(event, consumedAt));
        });
    }
}
