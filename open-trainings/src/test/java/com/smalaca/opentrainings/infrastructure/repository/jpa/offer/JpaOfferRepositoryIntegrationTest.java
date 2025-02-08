package com.smalaca.opentrainings.infrastructure.repository.jpa.offer;

import com.smalaca.opentrainings.domain.offer.GivenOffer;
import com.smalaca.opentrainings.domain.offer.GivenOfferFactory;
import com.smalaca.opentrainings.domain.offer.OfferAssertion;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.offer.OfferTestDto;
import com.smalaca.test.type.RepositoryTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.domain.offer.OfferAssertion.assertThatOffer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@RepositoryTest
@Import(JpaOfferRepository.class)
class JpaOfferRepositoryIntegrationTest {
    @Autowired
    private OfferRepository repository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private final GivenOfferFactory given = GivenOfferFactory.withoutPersistence();

    @Test
    void shouldFindNoOfferWhenDoesNotExist() {
        UUID offerId = randomId();
        Executable executable = () -> repository.findById(offerId);

        RuntimeException actual = assertThrows(OfferDoesNotExistException.class, executable);

        assertThat(actual).hasMessage("Offer with id " + offerId + " does not exist.");
    }

    @Test
    void shouldSaveOffer() {
        GivenOffer offer = given.offer().initiated();

        UUID offerId = transactionTemplate.execute(transactionStatus -> repository.save(offer.getOffer()));

        assertThatOfferHasDataEqualTo(offerId, offer.getDto());
    }

    @Test
    void shouldFindOfferById() {
        GivenOffer offerOne = given.offer().initiated();
        UUID offerIdOne = transactionTemplate.execute(transactionStatus -> repository.save(offerOne.getOffer()));
        GivenOffer offerTwo = given.offer().rejected();
        UUID offerIdTwo = transactionTemplate.execute(transactionStatus -> repository.save(offerTwo.getOffer()));
        GivenOffer offerThree = given.offer().createdMinutesAgo(20).terminated();
        UUID offerIdThree = transactionTemplate.execute(transactionStatus -> repository.save(offerThree.getOffer()));
        GivenOffer offerFour = given.offer().accepted();
        UUID offerIdFour = transactionTemplate.execute(transactionStatus -> repository.save(offerFour.getOffer()));
        GivenOffer offerFive = given.offer().declined();
        UUID offerIdFive = transactionTemplate.execute(transactionStatus -> repository.save(offerFive.getOffer()));

        assertThatOfferHasDataEqualTo(offerIdOne, offerOne.getDto()).isInitiated();
        assertThatOfferHasDataEqualTo(offerIdTwo, offerTwo.getDto()).isRejected();
        assertThatOfferHasDataEqualTo(offerIdThree, offerThree.getDto()).isTerminated();
        assertThatOfferHasDataEqualTo(offerIdFour, offerFour.getDto()).isAccepted();
        assertThatOfferHasDataEqualTo(offerIdFive, offerFive.getDto()).isDeclined();
    }

    private OfferAssertion assertThatOfferHasDataEqualTo(UUID offerId, OfferTestDto expected) {
        return assertThatOffer(repository.findById(offerId))
                .hasOfferId(offerId)
                .hasTrainingId(expected.getTrainingId())
                .hasValidOfferNumber()
                .hasTrainingPrice(expected.getTrainingPrice())
                .hasCreationDateTime(expected.getCreationDateTime());
    }
}