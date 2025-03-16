package com.beaconstrategists.taccaseapiservice.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class InvalidPathParameterException extends RuntimeException {

    private final String errorCode;

    public InvalidPathParameterException(String message) {
        super(message);
        this.errorCode = "RESOURCE_NOT_FOUND"; // Default error code if not specified
    }

    public InvalidPathParameterException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}
