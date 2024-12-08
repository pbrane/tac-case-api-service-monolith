package com.beaconstrategists.taccaseapiservice.config.api;

import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class CaseStatusConverter implements Converter<String, CaseStatus> {

    //fixme: perhaps this can be done better
    @Override
    public CaseStatus convert(@NonNull String source) {
        return CaseStatus.fromValue(source);
    }
}
