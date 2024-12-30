package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)  //fixme: check this what we want/need here
public class FreshdeskCaseResponseRecords<T> {

    private List<FreshdeskCaseResponse<T>> records;

}
