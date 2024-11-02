package com.beaconstrategists.taccaseapiservice.controllers.dto;

import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RmaCaseDto {

    private Long id;

    private Long tacCaseId;

    private String href;

    private String caseNumber;

    private String requestType;

    private String newPartSerialNumber;

    private String faultySerialNumber;

    private String faultyPartNumber;

    private String returnedSerialNumber;

    private String returnedPartNumber;

    private CaseStatus caseStatus;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime caseCreatedDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime caseClosedDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime newPartShippedDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime newPartDeliveredDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime faultyPartShippedDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime faultyPartDeliveredDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime failureAnalysisStartDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime failureAnalysisInProgressDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime failureAnalysisFinishedDate;

    private String shipToStreet1;

    private String shipToProvince;

    private String shipToPostalCode;

    private String shipToPhone;

    private String shipToCountry;

    private String shipToCity;

    private String shipToContactEmail;

    private String shipToAttention;

    private String shippedDate;

    private String shippedCarrier;

    private String problemDescription;

    private String installationCountry;

    private String customerTrackingNumber;

    private String contactEmail;

    private Integer vendorRmaNumber;

    private List<Long> attachmentIds;
}
