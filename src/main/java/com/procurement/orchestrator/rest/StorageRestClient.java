package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.config.FeignConfig;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "e-storage", configuration = FeignConfig.class)
public interface StorageRestClient {

    @RequestMapping(path = "/publish", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> setPublishDate(@RequestParam(value = "fileId") final String fileId,
                                               @RequestParam(value = "datePublished") final String datePublished
    ) throws Exception;

    @RequestMapping(method = RequestMethod.POST, value = "/publishBatch")
    ResponseEntity<ResponseDto> setPublishDateBatch(@RequestParam(value = "datePublished") final String datePublished,
                                                    @RequestBody final JsonNode dataDto) throws Exception;
}
