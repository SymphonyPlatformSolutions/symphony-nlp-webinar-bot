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
import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FetchTradesActivity extends FormReplyActivity<FormReplyContext> {
    private final MessageService messages;
    private final TradeService tradeService;
    private final List<String> tradeStates = Arrays.stream(TradeState.values())
        .map(TradeState::name).collect(Collectors.toList());

    @Override
    protected ActivityMatcher<FormReplyContext> matcher() {
        return context -> tradeStates.contains(context.getFormId())
            && "submit".equals(context.getFormValue("action"));
    }

    @Override
    protected void onActivity(FormReplyContext context) {
        String state = context.getFormId();
        log.debug("looking for {} trades", state);

        String counterparty = context.getFormValue("counterparty");
        String streamId = context.getSourceEvent().getStream().getStreamId();

        messages.send(streamId, "Fetching Trades that match this criteria...");
        List<Trade> trades = tradeService.getTrades(counterparty, TradeState.valueOf(state));

        if (trades.isEmpty()) {
            messages.send(streamId, "No " + state + " trades for this counterparty");
            return;
        }

        String message = messages.templates()
            .newTemplateFromClasspath("/templates/trade_table.ftl")
            .process(Map.of("trades", trades));
        messages.send(streamId, message);
    }

    @Override
    protected ActivityInfo info() {
        return new ActivityInfo().type(ActivityType.FORM)
            .name("Fetch Trades")
            .description("Form Handler for fetching trades");
    }
}
