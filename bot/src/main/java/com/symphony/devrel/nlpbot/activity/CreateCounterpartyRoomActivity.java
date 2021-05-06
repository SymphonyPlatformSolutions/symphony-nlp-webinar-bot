package com.symphony.devrel.nlpbot.activity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.form.FormReplyActivity;
import com.symphony.bdk.core.activity.form.FormReplyContext;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.gen.api.model.V3RoomAttributes;
import com.symphony.devrel.nlpbot.data.TradeService;
import com.symphony.devrel.nlpbot.model.Trade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateCounterpartyRoomActivity extends FormReplyActivity<FormReplyContext> {
    @Value("#{'${nlp-bot.traders}'.split(',')}")
    private List<Long> counterpartyTraders;
    private final MessageService messages;
    private final StreamService streamService;
    private final ObjectMapper objectMapper;
    private final TradeService tradeService;

    @Override
    protected ActivityMatcher<FormReplyContext> matcher() {
        return context -> "trade-table".equals(context.getFormId());
    }

    @Override
    protected void onActivity(FormReplyContext context) {
        JsonNode personSelectorValue = context.getFormValues().get("person-selector");
        List<Long> users = objectMapper.convertValue(personSelectorValue, new TypeReference<>() {});

        String action = context.getFormValue("action");
        if (action == null) {
            return;
        }
        String tradeId = action.substring(action.lastIndexOf("-") + 1);

        Trade trade = tradeService.getTrade(tradeId);
        String counterParty = trade.getCounterparty();
        users.addAll(counterpartyTraders);

        // Create room and post trade details into the room
        V3RoomAttributes tradeRoom = new V3RoomAttributes();
        tradeRoom.name(String.format("Resolve Trade %s - %s", counterParty, trade.getDescription()));
        tradeRoom.setPublic(false);
        tradeRoom.crossPod(true);
        tradeRoom.discoverable(false);
        tradeRoom.description(String.format("%s - %s - %s", trade.getStatus(), trade.getDescription(), tradeId));
        String newRoomStreamId = streamService.create(tradeRoom).getRoomSystemInfo().getId();

        users.forEach(user -> {
            try {
                streamService.addMemberToRoom(user, newRoomStreamId);
            } catch (Exception e) {
                log.error("Error adding user {} to room {}", user, newRoomStreamId);
            }
        });

        String message = messages.templates()
            .newTemplateFromClasspath("/templates/trade.ftl")
            .process(trade);
        messages.send(newRoomStreamId, message);
    }

    @Override
    protected ActivityInfo info() {
        return new ActivityInfo().type(ActivityType.FORM)
            .name("Resolve Trades")
            .description("Form Handler for creating counterparty room");
    }
}
