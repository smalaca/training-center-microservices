package com.smalaca.opentrainings.infrastructure.personaldatamanagement;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataManagement;
import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataRequest;
import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataResponse;
import org.springframework.stereotype.Service;

@Service
@DrivenAdapter
public class PersonalDataManagementRestClient implements PersonalDataManagement {
    @Override
    public PersonalDataResponse save(PersonalDataRequest request) {
        return PersonalDataResponse.failed();
    }
}
