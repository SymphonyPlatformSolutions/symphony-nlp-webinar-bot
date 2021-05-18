package com.symphony.devrel.nlpbot.action;

import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.spring.events.RealTimeEvent;
import com.symphony.bdk.template.api.Template;
import com.symphony.devrel.nlpbot.model.TradeState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RequestTradeAction implements TradeAction {
    private final MessageService messages;

    @Override
    public void executeAction(Map<String, String> entities, RealTimeEvent<V4MessageSent> message) {
        String streamId = message.getSource().getMessage().getStream().getStreamId();
        if (TradeState.valueOf(entities.get("trade_state")) != null ){
            TradeState tradeState = TradeState.valueOf(entities.get("trade_state"));
        }
        else{
            TradeState tradeState = TradeState.ALL;
        }
        TradeState tradeState = TradeState.valueOf(entities.get("trade_state"));

        switch (tradeState) {
            case UNRESOLVED:
            case RESOLVED: {
                Map<String, String> data = Map.of("state", tradeState.toString());
                messages.send(streamId, loadMessageTemplate("request_trade", data));
                break;
            }
            case ALL: {
                messages.send(streamId, loadMessageTemplate("all_trades", Map.of()));
                break;
            }
        }
    }

    private Message loadMessageTemplate(String templateName, Map<String, String> data) {
        Template template = messages.templates()
            .newTemplateFromClasspath("/templates/" + templateName + ".ftl");
        return Message.builder().template(template, data).build();
    }
}
