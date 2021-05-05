package com.symphony.bot.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties("additional_info")
@Data
public class Entity {

    @JsonProperty("entity")
    private String entity;

    @JsonProperty("start")
    private int start;

    @JsonProperty("end")
    private int end;

    @JsonProperty("text")
    private String text;

    @JsonProperty("confidence")
    private String confidence;

    @JsonProperty("confidence_entity")
    private String confidenceEntity;

    @JsonProperty("value")
    private String value;

    @JsonProperty("extractor")
    private String extractor;

}

