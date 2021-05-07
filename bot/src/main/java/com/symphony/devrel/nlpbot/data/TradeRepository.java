package com.symphony.devrel.nlpbot.data;

import com.symphony.devrel.nlpbot.model.Trade;
import com.symphony.devrel.nlpbot.model.TradeState;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface TradeRepository extends MongoRepository<Trade, String> {
    List<Trade> findAllByCounterparty(String counterparty);
    List<Trade> findAllByCounterpartyAndState(String counterparty, TradeState state);
}
