package com.symphony.devrel.nlpbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RasaParseRequest {
    private String text;
    @JsonProperty("message_id")
    private String messageId;
}
