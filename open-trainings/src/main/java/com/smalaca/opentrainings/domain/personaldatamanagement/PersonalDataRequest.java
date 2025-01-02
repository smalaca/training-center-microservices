package com.smalaca.opentrainings.domain.personaldatamanagement;

public record PersonalDataRequest(String firstName, String lastName, String email) {
    public static PersonalDataRequestBuilder builder() {
        return new PersonalDataRequestBuilder();
    }

    public static class PersonalDataRequestBuilder {
        private String firstName;
        private String lastName;
        private String email;

        public PersonalDataRequestBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public PersonalDataRequestBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public PersonalDataRequestBuilder email(String email) {
            this.email = email;
            return this;
        }

        public PersonalDataRequest build() {
            return new PersonalDataRequest(firstName, lastName, email);
        }
    }
}
