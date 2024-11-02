package com.beaconstrategists.taccaseapiservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimePeriod {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @Valid
  @JsonProperty("endDateTime")
  private OffsetDateTime endDateTime;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @Valid
  @JsonProperty("startDateTime")
  private OffsetDateTime startDateTime;
}

