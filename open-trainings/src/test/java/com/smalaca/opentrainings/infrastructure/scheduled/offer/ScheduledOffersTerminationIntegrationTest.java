package com.smalaca.opentrainings.infrastructure.scheduled.offer;

import com.smalaca.opentrainings.annotation.disable.DisabledKafkaIntegration;
import com.smalaca.opentrainings.application.offer.OfferApplicationService;
import com.smalaca.opentrainings.domain.offer.GivenOfferFactory;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.offer.OfferTestDto;
import com.smalaca.opentrainings.query.offer.OfferQueryService;
import com.smalaca.opentrainings.query.offer.OfferView;
import com.smalaca.test.type.SpringBootIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;

import static com.smalaca.opentrainings.query.offer.OfferViewAssertion.assertThatOffer;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@SpringBootIntegrationTest
@TestPropertySource(properties = "scheduled.offer.termination.rate=100")
@DisabledKafkaIntegration
class ScheduledOffersTerminationIntegrationTest {
    @SpyBean
    private OfferApplicationService applicationService;

    @Autowired
    private OfferQueryService service;

    @Autowired
    private OfferRepository repository;

    @Autowired
    private TransactionTemplate transaction;

    private GivenOfferFactory given;

    @BeforeEach
    void init() {
        given = GivenOfferFactory.create(repository);
    }

    @Test
    void shouldTerminateNothingWhenNoOfferFound() {
        givenNotTerminableOffers();

        await()
                .pollDelay(Duration.ofMillis(500))
                .untilAsserted(() -> {
                    then(applicationService).should(never()).terminate(any());
                });
    }

    @Test
    void shouldTerminateFoundOffers() {
        givenNotTerminableOffers();
        OfferTestDto dtoOne = transaction.execute(status -> given.offer().createdMinutesAgo(17).initiated().getDto());
        OfferTestDto dtoTwo = transaction.execute(status -> given.offer().createdMinutesAgo(10).initiated().getDto());
        OfferTestDto dtoThree = transaction.execute(status -> given.offer().createdMinutesAgo(22).initiated().getDto());

        await()
                .untilAsserted(() -> {
                    assertThatOfferIsTerminated(dtoOne);
                    assertThatOfferIsTerminated(dtoTwo);
                    assertThatOfferIsTerminated(dtoThree);
                });
    }

    private void assertThatOfferIsTerminated(OfferTestDto dto) {
        OfferView actual = service.findById(dto.getOfferId()).get();

        assertThatOffer(actual).hasStatus("TERMINATED");
    }

    private void givenNotTerminableOffers() {
        transaction.execute(status -> given.offer().initiated());
        transaction.execute(status -> given.offer().createdMinutesAgo(9).initiated());
        transaction.execute(status -> given.offer().rejected());
        transaction.execute(status -> given.offer().declined());
        transaction.execute(status -> given.offer().rejected());
        transaction.execute(status -> given.offer().createdMinutesAgo(17).rejected());
        transaction.execute(status -> given.offer().createdMinutesAgo(17).terminated());
    }
}