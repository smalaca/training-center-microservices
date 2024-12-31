package com.smalaca.opentrainings.query.order;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderDtoRepository extends CrudRepository<OrderDto, UUID> {
    @Query("SELECT o FROM OrderDto o WHERE o.creationDateTime < :tenMinutesAgo AND o.status = 'INITIATED'")
    List<OrderDto> findInitiatedOrdersOlderThan(@Param("tenMinutesAgo") LocalDateTime tenMinutesAgo);
}
