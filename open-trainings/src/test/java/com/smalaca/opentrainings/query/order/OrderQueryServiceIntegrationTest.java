package com.smalaca.opentrainings.query.order;

import com.smalaca.opentrainings.domain.order.OrderRepository;
import com.smalaca.opentrainings.domain.order.OrderTestDto;
import com.smalaca.opentrainings.domain.order.OrderTestFactory;
import com.smalaca.opentrainings.infrastructure.repository.jpa.order.JpaOrderRepository;
import com.smalaca.test.type.RepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomAmount;
import static com.smalaca.opentrainings.data.Random.randomCurrency;
import static com.smalaca.opentrainings.data.Random.randomId;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
@Import({JpaOrderRepository.class, OrderQueryService.class})
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

        Optional<OrderDto> actual = queryService.findById(orderId);

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldFindOrderDtoById() {
        OrderTestDto dto = randomOrderDto();
        UUID orderId = givenOrder(dto);

        Optional<OrderDto> actual = queryService.findById(orderId);

        assertThat(actual)
                .isPresent()
                .satisfies(orderDto -> assertThatInitiatedOrderHasDataEqualTo(orderDto.get(), dto));
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