package com.symphony.bot.Activities;

import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.form.FormReplyActivity;
import com.symphony.bdk.core.activity.form.FormReplyContext;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.template.api.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class FetchAllTradesActivity extends FormReplyActivity<FormReplyContext> {

    private static Logger logger = LoggerFactory.getLogger(FetchAllTradesActivity.class);
    private final MessageService messageService;
    private final Template template;

    public FetchAllTradesActivity(MessageService messageService) {
        this.messageService = messageService;
        this.template = messageService.templates().newTemplateFromClasspath("/templates/trade_table.ftl");
    }

    @Override
    protected ActivityMatcher<FormReplyContext> matcher() {
        return context -> "all".equals(context.getFormId())
                && "submit".equals(context.getFormValue("action"));
    }

    @Override
    protected void onActivity(FormReplyContext formReplyContext) {
        formReplyContext.getFormValues();
        messageService.send(formReplyContext.getSourceEvent().getStream().getStreamId(), "my message with the hashtag ");
        logger.debug("looking for all trades");
        String state = formReplyContext.getFormId();
        String counterParty = formReplyContext.getFormValue("counterparty");

        messageService.send(formReplyContext.getSourceEvent().getStream().getStreamId(), "Fetching Trades that match this criteria...");
        MongoActivity mongoActivity = new MongoActivity();
        HashMap tradeList = mongoActivity.getTrades(counterParty, state);

        Message message = Message.builder().template(this.template, tradeList).build();
        messageService.send(formReplyContext.getSourceEvent().getStream().getStreamId(), message);
    }

    @Override
    protected ActivityInfo info() {
        return new ActivityInfo().type(ActivityType.FORM)
                .name("Fetch All Trades")
                .description("Form Handler for All Trades");
    }
}

