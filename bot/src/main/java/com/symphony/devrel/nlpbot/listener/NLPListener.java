package com.symphony.devrel.nlpbot.listener;

import com.symphony.bdk.core.service.message.exception.PresentationMLParserException;
import com.symphony.bdk.core.service.message.util.PresentationMLParser;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;
import com.symphony.bdk.spring.events.RealTimeEvent;
import com.symphony.devrel.nlpbot.action.RequestTradeAction;
import com.symphony.devrel.nlpbot.client.RasaClient;
import com.symphony.devrel.nlpbot.model.Entity;
import com.symphony.devrel.nlpbot.model.ParseModel;
import com.symphony.devrel.nlpbot.model.RasaParseRequest;
import com.symphony.devrel.nlpbot.model.TradeState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NLPListener {
    private final RasaClient rasaClient;
    private final RequestTradeAction requestTradeAction;
    private static Logger logger = LoggerFactory.getLogger(NLPListener.class);


    @EventListener
    public void onMessageSent(RealTimeEvent<V4MessageSent> event) {
        V4Message message = event.getSource().getMessage();
        String messageText = null;
        try {
            messageText = PresentationMLParser.getTextContent(message.getMessage());
        } catch (PresentationMLParserException ignored) {}

        RasaParseRequest request = RasaParseRequest.builder()
            .text(messageText)
            .messageId(message.getMessageId())
            .build();
        ParseModel rasaResponse = rasaClient.parse(request);
        logger.debug(rasaResponse.toString());
        extractEntities(rasaResponse, event);
    }

    @EventListener
    public void onElementsAction(RealTimeEvent<V4SymphonyElementsAction> event){
        log.debug(event.toString());
    }

    public void extractEntities(ParseModel response, RealTimeEvent<V4MessageSent> message) {
        String intent = response.getIntent().getName();
        log.debug("-------------------------------------------------------------------------------------------");
        log.debug("intent : " + intent);
        log.debug("entities : " + response.getEntities().toString());
        log.debug("-------------------------------------------------------------------------------------------");
        if (!response.getEntities().isEmpty()){
            response.getEntities().forEach(entity -> log.debug(entity.getEntity() + " : " + entity.getValue()));
        }


        Map<String, String> entities = new HashMap<>();
        if (response.getEntities().isEmpty()) {
            entities.put("trade_state", TradeState.ALL.name());
        } else {
            Entity entity = response.getEntities().get(0);
            entities.put(entity.getEntity(), entity.getValue().toUpperCase());
        }

        routeAction(intent, entities, message);
    }

    public void routeAction(String intent, Map<String,String> entities, RealTimeEvent<V4MessageSent> message) {
        if (intent.equals("request_trade_status")) {
            this.requestTradeAction.executeAction(entities, message);
        }
    }
}
