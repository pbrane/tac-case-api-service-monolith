package com.beaconstrategists.taccaseapiservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RmaCaseCreate {

  private String relatedTacCaseId;

  private String faultySerialNumber;

  private String requesterShippingFirstName;

  private String requesterShippingLastName;

  private String requesterShippingEmail;

  private String requesterShippingPhone;

  private String requesterShippingStreet;

  private String requesterShippingCity;

  private String requesterShippingStateProvince;

  private String requesterShippingCountry;

  private String requesterShippingPostalCode;

  private String shippingInboundId;

  private String shippingOutboundId;

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

