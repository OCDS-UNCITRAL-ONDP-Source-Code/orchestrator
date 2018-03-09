package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.response.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "e-notice")
public interface NoticeRestClient {

    @RequestMapping(path = "/release", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createRelease(@RequestParam("cpId") final String cpId,
                                              @RequestParam("ocId") final String ocId,
                                              @RequestParam("stage") final String stage,
                                              @RequestParam("operation") final String operation,
                                              @RequestParam("phase") final String phase,
                                              @RequestParam("releaseDate") final String releaseDate,
                                              @RequestBody final JsonNode data) throws Exception;


}
