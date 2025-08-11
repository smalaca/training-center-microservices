package com.smalaca.reviews.infrastructure.trainerscatalogue;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.reviews.domain.trainerscatalogue.Trainer;
import com.smalaca.reviews.domain.trainerscatalogue.TrainersCatalogue;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@DrivenAdapter
public class TrainersCatalogueRestClient implements TrainersCatalogue {
    @Override
    public List<Trainer> findAllTrainers() {
        return List.of();
    }
}
