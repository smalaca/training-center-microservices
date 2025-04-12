package com.smalaca.opentrainings.query.order;

import com.smalaca.opentrainings.domain.order.GivenOrderFactory;
import com.smalaca.opentrainings.domain.order.OrderRepository;
import com.smalaca.opentrainings.domain.order.OrderTestDto;
import com.smalaca.opentrainings.infrastructure.clock.localdatetime.LocalDateTimeClock;
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
import static com.smalaca.opentrainings.query.order.OrderViewAssertion.assertThatOrder;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
@Import({JpaOrderRepository.class, OrderQueryService.class, LocalDateTimeClock.class})
class OrderQueryServiceIntegrationTest {
    @Autowired
    private OrderRepository repository;

    @Autowired
    private OrderQueryService queryService;

    @Autowired
    private TransactionTemplate transaction;

    private GivenOrderFactory given;

    @BeforeEach
    void init() {
        given = GivenOrderFactory.create(repository);
    }

    @Test
    void shouldFindNoOrderViewWhenDoesNotExist() {
        UUID orderId = randomId();

        Optional<OrderView> actual = queryService.findById(orderId);

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldFindOrderViewById() {
        OrderTestDto dto = transaction.execute(status -> given.order().initiated().getDto());

        Optional<OrderView> actual = queryService.findById(dto.getOrderId());

        assertThat(actual)
                .isPresent()
                .satisfies(orderView -> assertThatOrderHasDataEqualTo(orderView.get(), dto).hasStatus("INITIATED"));
    }

    @Test
    void shouldFindAllOrders() {
        OrderTestDto dtoOne = transaction.execute(status -> given.order().initiated().getDto());
        OrderTestDto dtoTwo = transaction.execute(status -> given.order().createdMinutesAgo(17).terminated().getDto());
        OrderTestDto dtoThree = transaction.execute(status -> given.order().cancelled().getDto());
        OrderTestDto dtoFour = transaction.execute(status -> given.order().confirmed().getDto());
        OrderTestDto dtoFive = transaction.execute(status -> given.order().rejected().getDto());

        Iterable<OrderView> actual = queryService.findAll();

        assertThat(actual).hasSize(5)
                .anySatisfy(orderView -> assertThatOrderHasDataEqualTo(orderView, dtoOne).hasStatus("INITIATED"))
                .anySatisfy(orderView -> assertThatOrderHasDataEqualTo(orderView, dtoTwo).hasStatus("TERMINATED"))
                .anySatisfy(orderView -> assertThatOrderHasDataEqualTo(orderView, dtoThree).hasStatus("CANCELLED"))
                .anySatisfy(orderView -> assertThatOrderHasDataEqualTo(orderView, dtoFour).hasStatus("CONFIRMED"))
                .anySatisfy(orderView -> assertThatOrderHasDataEqualTo(orderView, dtoFive).hasStatus("REJECTED"));
    }

    @Test
    void shouldFindAllOrderForTermination() {
        transaction.execute(status -> given.order().initiated());
        transaction.execute(status -> given.order().createdMinutesAgo(9).initiated());
        transaction.execute(status -> given.order().cancelled());
        transaction.execute(status -> given.order().createdMinutesAgo(13).cancelled());
        transaction.execute(status -> given.order().confirmed());
        transaction.execute(status -> given.order().createdMinutesAgo(21).confirmed());
        transaction.execute(status -> given.order().rejected());
        transaction.execute(status -> given.order().createdMinutesAgo(17).rejected());
        transaction.execute(status -> given.order().createdMinutesAgo(17).terminated());

        OrderTestDto dtoOne = transaction.execute(status -> given.order().createdMinutesAgo(17).initiated().getDto());
        OrderTestDto dtoTwo = transaction.execute(status -> given.order().createdMinutesAgo(11).initiated().getDto());
        OrderTestDto dtoThree = transaction.execute(status -> given.order().createdMinutesAgo(22).initiated().getDto());

        Iterable<OrderView> actual = queryService.findAllToTerminate();

        assertThat(actual).hasSize(3)
                .anySatisfy(orderView -> assertThatOrder(orderView).hasOrderId(dtoOne.getOrderId()))
                .anySatisfy(orderView -> assertThatOrder(orderView).hasOrderId(dtoTwo.getOrderId()))
                .anySatisfy(orderView -> assertThatOrder(orderView).hasOrderId(dtoThree.getOrderId()));
    }

    @Test
    void shouldFindNoOrderByOfferIdWhenDoesNotExist() {
        transaction.execute(status -> given.order().initiated());
        transaction.execute(status -> given.order().initiated());
        transaction.execute(status -> given.order().initiated());

        Optional<OrderView> actual = queryService.findByOfferId(randomId());

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldFindOrderByOfferId() {
        transaction.execute(status -> given.order().initiated().getDto());
        transaction.execute(status -> given.order().initiated().getDto());
        OrderTestDto dto = transaction.execute(status -> given.order().initiated().getDto());

        Optional<OrderView> actual = queryService.findByOfferId(dto.getOfferId());

        assertThatOrderHasDataEqualTo(actual.get(), dto).hasStatus("INITIATED");
    }

    private OrderViewAssertion assertThatOrderHasDataEqualTo(OrderView order, OrderTestDto dto) {
        return assertThatOrder(order)
                .hasOrderId(dto.getOrderId())
                .hasOfferId(dto.getOfferId())
                .hasTrainingId(dto.getTrainingId())
                .hasParticipantId(dto.getParticipantId())
                .hasCreationDateTime(dto.getCreationDateTime())
                .hasTrainingPriceAmount(dto.getTrainingPrice().amount())
                .hasTrainingPriceCurrency(dto.getTrainingPrice().currencyCode())
                .hasFinalPriceAmount(dto.getFinalPrice().amount())
                .hasFinalPriceCurrency(dto.getFinalPrice().currencyCode())
                .hasDiscountCode(dto.getDiscountCode())
                .hasValidOrderNumber();
    }
}