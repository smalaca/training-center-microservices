package com.smalaca.opentrainings.query.order;

import com.smalaca.opentrainings.domain.order.GivenOrderFactory;
import com.smalaca.opentrainings.domain.order.OrderRepository;
import com.smalaca.opentrainings.domain.order.OrderTestDto;
import com.smalaca.opentrainings.infrastructure.repository.jpa.order.JpaOrderRepository;
import com.smalaca.test.type.RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.query.order.OrderDtoAssertion.assertThatOrder;
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

    private GivenOrderFactory given;

    @BeforeEach
    void init() {
        given = GivenOrderFactory.create(repository);
    }

    @Test
    void shouldFindNoOrderDtoWhenDoesNotExist() {
        UUID orderId = randomId();

        Optional<OrderDto> actual = queryService.findById(orderId);

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldFindOrderDtoById() {
        OrderTestDto dto = transactionTemplate.execute(transactionStatus -> given.order().initiated().getDto());

        Optional<OrderDto> actual = queryService.findById(dto.getOrderId());

        assertThat(actual)
                .isPresent()
                .satisfies(orderDto -> assertThatOrderHasDataEqualTo(orderDto.get(), dto).hasStatus("INITIATED"));
    }

    @Test
    void shouldFindAllOrders() {
        OrderTestDto dtoOne = transactionTemplate.execute(transactionStatus -> given.order().initiated().getDto());
        OrderTestDto dtoTwo = transactionTemplate.execute(transactionStatus -> given.order().createdMinutesAgo(17).terminated().getDto());
        OrderTestDto dtoThree = transactionTemplate.execute(transactionStatus -> given.order().cancelled().getDto());
        OrderTestDto dtoFour = transactionTemplate.execute(transactionStatus -> given.order().confirmed().getDto());
        OrderTestDto dtoFive = transactionTemplate.execute(transactionStatus -> given.order().rejected().getDto());

        Iterable<OrderDto> actual = queryService.findAll();

        assertThat(actual).hasSize(5)
                .anySatisfy(orderDto -> assertThatOrderHasDataEqualTo(orderDto, dtoOne).hasStatus("INITIATED"))
                .anySatisfy(orderDto -> assertThatOrderHasDataEqualTo(orderDto, dtoTwo).hasStatus("TERMINATED"))
                .anySatisfy(orderDto -> assertThatOrderHasDataEqualTo(orderDto, dtoThree).hasStatus("CANCELLED"))
                .anySatisfy(orderDto -> assertThatOrderHasDataEqualTo(orderDto, dtoFour).hasStatus("CONFIRMED"))
                .anySatisfy(orderDto -> assertThatOrderHasDataEqualTo(orderDto, dtoFive).hasStatus("REJECTED"));
    }

    private OrderDtoAssertion assertThatOrderHasDataEqualTo(OrderDto order, OrderTestDto dto) {
        return assertThatOrder(order)
                .hasOrderId(dto.getOrderId())
                .hasTrainingId(dto.getTrainingId())
                .hasParticipantId(dto.getParticipantId())
                .hasCreationDateTime(dto.getCreationDateTime())
                .hasPriceAmount(dto.getAmount())
                .hasPriceCurrency(dto.getCurrency());
    }
}