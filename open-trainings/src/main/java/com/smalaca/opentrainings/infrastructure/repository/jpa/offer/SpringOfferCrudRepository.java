package com.smalaca.opentrainings.infrastructure.repository.jpa.offer;

import com.smalaca.opentrainings.domain.offer.Offer;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface SpringOfferCrudRepository extends CrudRepository<Offer, UUID> {
}
