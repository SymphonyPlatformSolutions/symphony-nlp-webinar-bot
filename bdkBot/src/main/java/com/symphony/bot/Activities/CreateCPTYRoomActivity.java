package com.symphony.bot.Activities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.form.FormReplyActivity;
import com.symphony.bdk.core.activity.form.FormReplyContext;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.gen.api.model.V3RoomAttributes;
import com.symphony.bdk.gen.api.model.V3RoomDetail;
import com.symphony.bdk.template.api.Template;
import com.symphony.bot.Repository.Trade;
import com.symphony.bot.models.Case;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.util.ArrayUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

@Component
public class CreateCPTYRoomActivity extends FormReplyActivity<FormReplyContext> {

    private final Template template;
    private final MessageService messageService;
    private final StreamService streamService;
    ObjectMapper objectMapper = new ObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(CreateCPTYRoomActivity.class);

    ArrayList<String> counterpartyTraders;
    HashMap<String, Case> tradeStates;

    public CreateCPTYRoomActivity(MessageService messageService, StreamService streamService) {
        this.template = messageService.templates().newTemplateFromClasspath("/templates/trade.ftl");
        this.messageService = messageService;
        this.streamService = streamService;
        this.tradeStates = new HashMap<>();
        this.counterpartyTraders = new ArrayList<>(Arrays.asList("347583113335425", "347583113335427", "347583113335428", "347583113335429", "347583113335430"));
    }

    @Override
    protected ActivityMatcher<FormReplyContext> matcher() {
        return context -> "trade-table".equals(context.getFormId());
    }

    @Override
    protected void onActivity(FormReplyContext formReplyContext) {
        ArrayList userArray = objectMapper.convertValue(formReplyContext.getFormValues().get("person-selector"), ArrayList.class);
        String tradeId = formReplyContext.getFormValue("action").
                substring(formReplyContext.getFormValue("action").lastIndexOf("-") + 1);

        Trade trade = new MongoActivity().getTrade(tradeId);
        String counterParty = trade.getCounterparty();
        userArray.addAll(counterpartyTraders);

        //create room and post trade details into the room
        V3RoomAttributes tradeRoom = new V3RoomAttributes();
        tradeRoom.name(String.format("Resolve Trade %s - %s", counterParty, trade.getDescription()));
        tradeRoom.setPublic(false);
        tradeRoom.crossPod(true);
        tradeRoom.discoverable(false);
        tradeRoom.description(String.format("%s - %s - %s", trade.getStatus(), trade.getDescription(), tradeId));
        V3RoomDetail createdRoom = streamService.create(tradeRoom);
        try {
            userArray.forEach(userId -> streamService.addMemberToRoom(Long.valueOf(userId.toString()), createdRoom.getRoomSystemInfo().getId()));
        }
        catch (Exception e){
            logger.debug("error adding cpty users to room");
        }

        HashMap<String, Trade> tradeData = new HashMap<>();
        tradeData.put("trades", trade);

        Message message = Message.builder().template(this.template, tradeData).build();
        messageService.send(createdRoom.getRoomSystemInfo().getId(), message);
    }

    @Override
    protected ActivityInfo info() {
        return new ActivityInfo().type(ActivityType.FORM)
                .name("Resolve Trades")
                .description("Form Handler for creating CPTY room");
    }
}

