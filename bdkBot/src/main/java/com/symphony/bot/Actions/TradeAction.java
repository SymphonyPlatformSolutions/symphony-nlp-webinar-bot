package com.symphony.bot.Actions;

import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.spring.events.RealTimeEvent;

import java.util.HashMap;

public interface TradeAction {

    public void executeAction(HashMap<String,String> entities, RealTimeEvent<V4MessageSent> message);

}
