package com.hanwha.tax.apiserver.model.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class User {

    @Getter
    @AllArgsConstructor
    public enum Gender {
        @JsonProperty("M")
        MALE,
        @JsonProperty("F")
        FEMALE;

        public Character toCharacter() {
            return this == MALE ? 'M' : 'F';
        }

        public static Gender parse(Character gender) {
            return gender == 'M' ? MALE : FEMALE;
        }
    }
}
