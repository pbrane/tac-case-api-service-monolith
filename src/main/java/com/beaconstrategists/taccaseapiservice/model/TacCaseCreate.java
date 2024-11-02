package com.beaconstrategists.taccaseapiservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("TacCase_Create") //TODO: Test this
public class TacCaseCreate {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime requestDate;

  private String requesterOrganization;

  private String requesterFirstName;

  private String requesterLastName;

  //TODO: fix
  private JsonNullable<Object> requesterEmail = JsonNullable.<Object>undefined();

  //TODO: fix
  private JsonNullable<Object> requesterPhone = JsonNullable.<Object>undefined();

  private String installationCountry;

  /**
   * Gets or Sets networkStatus
   */
  public enum NetworkStatusEnum {
    PRODUCTION("Production"),
    
    TEST("Test"),
    
    DEVELOPMENT("Development");

    private String value;

    NetworkStatusEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static NetworkStatusEnum fromValue(String value) {
      for (NetworkStatusEnum b : NetworkStatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private NetworkStatusEnum networkStatus;

  private String businessImpact;

  private String subject;

  private String problemDescription;

  private String productName;

  private String productSerialNumber;

  private String productFirmwareVersion;

  private String productSoftwareVersion;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime outageStartTime;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime outageRecoveryTime;

  private String customerTrackingNumber;
}

