package com.smalaca.opentrainings.query.offer;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OfferViewRepository extends CrudRepository<OfferView, UUID> {
}
