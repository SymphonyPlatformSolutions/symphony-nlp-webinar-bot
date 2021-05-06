package com.symphony.devrel.nlpbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class Trade {
    @Id
    private String id;
    private String process;
    @JsonProperty("trade_id")
    private String tradeId;
    private String status;
    private TradeState state;
    private String cusip;
    private String description;
    private String portfolio;
    private double price;
    private double quantity;
    private String transaction;
    private String counterparty;
}
