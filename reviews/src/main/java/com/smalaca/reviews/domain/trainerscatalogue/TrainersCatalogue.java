package com.smalaca.reviews.domain.trainerscatalogue;

import com.smalaca.architecture.portsandadapters.DrivenPort;

import java.util.List;

@DrivenPort
public interface TrainersCatalogue {
    List<Trainer> findAllTrainers();
}