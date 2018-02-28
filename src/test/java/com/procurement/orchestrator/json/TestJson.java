package com.procurement.orchestrator.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.procurement.orchestrator.cassandra.model.Params;
import com.procurement.orchestrator.kafka.dto.ChronographResponse;
import com.procurement.orchestrator.utils.DateUtil;
import com.procurement.orchestrator.utils.JsonUtil;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.junit.jupiter.api.Assertions.*;

public class TestJson {

    private final JsonUtil jsonUtil = new JsonUtil(new ObjectMapper());
    private final DateUtil dateUtil = new DateUtil();

    @Test
    public void getJsonNodeTest() {
        final String resource = jsonUtil.getResource("json/lots.json");
        final JsonNode lotsData = jsonUtil.toJsonNode(resource);
        JsonNode jsonData = jsonUtil.createObjectNode();
        ((ObjectNode) jsonData).replace("lots", lotsData.get("lots"));
        assertNotNull(jsonData.get("lots"));
    }

    @Test
    public void getChronographResponseTest() {
        final String resource = jsonUtil.getResource("json/cron.json");
        final ChronographResponse response = jsonUtil.toObject(ChronographResponse.class, resource);
        final Params params = jsonUtil.toObject(Params.class, response.getData().getMetaData());
        assertNotNull(params.getCpid());
    }


    @Test
    public void getInterval() {
        final int interval = 5;
        final LocalDateTime startDate = dateUtil.localDateTimeNowUTC();
        final LocalDateTime endDate = dateUtil.stringToLocal("2018-02-27T17:25:33Z");
        final long minutes = MINUTES.between(startDate, endDate);
        assertFalse((minutes >= interval));
    }
}