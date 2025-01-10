package com.smalaca.opentrainings.domain.trainingoffercatalogue;

import com.smalaca.opentrainings.domain.price.Price;

public record TrainingDto(int availablePlaces, Price price) {
}
