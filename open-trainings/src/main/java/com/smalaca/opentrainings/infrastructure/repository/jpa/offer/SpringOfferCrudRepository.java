package com.smalaca.opentrainings.infrastructure.repository.jpa.offer;

import com.smalaca.opentrainings.domain.offer.Offer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringOfferCrudRepository extends CrudRepository<Offer, UUID> {
}
