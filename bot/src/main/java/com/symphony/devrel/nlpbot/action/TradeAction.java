package com.symphony.devrel.nlpbot.action;

import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.spring.events.RealTimeEvent;
import java.util.Map;

public interface TradeAction {
    void executeAction(Map<String,String> entities, RealTimeEvent<V4MessageSent> message);
}
