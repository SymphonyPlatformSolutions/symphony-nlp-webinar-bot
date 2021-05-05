package com.symphony.bot.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Intent {

    @JsonProperty("confidence")
    private int confidence;

    @JsonProperty("name")
    private String name;

    @JsonProperty("id")
    private Long id;

}

