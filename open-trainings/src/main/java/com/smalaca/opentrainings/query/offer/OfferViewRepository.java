package com.smalaca.opentrainings.query.offer;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface OfferViewRepository extends CrudRepository<OfferView, UUID> {
    @Query("SELECT o FROM OfferView o WHERE o.status = 'INITIATED' AND o.creationDateTime < :tenMinutesAgo")
    Iterable<OfferView> findAllInitiatedOffersOlderThan(@Param("tenMinutesAgo") LocalDateTime tenMinutesAgo);
}
