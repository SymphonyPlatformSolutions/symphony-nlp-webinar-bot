package com.symphony.devrel.nlpbot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Entity {
    private String entity;
    private int start;
    private int end;
    private String text;
    private String confidence;
    private String confidenceEntity;
    private String value;
    private String extractor;
}
