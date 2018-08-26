package com.procurement.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "e-submission")
public interface SubmissionRestClient {

    @RequestMapping(path = "/period/check", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> checkPeriod(@RequestParam("cpid") String cpId,
                                            @RequestParam("stage") String stage,
                                            @RequestParam("country") String country,
                                            @RequestParam("pmd") String pmd,
                                            @RequestParam("operationType") String operationType,
                                            @RequestParam("startDate") String startDate,
                                            @RequestParam("endDate") String endDate) throws Exception;

    @RequestMapping(path = "/period/validation", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> periodValidation(@RequestParam("country") String country,
                                                 @RequestParam("pmd") String pmd,
                                                 @RequestParam("startDate") String startDate,
                                                 @RequestParam("endDate") String endDate) throws Exception;

    @RequestMapping(path = "/period/save", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> savePeriod(@RequestParam("cpid") String cpId,
                                           @RequestParam("stage") String stage,
                                           @RequestParam("startDate") String startDate,
                                           @RequestParam("endDate") String endDate) throws Exception;

    @RequestMapping(path = "/period/new", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> saveNewPeriod(@RequestParam("cpid") String cpId,
                                              @RequestParam("stage") String stage,
                                              @RequestParam("country") String country,
                                              @RequestParam("pmd") String pmd,
                                              @RequestParam("startDate") String startDate) throws Exception;

    @RequestMapping(path = "/period", method = RequestMethod.GET)
    ResponseEntity<ResponseDto> getPeriod(@RequestParam("cpid") String cpId,
                                          @RequestParam("stage") String stage) throws Exception;

    @RequestMapping(path = "/bid", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> createBid(@RequestParam("cpid") String cpId,
                                          @RequestParam("stage") String stage,
                                          @RequestParam("owner") String owner,
                                          @RequestParam("date") String startDate,
                                          @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/bid", method = RequestMethod.PUT)
    ResponseEntity<ResponseDto> updateBid(@RequestParam("cpid") String cpId,
                                          @RequestParam("stage") String stage,
                                          @RequestParam("token") String token,
                                          @RequestParam("owner") String owner,
                                          @RequestParam("bidId") String bidId,
                                          @RequestParam("date") String startDate,
                                          @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/bid/copy", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> copyBids(@RequestParam("cpid") String cpId,
                                         @RequestParam("stage") String newStage,
                                         @RequestParam("previousStage") String previousStage,
                                         @RequestParam("startDate") String startDate,
                                         @RequestParam("endDate") String endDate,
                                         @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/submission/bidsSelection", method = RequestMethod.GET)
    ResponseEntity<ResponseDto> bidsSelection(@RequestParam("cpid") String cpId,
                                              @RequestParam("stage") String stage,
                                              @RequestParam("country") String country,
                                              @RequestParam("pmd") String pmd,
                                              @RequestParam("date") String startDate) throws Exception;

    @RequestMapping(path = "/submission/updateStatus", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> updateStatus(@RequestParam("cpid") String cpId,
                                             @RequestParam("stage") String stage,
                                             @RequestParam("country") String country,
                                             @RequestParam("pmd") String pmd,
                                             @RequestBody JsonNode jsonData) throws Exception;

    @RequestMapping(path = "/submission/updateStatusDetails", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> updateStatusDetails(@RequestParam("cpid") String cpId,
                                                    @RequestParam("stage") String stage,
                                                    @RequestParam("bidId") String bidId,
                                                    @RequestParam("date") String startDate,
                                                    @RequestParam("awardStatusDetails") String awardStatusDetails)
            throws Exception;

    @RequestMapping(path = "/submission/setFinalStatuses", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> setFinalStatuses(@RequestParam("cpid") String cpId,
                                                 @RequestParam("stage") String stage,
                                                 @RequestParam("date") String startDate) throws Exception;

    @RequestMapping(path = "/submission/bidsWithdrawn", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> bidsWithdrawn(@RequestParam("cpid") String cpId,
                                              @RequestParam("stage") String stage,
                                              @RequestParam("date") String startDate) throws Exception;

    @RequestMapping(path = "/submission/bidWithdrawn", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> bidWithdrawn(@RequestParam("cpid") String cpId,
                                             @RequestParam("stage") String stage,
                                             @RequestParam("token") String token,
                                             @RequestParam("owner") String owner,
                                             @RequestParam("bidId") String bidId,
                                             @RequestParam("date") String startDate) throws Exception;

    @RequestMapping(path = "submission/prepareCancellation", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> prepareCancellation(@RequestParam("cpid") String cpId,
                                                    @RequestParam("stage") String stage,
                                                    @RequestParam("pmd") String pmd,
                                                    @RequestParam("phase") String phase,
                                                    @RequestParam("date") String startDate) throws Exception;

    @RequestMapping(path = "submission/bidsCancellation", method = RequestMethod.POST)
    ResponseEntity<ResponseDto> bidsCancellation(@RequestParam("cpid") String cpId,
                                                 @RequestParam("stage") String stage,
                                                 @RequestParam("pmd") String pmd,
                                                 @RequestParam("phase") String phase,
                                                 @RequestParam("date") String startDate) throws Exception;


}