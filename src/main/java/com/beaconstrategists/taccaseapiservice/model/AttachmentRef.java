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
public class AttachmentRef {

  private String id;

  private String href;

  private String description;

  private String name;

  private String url;

  private String atBaseType;

  private URI atSchemaLocation;

  private String atType;

  private String atReferredType;

}

