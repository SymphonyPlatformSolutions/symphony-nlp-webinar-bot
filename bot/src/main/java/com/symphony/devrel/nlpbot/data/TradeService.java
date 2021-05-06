package com.symphony.devrel.nlpbot.data;

import com.symphony.devrel.nlpbot.model.Trade;
import com.symphony.devrel.nlpbot.model.TradeState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeService {
    private final TradeRepository tradeRepo;

    public List<Trade> getAllTrades() {
        return tradeRepo.findAll();
    }

    public List<Trade> getTrades(String clientName, TradeState state) {
        if (state == TradeState.ALL) {
            return tradeRepo.findAllByCounterparty(clientName);
        }
        return tradeRepo.findAllByCounterpartyAndState(clientName, state);
    }

    public Trade getTrade(String id) {
        return tradeRepo.findById(id).orElseThrow(IllegalAccessError::new);
    }

    public Trade updatePrice(String id, double price) {
        Trade trade = tradeRepo.findById(id).orElseThrow(IllegalAccessError::new);
        trade.setPrice(price);
        return tradeRepo.save(trade);
    }

    public Trade confirmTrade(String id, TradeState state) {
        Trade trade = tradeRepo.findById(id).orElseThrow(IllegalAccessError::new);
        trade.setState(state);
        return tradeRepo.save(trade);
    }
}
