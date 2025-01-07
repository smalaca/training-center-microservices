package com.smalaca.opentrainings.infrastructure.repository.jpa.order;

import com.smalaca.opentrainings.domain.order.GivenOrder;
import com.smalaca.opentrainings.domain.order.GivenOrderFactory;
import com.smalaca.opentrainings.domain.order.OrderAssertion;
import com.smalaca.opentrainings.domain.order.OrderRepository;
import com.smalaca.opentrainings.domain.order.OrderTestDto;
import com.smalaca.opentrainings.domain.price.Price;
import com.smalaca.test.type.RepositoryTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.domain.order.OrderAssertion.assertThatOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RepositoryTest
@Import(JpaOrderRepository.class)
class JpaOrderRepositoryIntegrationTest {
    @Autowired
    private OrderRepository repository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private final GivenOrderFactory given = GivenOrderFactory.withoutPersistence();

    @Test
    void shouldFindNoOrderWhenDoesNotExist() {
        UUID orderId = randomId();
        Executable executable = () -> repository.findById(orderId);

        RuntimeException actual = assertThrows(OrderDoesNotExistException.class, executable);

        assertThat(actual).hasMessage("Order with id " + orderId + " does not exist.");
    }

    @Test
    void shouldSaveOrder() {
        GivenOrder order = given.order().initiated();

        UUID orderId = transactionTemplate.execute(transactionStatus -> repository.save(order.getOrder()));

        assertThatOrderHasDataEqualTo(orderId, order.getDto());
    }

    @Test
    void shouldFindOrderById() {
        GivenOrder orderOne = given.order().initiated();
        UUID orderIdOne = transactionTemplate.execute(transactionStatus -> repository.save(orderOne.getOrder()));
        GivenOrder orderTwo = given.order().rejected();
        UUID orderIdTwo = transactionTemplate.execute(transactionStatus -> repository.save(orderTwo.getOrder()));
        GivenOrder orderThree = given.order().createdMinutesAgo(20).terminated();
        UUID orderIdThree = transactionTemplate.execute(transactionStatus -> repository.save(orderThree.getOrder()));
        GivenOrder orderFour = given.order().confirmed();
        UUID orderIdFour = transactionTemplate.execute(transactionStatus -> repository.save(orderFour.getOrder()));
        GivenOrder orderFive = given.order().cancelled();
        UUID orderIdFive = transactionTemplate.execute(transactionStatus -> repository.save(orderFive.getOrder()));

        assertThatOrderHasDataEqualTo(orderIdOne, orderOne.getDto()).isInitiated();
        assertThatOrderHasDataEqualTo(orderIdTwo, orderTwo.getDto()).isRejected();
        assertThatOrderHasDataEqualTo(orderIdThree, orderThree.getDto()).isTerminated();
        assertThatOrderHasDataEqualTo(orderIdFour, orderFour.getDto()).isConfirmed();
        assertThatOrderHasDataEqualTo(orderIdFive, orderFive.getDto()).isCancelled();
    }

    private OrderAssertion assertThatOrderHasDataEqualTo(UUID orderId, OrderTestDto expected) {
        return assertThatOrder(repository.findById(orderId))
                .hasOrderId(orderId)
                .hasOfferId(expected.getOfferId())
                .hasTrainingId(expected.getTrainingId())
                .hasParticipantId(expected.getParticipantId())
                .hasCreationDateTime(expected.getCreationDateTime())
                .hasTrainingPrice(Price.of(expected.getAmount(), expected.getCurrency()));
    }
}
