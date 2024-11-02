package com.beaconstrategists.taccaseapiservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Error {

  private String code;

  private String reason;

  private String message;

  private String status;

  private URI referenceError;

  private String atBaseType;

  private URI atSchemaLocation;

  private String atType;

}

