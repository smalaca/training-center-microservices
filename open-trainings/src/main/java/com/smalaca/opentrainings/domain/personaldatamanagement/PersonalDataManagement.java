package com.smalaca.opentrainings.domain.personaldatamanagement;

import com.smalaca.architecture.portsandadapters.DrivenPort;

@DrivenPort
public interface PersonalDataManagement {
    PersonalDataResponse save(PersonalDataRequest request);
}
