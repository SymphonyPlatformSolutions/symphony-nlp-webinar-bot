package com.symphony.bot.Activities;

import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.*;
import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.form.FormReplyActivity;
import com.symphony.bdk.core.activity.form.FormReplyContext;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.template.api.Template;
import com.symphony.bot.Repository.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import java.util.ArrayList;
import java.util.HashMap;

import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Component
public class FetchUnresolvedTradesActivity extends FormReplyActivity<FormReplyContext> {

    private static Logger logger = LoggerFactory.getLogger(FetchUnresolvedTradesActivity.class);
    private final MessageService messageService;
    private final Template template;


    public FetchUnresolvedTradesActivity(MessageService messageService) {
        this.messageService = messageService;
        this.template = messageService.templates().newTemplateFromClasspath("/templates/trade_table.ftl");

    }


    @Override
    protected ActivityMatcher<FormReplyContext> matcher() {
        return context -> "unresolved".equals(context.getFormId())
                && "submit".equals(context.getFormValue("action"));
    }

    @Override
    protected void onActivity(FormReplyContext formReplyContext) {
        logger.debug("looking for unresolved trades");
        String state = formReplyContext.getFormId();
        String counterParty = formReplyContext.getFormValue("counterparty");

        messageService.send(formReplyContext.getSourceEvent().getStream().getStreamId(), "Fetching Trades that match this criteria...");
        MongoActivity mongoActivity = new MongoActivity();
        HashMap tradeList = mongoActivity.getTrades(counterParty, state);

        Message message = Message.builder().template(this.template, tradeList).build();
        logger.debug(message.getData());
        logger.debug(message.getContent());
        messageService.send(formReplyContext.getSourceEvent().getStream().getStreamId(), message);

    }

    @Override
    protected ActivityInfo info() {
        return new ActivityInfo().type(ActivityType.FORM)
                .name("Fetch Unresolved Trades")
                .description("Form Handler for TradeLookup");
    }
}

