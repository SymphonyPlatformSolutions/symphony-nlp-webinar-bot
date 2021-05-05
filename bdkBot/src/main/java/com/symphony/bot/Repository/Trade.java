package com.symphony.bot.Repository;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@Data
public class Trade {

    private ObjectId id;

    @BsonProperty(value = "process")
    private String process;

    @BsonProperty(value = "trade_id")
    private String tradeId;

    @BsonProperty(value = "status")
    private String status;

    @BsonProperty(value = "state")
    private String state;

    @BsonProperty(value = "cusip")
    private String cusip;

    @BsonProperty(value = "description")
    private String description;

    @BsonProperty(value = "portfolio")
    private String portfolio;

    @BsonProperty(value = "price")
    private Double price;

    @BsonProperty(value = "quantity")
    private Double quantity;

    @BsonProperty(value = "transaction")
    private String transaction;

    @BsonProperty(value = "counterparty")
    private String counterparty;

}

