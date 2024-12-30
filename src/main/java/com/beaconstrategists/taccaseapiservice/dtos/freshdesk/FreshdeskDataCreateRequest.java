package com.beaconstrategists.taccaseapiservice.dtos.freshdesk;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FreshdeskDataCreateRequest<T> {

    private T data;

    public FreshdeskDataCreateRequest(T data) {
        this.data = data;
    }
}
