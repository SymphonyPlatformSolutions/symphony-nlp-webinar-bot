package com.symphony.bot.Activities;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.symphony.bot.Repository.Trade;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Updates.set;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Component
public class MongoActivity {

    private static Logger logger = LoggerFactory.getLogger(FetchUnresolvedTradesActivity.class);
    public MongoCollection<Trade> tradeCollection;

    public MongoCollection<Trade> getCollection(){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://nlp-user:symphony@cluster0.y27fx.mongodb.net/test");
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoDatabase database = mongoClient.getDatabase("trades").withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Trade> tradeCollection = database.getCollection("unresolved", Trade.class);
        return tradeCollection;
    }

    public Trade updatePrice(String id, Double price) {
        logger.debug(price.toString());
        MongoCollection<Trade> tradeCollection = this.getCollection();
        tradeCollection.updateOne(eq("_id", new ObjectId(id)), set("price", price));
        Trade trade = tradeCollection.find(eq("_id", new ObjectId(id))).first();
        return trade;
    }

    public Trade confirmTrade(String id, String state) {
        MongoCollection<Trade> tradeCollection = this.getCollection();
        tradeCollection.updateOne(eq("_id", new ObjectId(id)), set("state", state));
        Trade trade = tradeCollection.find(eq("_id", new ObjectId(id))).first();
        return trade;
    }

     public Trade getTrade(String id) {
        MongoCollection<Trade> tradeCollection = this.getCollection();
         Trade trade = tradeCollection.find(eq("_id", new ObjectId(id))).first();
         logger.debug(trade.toString());
         return trade;
     }

     public HashMap<String, ArrayList> getTrades(String clientName, String state) {
         ArrayList<Trade> results = new ArrayList<>();
         HashMap<String, ArrayList> trades = new HashMap<>();
         MongoCollection<Trade> tradeCollection = this.getCollection();
         MongoCursor<Trade> cursor;
         if (state.equals("all")){
             cursor = tradeCollection.find(eq("counterparty", clientName)).iterator();
         }
         else {
             cursor = tradeCollection.find(and(eq("counterparty", clientName), eq("state", state))).iterator();
         }
         try {
             while (cursor.hasNext()) {
                 results.add(cursor.next());
             }
         } finally {
             cursor.close();
         }
         trades.put("trades", results);
         return trades;
     }

}

