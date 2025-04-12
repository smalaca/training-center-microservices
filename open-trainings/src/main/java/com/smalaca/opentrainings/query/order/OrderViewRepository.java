package com.smalaca.opentrainings.query.order;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderViewRepository extends CrudRepository<OrderView, UUID> {
    @Query("SELECT o FROM OrderView o WHERE o.creationDateTime < :tenMinutesAgo AND o.status = 'INITIATED'")
    List<OrderView> findInitiatedOrdersOlderThan(@Param("tenMinutesAgo") LocalDateTime tenMinutesAgo);

    Optional<OrderView> findByOfferId(UUID offerId);
}
