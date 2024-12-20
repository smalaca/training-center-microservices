package com.smalaca.opentrainings.infrastructure.repository.jpa.order;

import com.smalaca.opentrainings.domain.order.Order;
import com.smalaca.opentrainings.domain.order.OrderRepository;
import com.smalaca.opentrainings.domain.order.OrderTestDto;
import com.smalaca.opentrainings.domain.order.OrderTestFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomAmount;
import static com.smalaca.opentrainings.data.Random.randomCurrency;
import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.domain.order.OrderAssertion.assertThatOrder;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import(JpaOrderRepository.class)
class JpaOrderRepositoryIntegrationTest {
    @Autowired
    private OrderRepository repository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private final OrderTestFactory factory = OrderTestFactory.orderTestFactory();

    @Test
    void shouldFindNoOrderWhenDoesNotExist() {
        UUID orderId = randomId();
        Executable executable = () -> repository.findById(orderId);

        RuntimeException actual = assertThrows(OrderDoesNotExistException.class, executable);

        assertThat(actual).hasMessage("Order with id " + orderId + " does not exist.");
    }

    @Test
    void shouldSaveOrder() {
        OrderTestDto dto = randomOrderDto();
        Order order = factory.orderCreatedAt(dto);

        UUID orderId = transactionTemplate.execute(transactionStatus -> repository.save(order));

        assertThatInitiatedOrderHasDataEqualTo(orderId, dto);
    }

    @Test
    void shouldFindOrderById() {
        OrderTestDto dtoOne = randomOrderDto();
        UUID orderIdOne = transactionTemplate.execute(transactionStatus -> repository.save(factory.orderCreatedAt(dtoOne)));
        OrderTestDto dtoTwo = randomOrderDto();
        UUID orderIdTwo = transactionTemplate.execute(transactionStatus -> repository.save(factory.orderCreatedAt(dtoTwo)));
        OrderTestDto dtoThree = randomOrderDto();
        UUID orderIdThree = transactionTemplate.execute(transactionStatus -> repository.save(factory.orderCreatedAt(dtoThree)));
        OrderTestDto dtoFour = randomOrderDto();
        UUID orderIdFour = transactionTemplate.execute(transactionStatus -> repository.save(factory.orderCreatedAt(dtoFour)));
        OrderTestDto dtoFive = randomOrderDto();
        UUID orderIdFive = transactionTemplate.execute(transactionStatus -> repository.save(factory.orderCreatedAt(dtoFive)));

        assertThatInitiatedOrderHasDataEqualTo(orderIdOne, dtoOne);
        assertThatInitiatedOrderHasDataEqualTo(orderIdTwo, dtoTwo);
        assertThatInitiatedOrderHasDataEqualTo(orderIdThree, dtoThree);
        assertThatInitiatedOrderHasDataEqualTo(orderIdFour, dtoFour);
        assertThatInitiatedOrderHasDataEqualTo(orderIdFive, dtoFive);
    }

    private OrderTestDto randomOrderDto() {
        return OrderTestDto.builder()
                .trainingId(randomId())
                .participantId(randomId())
                .amount(randomAmount())
                .currency(randomCurrency())
                .creationDateTime(now())
                .build();
    }

    private void assertThatInitiatedOrderHasDataEqualTo(UUID orderId, OrderTestDto dto) {
        assertThatOrder(repository.findById(orderId))
                .isInitiated()
                .hasTrainingId(dto.getTrainingId())
                .hasParticipantId(dto.getParticipantId())
                .hasCreationDateTime(dto.getCreationDateTime())
                .hasPrice(dto.getAmount(), dto.getCurrency());
    }
}
