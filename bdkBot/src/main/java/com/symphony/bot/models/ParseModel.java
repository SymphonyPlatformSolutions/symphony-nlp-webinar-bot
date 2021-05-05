package com.symphony.bot.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;



@Data
@JsonIgnoreProperties("response_selector")
public class ParseModel {

    @JsonProperty("entities")
    private ArrayList<Entity> entities;

    @JsonProperty("intent")
    private Intent intent;

    @JsonProperty("intent_ranking")
    private ArrayList<Intent> intentRanking;

    @JsonProperty("text")
    private String text;

}

