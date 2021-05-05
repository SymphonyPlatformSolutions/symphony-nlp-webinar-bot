package com.symphony.bot.Activities;

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

import java.util.HashMap;

@Component
public class CounterPartyActivity extends FormReplyActivity<FormReplyContext> {

    private static Logger logger = LoggerFactory.getLogger(CounterPartyActivity.class);
    private final Template template;
    private final Template confirmedTemplate;
    private final MessageService messageService;
    private MongoActivity mongoActivity = null;

    public CounterPartyActivity(MessageService messageService) {
        this.mongoActivity = new MongoActivity();
        this.messageService = messageService;
        this.template = messageService.templates().newTemplateFromClasspath("/templates/trade.ftl");
        this.confirmedTemplate = messageService.templates().newTemplateFromClasspath("/templates/confirmed_trade.ftl");

    }

    @Override
    protected ActivityMatcher<FormReplyContext> matcher() {
        return context -> "trade-row".equals(context.getFormId());
    }

    @Override
    protected void onActivity(FormReplyContext formReplyContext) {
        logger.debug(formReplyContext.getFormValues().toString());
        //find the updated value
        String updatedPrice = formReplyContext.getFormValue("price");
        String tradeId = formReplyContext.getFormValue("action").
                substring(formReplyContext.getFormValue("action").lastIndexOf("-") + 1);

        if (!updatedPrice.isEmpty()){
            logger.debug("price is being updated");
            Trade trade = new MongoActivity().updatePrice(tradeId, Double.parseDouble(updatedPrice));
            HashMap<String, Trade> tradeData = new HashMap<>();
            tradeData.put("trades", trade);
            Message message = Message.builder().template(this.template, tradeData).build();
            messageService.send(formReplyContext.getSourceEvent().getStream().getStreamId(), String.format("%s has proposed the following changes:", formReplyContext.getInitiator().getUser().getDisplayName()));
            messageService.send(formReplyContext.getSourceEvent().getStream().getStreamId(), message);
        }
        else if (formReplyContext.getFormValue("action").startsWith("confirm")){
            logger.debug("trade has been confirmed");
            Trade trade = new MongoActivity().confirmTrade(tradeId, "RESOLVED");
            HashMap<String, Trade> tradeData = new HashMap<>();
            tradeData.put("trades", trade);
            Message message = Message.builder().template(this.confirmedTemplate, tradeData).build();
            messageService.send(formReplyContext.getSourceEvent().getStream().getStreamId(), String.format("%s has confirmed the following trade:", formReplyContext.getInitiator().getUser().getDisplayName()));
            messageService.send(formReplyContext.getSourceEvent().getStream().getStreamId(), message);
        }

    }

    @Override
    protected ActivityInfo info() {
        return new ActivityInfo().type(ActivityType.FORM)
                .name("Counterparty Room")
                .description("Form Handler for Counterparty room");
    }
}

