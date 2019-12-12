package com.procurement.orchestrator.domain.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum SubmissionCommandType {

    CREATE_BID("createBid"),
    UPDATE_BID("updateBid"),
    UPDATE_BID_DOCS("updateBidDocs"),
    COPY_BIDS("copyBids"),
    GET_PERIOD("getPeriod"),
    SAVE_PERIOD("savePeriod"),
    SAVE_NEW_PERIOD("saveNewPeriod"),
    VALIDATE_PERIOD("validatePeriod"),
    CHECK_PERIOD_END_DATE("checkPeriodEndDate"),
    CHECK_PERIOD("checkPeriod"),
    CHECK_TOKEN_OWNER("checkTokenOwner"),
    GET_BIDS("getBids"),
    GET_BIDS_AUCTION("getBidsAuction"),
    UPDATE_BIDS_BY_LOTS("updateBidsByLots"),
    UPDATE_BID_BY_AWARD_STATUS("updateBidBAwardStatus"),
    SET_BIDS_FINAL_STATUSES("setBidsFinalStatuses"),
    BID_WITHDRAWN("bidWithdrawn"),
    BIDS_WITHDRAWN("bidsWithdrawn"),
    PREPARE_BIDS_CANCELLATION("prepareBidsCancellation"),
    BIDS_CANCELLATION("bidsCancellation"),
    GET_DOCS_OF_CONSIDERED_BID("getDocsOfConsideredBid"),
    SET_INITIAL_BIDS_STATUS("setInitialBidsStatus"),
    FINAL_BIDS_STATUS_BY_LOTS("finalBidsStatusByLots"),
    GET_BIDS_FOR_EVALUATION("getBidsForEvaluation"),
    OPEN_BIDS_FOR_PUBLISHING("openBidsForPublishing"),
    OPEN_BID_DOCS("openBidDocs"),
    GET_BIDS_BY_LOTS("getBidsByLots");

    private static final Map<String, SubmissionCommandType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final SubmissionCommandType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    SubmissionCommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static SubmissionCommandType fromValue(final String value) {
        final SubmissionCommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(SubmissionCommandType.class.getName(), value, Arrays.toString(values()));
        }
        return constant;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }
}
