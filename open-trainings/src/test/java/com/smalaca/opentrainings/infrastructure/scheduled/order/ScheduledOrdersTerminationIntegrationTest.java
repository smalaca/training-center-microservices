package com.smalaca.opentrainings.infrastructure.scheduled.order;

import com.smalaca.opentrainings.application.order.OrderApplicationService;
import com.smalaca.opentrainings.domain.order.GivenOrderFactory;
import com.smalaca.opentrainings.domain.order.OrderRepository;
import com.smalaca.opentrainings.domain.order.OrderTestDto;
import com.smalaca.opentrainings.query.order.OrderQueryService;
import com.smalaca.opentrainings.query.order.OrderView;
import com.smalaca.test.type.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;

import static com.smalaca.opentrainings.query.order.OrderViewAssertion.assertThatOrder;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@SpringBootTest
@IntegrationTest
@TestPropertySource(properties = "scheduled.order.termination.rate=100")
class ScheduledOrdersTerminationIntegrationTest {
    @SpyBean
    private OrderApplicationService applicationService;

    @Autowired
    private OrderQueryService service;

    @Autowired
    private OrderRepository repository;

    @Autowired
    private TransactionTemplate transaction;

    private GivenOrderFactory given;

    @BeforeEach
    void init() {
        given = GivenOrderFactory.create(repository);
    }

    @Test
    void shouldTerminateNothingWhenNoOrderFound() {
        givenNotTerminableOrders();

        await()
                .pollDelay(Duration.ofMillis(500))
                .untilAsserted(() -> {
                    then(applicationService).should(never()).terminate(any());
                });
    }

    @Test
    void shouldTerminateFoundOrders() {
        givenNotTerminableOrders();
        OrderTestDto dtoOne = transaction.execute(status -> given.order().createdMinutesAgo(17).initiated().getDto());
        OrderTestDto dtoTwo = transaction.execute(status -> given.order().createdMinutesAgo(10).initiated().getDto());
        OrderTestDto dtoThree = transaction.execute(status -> given.order().createdMinutesAgo(22).initiated().getDto());

        await()
                .untilAsserted(() -> {
                    assertThatOrderIsTerminated(dtoOne);
                    assertThatOrderIsTerminated(dtoTwo);
                    assertThatOrderIsTerminated(dtoThree);
                });
    }

    private void assertThatOrderIsTerminated(OrderTestDto dto) {
        OrderView actual = service.findById(dto.getOrderId()).get();

        assertThatOrder(actual).hasStatus("TERMINATED");
    }

    private void givenNotTerminableOrders() {
        transaction.execute(status -> given.order().initiated());
        transaction.execute(status -> given.order().createdMinutesAgo(9).initiated());
        transaction.execute(status -> given.order().cancelled());
        transaction.execute(status -> given.order().createdMinutesAgo(13).cancelled());
        transaction.execute(status -> given.order().confirmed());
        transaction.execute(status -> given.order().createdMinutesAgo(21).confirmed());
        transaction.execute(status -> given.order().rejected());
        transaction.execute(status -> given.order().createdMinutesAgo(17).rejected());
        transaction.execute(status -> given.order().createdMinutesAgo(17).terminated());
    }
}