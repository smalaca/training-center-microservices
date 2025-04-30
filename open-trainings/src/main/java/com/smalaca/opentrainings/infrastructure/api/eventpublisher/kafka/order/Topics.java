package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order;

record Topics(String trainingPurchased, String orderRejected, String orderTerminated) {
}
