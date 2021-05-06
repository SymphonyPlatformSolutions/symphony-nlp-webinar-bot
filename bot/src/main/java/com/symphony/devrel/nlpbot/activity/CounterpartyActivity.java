package com.symphony.devrel.nlpbot.activity;

import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.form.FormReplyActivity;
import com.symphony.bdk.core.activity.form.FormReplyContext;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.devrel.nlpbot.data.TradeService;
import com.symphony.devrel.nlpbot.model.Trade;
import com.symphony.devrel.nlpbot.model.TradeState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CounterpartyActivity extends FormReplyActivity<FormReplyContext> {
    private final MessageService messages;
    private final TradeService tradeService;

    @Override
    protected ActivityMatcher<FormReplyContext> matcher() {
        return context -> "trade-row".equals(context.getFormId());
    }

    @Override
    protected void onActivity(FormReplyContext context) {
        log.debug(context.getFormValues().toString());
        String streamId = context.getSourceEvent().getStream().getStreamId();

        // Find the updated value
        String updatedPrice = context.getFormValue("price");
        String action = context.getFormValue("action");
        if (updatedPrice == null || action == null) {
            return;
        }

        String tradeId = action.substring(action.lastIndexOf("-") + 1);
        String displayName = context.getInitiator().getUser().getDisplayName();

        if (!updatedPrice.isEmpty()) {
            log.debug("price is being updated");

            Trade trade = tradeService.updatePrice(tradeId, Double.parseDouble(updatedPrice));
            String message = messages.templates()
                .newTemplateFromClasspath("/templates/trade.ftl")
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
