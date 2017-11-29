package com.procurement.orchestrator.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestDto<T> {

    @JsonProperty(value = "data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
}
