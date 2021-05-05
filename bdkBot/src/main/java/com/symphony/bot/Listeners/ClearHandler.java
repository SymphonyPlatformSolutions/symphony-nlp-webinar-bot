package com.symphony.bot.Listeners;

import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.core.service.connection.ConnectionService;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.spring.annotation.Slash;
import com.symphony.bdk.template.api.Template;
import org.springframework.stereotype.Component;

@Component
public class ClearHandler {

    private final MessageService messageService;
    private final Template template;
    private final ConnectionService connectionService;

    public ClearHandler(MessageService messageService, ConnectionService connectionService) {
        this.messageService = messageService;
        this.connectionService = connectionService;
        this.template = messageService.templates().newTemplateFromClasspath("/templates/clear.ftl");
    }

    @Slash(value = "/clear", mentionBot = false)
    public void onSlashClear(CommandContext context) {
        this.messageService.send(context.getStreamId(), Message.builder().template(this.template).build());

    }

}

