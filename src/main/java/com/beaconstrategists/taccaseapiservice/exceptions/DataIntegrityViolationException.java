package com.beaconstrategists.taccaseapiservice.exceptions;

public class DataIntegrityViolationException extends Throwable {
    public DataIntegrityViolationException(String message) {
        super(message);
    }
}
