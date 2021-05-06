package com.symphony.devrel.nlpbot.model;

import lombok.Data;

@Data
public class Intent {
    private int confidence;
    private String name;
    private long id;
}
