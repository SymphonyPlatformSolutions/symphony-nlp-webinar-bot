package com.symphony.devrel.nlpbot.listener;

import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.spring.annotation.Slash;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClearHandler {
    private final MessageService messageService;

    @Slash(value = "/clear", mentionBot = false)
    public void onSlashClear(CommandContext context) {
        this.messageService.send(context.getStreamId(), "<br />".repeat(50));
    }
}
