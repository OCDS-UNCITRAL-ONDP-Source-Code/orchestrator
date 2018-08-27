package com.procurement.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.dto.CommandMessage;
import com.procurement.orchestrator.domain.dto.CommandType;
import com.procurement.orchestrator.domain.dto.ResponseDto;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ProcessService {

    void startProcess(Context context, Map<String, Object> variables);

    void terminateProcess(String processId, String message);

    CommandMessage getCommandMessage(CommandType commandType, Context context, JsonNode data);

    JsonNode processResponse(ResponseEntity<ResponseDto> responseEntity,
                             Context context,
                             String processId,
                             String taskId,
                             JsonNode request);

    String getText(String fieldName, JsonNode jsonData, String processId);

    Boolean getBoolean(String fieldName, JsonNode jsonData, String processId);

    String getTenderPeriodEndDate(JsonNode jsonData, String processId);

    JsonNode addTenderTenderPeriod(JsonNode jsonData, JsonNode periodData, String processId);

    JsonNode addTenderTenderPeriodStartDate(JsonNode jsonData, String startDate, String processId);

    JsonNode addTenderEnquiryPeriod(JsonNode jsonData, JsonNode periodData, String processId);

    JsonNode addTenderStatus(JsonNode jsonData, JsonNode statusData, String processId);

    JsonNode addLots(JsonNode jsonData, JsonNode lotsData, String processId);

    JsonNode addLotsAndAwardCriteria(JsonNode jsonData, JsonNode lotsData, String processId);

    JsonNode addLotsAndItems(JsonNode jsonData, JsonNode data, String processId);

    JsonNode addAwardData(JsonNode jsonData, JsonNode awardData, String processId);

    JsonNode addAwards(JsonNode jsonData, JsonNode awardsData, String processId);

    JsonNode addCans(JsonNode jsonData, JsonNode cansData, String processId);

    JsonNode addContracts(JsonNode jsonData, JsonNode data, String processId);

    JsonNode getUnsuccessfulLots(JsonNode jsonData, String processId);

    JsonNode getTenderLots(JsonNode jsonData, String processId);

    JsonNode addUpdateBidsStatusData(JsonNode jsonData, JsonNode bidsData, String processId);

    JsonNode addBidsAndTenderPeriod(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode addBids(JsonNode jsonData, JsonNode responseData, String processId);

    Boolean isBidsEmpty(JsonNode responseData, String processId);

    JsonNode getNextAward(JsonNode jsonData, String processId);

    JsonNode addUpdatedBid(JsonNode jsonData, JsonNode bidData, String processId);

    JsonNode addUpdatedLot(JsonNode jsonData, JsonNode lotData, String processId);

    JsonNode getDocumentsOfTender(JsonNode jsonData, String processId);

    JsonNode setDocumentsOfTender(JsonNode jsonData, JsonNode documentsData, String processId);

    JsonNode getDocumentsOfAwards(JsonNode jsonData, String processId);

    JsonNode setDocumentsOfAward(JsonNode jsonData, JsonNode documentsData, String processId);

    JsonNode getDocumentsOfBids(JsonNode jsonData, String processId);

    JsonNode setDocumentsOfBids(JsonNode jsonData, JsonNode documentsData, String processId);

    JsonNode addStandstillPeriod(JsonNode jsonData, String startDate, String endDate, String processId);

    JsonNode setAccessData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getAccessData(JsonNode jsonData, String processId);

    JsonNode setTender(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getEiData(JsonNode jsonData, String processId);

    JsonNode setEiData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getFsData(JsonNode jsonData, String processId);

    JsonNode setFsData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getTenderData(Boolean itemsAdd, JsonNode jsonData, String processId);

    JsonNode setTenderData(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getCheckItems(JsonNode jsonData, String processId);

    JsonNode setCheckItems(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getCheckFs(JsonNode jsonData, String startDate, String processId);

    JsonNode setCheckFs(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getBidTenderersData(JsonNode jsonData, String processId);

    JsonNode setBidTenderersData(JsonNode jsonData, JsonNode responseData, String processId);

    String getEnquiryId(JsonNode jsonData, String processId);

    JsonNode getEnquiryAuthor(JsonNode jsonData, String processId);

    JsonNode setEnquiryAuthor(JsonNode jsonData, JsonNode responseData, String processId);

    JsonNode getEnquiryRelatedLot(JsonNode jsonData, String processId);


}

