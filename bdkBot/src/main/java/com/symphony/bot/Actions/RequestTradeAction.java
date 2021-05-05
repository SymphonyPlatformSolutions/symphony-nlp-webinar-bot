package com.symphony.bot.Actions;

import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.spring.events.RealTimeEvent;
import com.symphony.bdk.template.api.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;

@Component
public class RequestTradeAction implements TradeAction {

    private static Logger logger = LoggerFactory.getLogger(TradeAction.class);
    private MessageService messageService;
    private final Template unresolvedTemplate;
    private final Template resolvedTemplate;
    private final Template allTradesTemplate;

    public RequestTradeAction(MessageService messageService) {
        this.messageService = messageService;
        this.unresolvedTemplate = messageService.templates().newTemplateFromClasspath("/templates/request_trade_unresolved.ftl");
        this.resolvedTemplate = messageService.templates().newTemplateFromClasspath("/templates/request_trade_resolved.ftl");
        this.allTradesTemplate = messageService.templates().newTemplateFromClasspath("/templates/all_trades.ftl");

    }

    @Override
    public void executeAction(HashMap<String, String> entities, RealTimeEvent<V4MessageSent> message){
        logger.debug("inside Request trade action -> execute action");
        logger.debug(entities.toString());
        logger.debug(entities.get("trade_state"));
        if (entities.get("trade_state").equals("unresolved")){
            final Message messageTemplate = Message.builder().template(unresolvedTemplate).build();
            messageService.send(message.getSource().getMessage().getStream().getStreamId(), messageTemplate);
        }
        else if (entities.get("trade_state").equals("resolved")){
            final Message messageTemplate = Message.builder().template(resolvedTemplate).build();
            messageService.send(message.getSource().getMessage().getStream().getStreamId(), messageTemplate);
        }

        else if (entities.get("trade_state").equals("all")) {
            final Message messageTemplate = Message.builder().template(allTradesTemplate).build();
            messageService.send(message.getSource().getMessage().getStream().getStreamId(), messageTemplate);
        }

        return;

    }

}

