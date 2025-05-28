package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.offer;

record Topics(String registerPerson, String useDiscountCode, String confirmTrainingPrice, String bookTrainingPlace, String returnDiscountCode) {
}
