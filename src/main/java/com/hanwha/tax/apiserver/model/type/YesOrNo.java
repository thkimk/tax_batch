package com.hanwha.tax.apiserver.model.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum YesOrNo {
    @JsonProperty("Y")
    YES,
    @JsonProperty("N")
    NO;

    public boolean isYes() {
        return this == YES;
    }

    public boolean isNo() {
        return this == NO;
    }

    public Character toCharacter() {
        return this == YES ? 'Y' : 'N';
    }

    public static YesOrNo parse(Character yn) {
        if (yn == null) {
            return NO;
        }

        if ('Y' == yn) {
            return YES;
        } else {
            return NO;
        }
    }
}
