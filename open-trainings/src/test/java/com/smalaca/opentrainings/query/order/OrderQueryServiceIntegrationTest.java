package com.smalaca.opentrainings.query.order;

import com.smalaca.opentrainings.domain.order.OrderRepository;
import com.smalaca.opentrainings.domain.order.OrderTestDto;
import com.smalaca.opentrainings.domain.order.OrderTestFactory;
import com.smalaca.opentrainings.infrastructure.repository.jpa.order.JpaOrderRepository;
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
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({JpaOrderRepository.class, OrderQueryService.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class OrderQueryServiceIntegrationTest {
    @Autowired
    private OrderRepository repository;

    @Autowired
    private OrderQueryService queryService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private final OrderTestFactory factory = OrderTestFactory.orderTestFactory();

    @Test
    void shouldFindNoOrderDtoWhenDoesNotExist() {
        UUID orderId = randomId();
        Executable executable = () -> queryService.findById(orderId);

        RuntimeException actual = assertThrows(OrderDtoDoesNotExistException.class, executable);

        assertThat(actual).hasMessage("Order with id " + orderId + " does not exist.");
    }

    @Test
    void shouldFindOrderDtoById() {
        OrderTestDto dto = randomOrderDto();
        UUID orderId = givenOrder(dto);

        OrderDto actual = queryService.findById(orderId);

        assertThatInitiatedOrderHasDataEqualTo(actual, dto);
    }

    @Test
    void shouldFindAllOrders() {
        OrderTestDto dtoOne = randomOrderDto();
        UUID orderIdOne = givenOrder(dtoOne);
        OrderTestDto dtoTwo = randomOrderDto();
        UUID orderIdTwo = givenOrder(dtoTwo);
        OrderTestDto dtoThree = randomOrderDto();
        UUID orderIdThree = givenOrder(dtoThree);
        OrderTestDto dtoFour = randomOrderDto();
        UUID orderIdFour = givenOrder(dtoFour);
        OrderTestDto dtoFive = randomOrderDto();
        UUID orderIdFive = givenOrder(dtoFive);

        Iterable<OrderDto> actual = queryService.findAll();

        assertThat(actual).hasSize(5)
                .anySatisfy(orderDto -> assertThatInitiatedOrderHasDataEqualTo(orderDto, orderIdOne, dtoOne))
                .anySatisfy(orderDto -> assertThatInitiatedOrderHasDataEqualTo(orderDto, orderIdTwo, dtoTwo))
                .anySatisfy(orderDto -> assertThatInitiatedOrderHasDataEqualTo(orderDto, orderIdThree, dtoThree))
                .anySatisfy(orderDto -> assertThatInitiatedOrderHasDataEqualTo(orderDto, orderIdFour, dtoFour))
                .anySatisfy(orderDto -> assertThatInitiatedOrderHasDataEqualTo(orderDto, orderIdFive, dtoFive));
    }

    private UUID givenOrder(OrderTestDto dtoFive) {
        return transactionTemplate.execute(transactionStatus -> repository.save(factory.orderCreatedAt(dtoFive)));
    }

    private void assertThatInitiatedOrderHasDataEqualTo(OrderDto order, UUID orderId, OrderTestDto dto) {
        assertThatInitiatedOrderHasDataEqualTo(order, dto).hasOrderId(orderId);
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

    private OrderDtoAssertion assertThatInitiatedOrderHasDataEqualTo(OrderDto order, OrderTestDto dto) {
        return OrderDtoAssertion.assertThatOrder(order)
                .hasStatus("INITIATED")
                .hasTrainingId(dto.getTrainingId())
                .hasParticipantId(dto.getParticipantId())
                .hasCreationDateTime(dto.getCreationDateTime())
                .hasPriceAmount(dto.getAmount())
                .hasPriceCurrency(dto.getCurrency());
    }
}