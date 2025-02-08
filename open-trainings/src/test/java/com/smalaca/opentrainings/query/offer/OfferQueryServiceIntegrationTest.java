package com.smalaca.opentrainings.query.offer;

import com.smalaca.opentrainings.domain.offer.GivenOfferFactory;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.offer.OfferTestDto;
import com.smalaca.opentrainings.infrastructure.repository.jpa.offer.JpaOfferRepository;
import com.smalaca.test.type.RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.query.offer.OfferViewAssertion.assertThatOffer;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
@Import({JpaOfferRepository.class, OfferQueryService.class})
class OfferQueryServiceIntegrationTest {
    @Autowired
    private OfferRepository repository;

    @Autowired
    private OfferQueryService queryService;

    @Autowired
    private TransactionTemplate transaction;

    private GivenOfferFactory given;

    @BeforeEach
    void init() {
        given = GivenOfferFactory.create(repository);
    }

    @Test
    void shouldFindNoOfferViewWhenDoesNotExist() {
        UUID offerId = randomId();

        Optional<OfferView> actual = queryService.findById(offerId);

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldFindOfferViewById() {
        OfferTestDto dto = transaction.execute(status -> given.offer().initiated().getDto());

        Optional<OfferView> actual = queryService.findById(dto.getOfferId());

        assertThat(actual)
                .isPresent()
                .satisfies(offerView -> assertThatOfferHasDataEqualTo(offerView.get(), dto).hasStatus("INITIATED"));
    }

    @Test
    void shouldFindAllOffers() {
        OfferTestDto dtoOne = transaction.execute(status -> given.offer().initiated().getDto());
        OfferTestDto dtoTwo = transaction.execute(status -> given.offer().createdMinutesAgo(17).terminated().getDto());
        OfferTestDto dtoThree = transaction.execute(status -> given.offer().accepted().getDto());
        OfferTestDto dtoFour = transaction.execute(status -> given.offer().declined().getDto());
        OfferTestDto dtoFive = transaction.execute(status -> given.offer().rejected().getDto());

        Iterable<OfferView> actual = queryService.findAll();

        assertThat(actual).hasSize(5)
                .anySatisfy(offerView -> assertThatOfferHasDataEqualTo(offerView, dtoOne).hasStatus("INITIATED"))
                .anySatisfy(offerView -> assertThatOfferHasDataEqualTo(offerView, dtoTwo).hasStatus("TERMINATED"))
                .anySatisfy(offerView -> assertThatOfferHasDataEqualTo(offerView, dtoThree).hasStatus("ACCEPTED"))
                .anySatisfy(offerView -> assertThatOfferHasDataEqualTo(offerView, dtoFour).hasStatus("DECLINED"))
                .anySatisfy(offerView -> assertThatOfferHasDataEqualTo(offerView, dtoFive).hasStatus("REJECTED"));
    }

    private OfferViewAssertion assertThatOfferHasDataEqualTo(OfferView offer, OfferTestDto dto) {
        return assertThatOffer(offer)
                .hasOfferId(dto.getOfferId())
                .hasTrainingId(dto.getTrainingId())
                .hasCreationDateTime(dto.getCreationDateTime())
                .hasTrainingPriceAmount(dto.getTrainingPrice().amount())
                .hasTrainingPriceCurrency(dto.getTrainingPrice().currencyCode())
                .hasValidOfferNumber();
    }
}