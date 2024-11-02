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
public class AttachmentRefOrValue {

  private String id;

  private String href;

  private String attachmentType;

  private String content;

  private String description;

  private String mimeType;

  private String name;

  private String url;

  private Float size;

  private String atBaseType;

  private URI atSchemaLocation;

  private String atType;

  private String atReferredType;

}

