package com.smalaca.opentrainings.infrastructure.repository.jpa.offeracceptancesaga;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

interface SpringOfferAcceptanceSagaEventCrudRepository extends CrudRepository<OfferAcceptanceSagaPersistableEvent, UUID> {
    List<OfferAcceptanceSagaPersistableEvent> findAllByOfferIdOrderByConsumedAtAsc(UUID offerId);
}
