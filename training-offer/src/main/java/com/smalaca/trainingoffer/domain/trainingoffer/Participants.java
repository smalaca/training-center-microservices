package com.smalaca.trainingoffer.domain.trainingoffer;

import com.smalaca.domaindrivendesign.DomainEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static jakarta.persistence.FetchType.EAGER;

@DomainEntity
@Embeddable
class Participants {
    @ElementCollection(fetch = EAGER)
    @CollectionTable(name = "TRAINING_OFFER_PARTICIPANTS", joinColumns = @JoinColumn(name = "TRAINING_OFFER_ID"))
    @Column(name = "PARTICIPANT_ID")
    private Set<UUID> participantIds;

    @Column(name = "MINIMUM_PARTICIPANTS")
    private int minimumParticipants;

    @Column(name = "MAXIMUM_PARTICIPANTS")
    private int maximumParticipants;

    private Participants() {}

    private Participants(int minimumParticipants, int maximumParticipants, HashSet<UUID> participantIds) {
        this.minimumParticipants = minimumParticipants;
        this.maximumParticipants = maximumParticipants;
        this.participantIds = participantIds;
    }

    static Participants from(int minimumParticipants, int maximumParticipants) {
        return new Participants(minimumParticipants, maximumParticipants, new HashSet<>());
    }
}