package com.smalaca.opentrainings.infrastructure.repository.jpa.offeracceptancesaga;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

interface OfferAcceptanceSagaJpaEventCrudRepository extends CrudRepository<OfferAcceptanceSagaJpaEvent, UUID> {
    List<OfferAcceptanceSagaJpaEvent> findAllByOfferIdOrderByConsumedAtAsc(UUID offerId);
}
