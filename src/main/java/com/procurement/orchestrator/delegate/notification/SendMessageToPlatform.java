package com.procurement.orchestrator.delegate.notification;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.orchestrator.delegate.kafka.MessageProducer;
import com.procurement.orchestrator.domain.Context;
import com.procurement.orchestrator.domain.Notification;
import com.procurement.orchestrator.domain.entity.OperationStepEntity;
import com.procurement.orchestrator.service.NotificationService;
import com.procurement.orchestrator.service.OperationService;
import com.procurement.orchestrator.utils.JsonUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SendMessageToPlatform implements JavaDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(SendMessageToPlatform.class);

    private final NotificationService notificationService;
    private final OperationService operationService;
    private final MessageProducer messageProducer;
    private final JsonUtil jsonUtil;

    public SendMessageToPlatform(final NotificationService notificationService,
                                 final OperationService operationService,
                                 final MessageProducer messageProducer,
                                 final JsonUtil jsonUtil) {
        this.notificationService = notificationService;
        this.operationService = operationService;
        this.messageProducer = messageProducer;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void execute(final DelegateExecution execution) {
        LOG.info(execution.getCurrentActivityId());
        final OperationStepEntity entity = operationService.getPreviousOperationStep(execution);
        final Context context = jsonUtil.toObject(Context.class, entity.getContext());
        final Notification notification = notificationService.getNotificationForPlatform(context);
        if (notification != null) {
            messageProducer.sendToPlatform(notification);

            if (LOG.isDebugEnabled())
                LOG.debug("CONTEXT FOR SAVE (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(context) + "'.");

            final JsonNode step = jsonUtil.toJsonNode(notification);
            if (LOG.isDebugEnabled())
                LOG.debug("STEP FOR SAVE (" + context.getOperationId() + "): '" + jsonUtil.toJsonOrEmpty(step) + "'.");

            operationService.saveOperationStep(execution, entity, context, step);
        }
    }
}
