package com.symphony.devrel.nlpbot.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.gen.api.model.V4Event;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;
import com.symphony.bdk.spring.events.RealTimeEvent;
import com.symphony.devrel.nlpbot.data.TradeService;
import com.symphony.devrel.nlpbot.model.Trade;
import com.symphony.devrel.nlpbot.model.TradeState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j

@RequiredArgsConstructor
public class CounterPartyListener implements RealTimeEventListener {
    private final MessageService messages;
    private final TradeService tradeService;

    private static final ObjectMapper MAPPER = new JsonMapper();

    @Override
    public boolean isAcceptingEvent(V4Event event, String username) {
        if (event.getInitiator().getUser().getUsername() == null){
            return true;
        }
        else {
            return event.getInitiator() != null &&
                    event.getInitiator().getUser() != null &&
                    !event.getInitiator().getUser().getUsername().equals(username);
        }
    }

    public String getFormValue(JsonNode formValues, String fieldName) {
        return formValues.has(fieldName) ? formValues.get(fieldName).asText() : null;
    }

    @Override
    public void onSymphonyElementsAction(V4Initiator initiator, V4SymphonyElementsAction event) {
        if (event.getFormId().equals("trade-row")){
            JsonNode formValues = MAPPER.valueToTree(event.getFormValues());
            log.debug(event.getFormValues().toString());
            String streamId = event.getStream().getStreamId();

            // Find the updated value
            String updatedPrice = getFormValue(formValues, "price");
            String action = getFormValue(formValues, "action");


            String tradeId = action.substring(action.lastIndexOf("-") + 1);
            String displayName = initiator.getUser().getDisplayName();

            if (!updatedPrice.isEmpty()) {
                log.debug("price is being updated");

                Trade trade = tradeService.updatePrice(tradeId, Double.parseDouble(updatedPrice));
                String message = messages.templates()
                        .newTemplateFromClasspath("/templates/updated_trade.ftl")
                        .process(trade);
                messages.send(streamId, displayName + " has proposed the following changes:");
                messages.send(streamId, message);
            } else if (action.startsWith("confirm")) {
                log.debug("trade has been confirmed");

                Trade trade = tradeService.confirmTrade(tradeId, TradeState.RESOLVED);
                String message = messages.templates()
                        .newTemplateFromClasspath("/templates/confirmed_trade.ftl")
                        .process(trade);
                messages.send(streamId, displayName + " has confirmed the following trade:");
                messages.send(streamId, message);
            }
        }

    }

}

