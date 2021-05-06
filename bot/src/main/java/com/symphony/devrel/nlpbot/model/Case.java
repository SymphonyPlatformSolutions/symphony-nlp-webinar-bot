package com.symphony.devrel.nlpbot.model;

import lombok.Data;
import java.util.List;

@Data
public class Case {
    private String tradeId;
    private String streamId;
    private String tradeState;
    private List<Long> users;
}
