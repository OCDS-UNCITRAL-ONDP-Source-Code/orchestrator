package com.procurement.orchestrator.kafka.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PlatformMessage {

    @JsonProperty(value = "success")
    private Boolean success;
    @JsonProperty("operationId")
    private String operationId;
    @JsonProperty("token")
    private String token;
    @JsonProperty("cpid")
    private String cpid;
    @JsonProperty("ocid")
    private String ocid;
    @JsonProperty("message")
    private String message;

    @JsonCreator
    public PlatformMessage(@JsonProperty("success") final Boolean success,
                           @JsonProperty("operationId") final String operationId,
                           @JsonProperty("token") final String token,
                           @JsonProperty("cpid") final String cpid,
                           @JsonProperty("ocid") final String ocid,
                           @JsonProperty("message") final String message) {
        this.success = success;
        this.operationId = operationId;
        this.token = token;
        this.cpid = cpid;
        this.ocid = ocid;
        this.message = message;
    }

    @JsonCreator
    public PlatformMessage(@JsonProperty("success") final Boolean success,
                           @JsonProperty("operationId") final String operationId,
                           @JsonProperty("message") final String message) {
        this.success = success;
        this.operationId = operationId;
        this.message = message;
    }

}
