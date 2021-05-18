package com.symphony.devrel.nlpbot.activity;

import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.form.FormReplyActivity;
import com.symphony.bdk.core.activity.form.FormReplyContext;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.service.datafeed.util.RealTimeEventsBinder;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.gen.api.model.V4Event;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;
import com.symphony.devrel.nlpbot.data.TradeService;
import com.symphony.devrel.nlpbot.model.Trade;
import com.symphony.devrel.nlpbot.model.TradeState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class CounterpartyActivity extends FormReplyActivity<FormReplyContext> {
    private final MessageService messages;
    private final TradeService tradeService;
    private RealTimeEventListener listener;
    private RealTimeEventsBinder binder;

    @Override
    protected void bindToRealTimeEventsSource(Consumer<RealTimeEventListener> realTimeEventsSource) {
        if (listener == null) {
            listener = new RealTimeEventListener() {
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

                @Override
                public void onSymphonyElementsAction(V4Initiator initiator, V4SymphonyElementsAction event) {
                    processEvent(initiator, event);
                }
                        };
                    }

        binder.bindRealTimeListener(realTimeEventsSource, listener);
    }

    @Override
    protected ActivityMatcher<FormReplyContext> matcher() {
        log.debug("matcher is being called");
        return context -> "trade-row".equals(context.getFormId());
    }

    @Override
    protected void onActivity(FormReplyContext context) {
        log.debug(context.getFormValues().toString());
        String streamId = context.getSourceEvent().getStream().getStreamId();

        // Find the updated value
        String updatedPrice = context.getFormValue("price");
        String action = context.getFormValue("action");

        String tradeId = action.substring(action.lastIndexOf("-") + 1);
        String displayName = context.getInitiator().getUser().getDisplayName();

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

    @Override
    protected ActivityInfo info() {
        return new ActivityInfo().type(ActivityType.FORM)
            .name("Counterparty Room")
            .description("Form Handler for Counterparty room");
    }
}
