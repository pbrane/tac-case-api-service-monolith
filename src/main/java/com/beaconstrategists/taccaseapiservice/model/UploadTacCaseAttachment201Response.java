package com.beaconstrategists.taccaseapiservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("uploadTacCaseAttachment_201_response")
public class UploadTacCaseAttachment201Response {

  @JsonProperty("attachmentId")
  private String attachmentId;

  @JsonProperty("href")
  private URI href;
}

