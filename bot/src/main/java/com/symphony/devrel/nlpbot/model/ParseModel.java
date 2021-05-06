package com.symphony.devrel.nlpbot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParseModel {
    private List<Entity> entities;
    private Intent intent;
    @JsonProperty("intent_ranking")
    private List<Intent> intentRanking;
    private String text;
}
