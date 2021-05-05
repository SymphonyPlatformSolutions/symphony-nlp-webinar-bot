package com.symphony.bot.models;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Case {

    public String tradeId;
    public String streamId;
    public String tradeState;
    public ArrayList users;

    public Case(String tradeId, String streamId, String tradeState, ArrayList users) {
        this.tradeId = tradeId;
        this.streamId = streamId;
        this.tradeState = tradeState;
        this.users = users;

    }

}

