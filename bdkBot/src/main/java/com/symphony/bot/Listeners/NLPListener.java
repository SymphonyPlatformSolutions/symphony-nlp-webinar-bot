package com.symphony.bot.Listeners;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.util.PresentationMLParser;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.spring.events.RealTimeEvent;
import com.symphony.bot.Actions.RequestTradeAction;
import com.symphony.bot.models.ParseModel;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class NLPListener {

    private static Logger logger = LoggerFactory.getLogger(NLPListener.class);
    private RequestTradeAction requestTradeAction;

    @Value("${rasa-action-server}")
    private String rasaActionServer;
    @Value("${bot-id}")
    private String botId;
    @Value("${rasa-secret}")
    private String rasaSecret;

    private MessageService messageService;
    ObjectMapper objectMapper = new ObjectMapper();
    private String jwtToken;

    public NLPListener(MessageService messageService) {
        this.messageService = messageService;
        this.requestTradeAction = new RequestTradeAction(messageService);

    }

    public String authenticate(String userId) throws IOException {

        Map<String, String> user = new HashMap<>();
        user.put("username", userId);
        user.put("role", "admin");

        Map<String, Object> authPayload = new HashMap<>();
        authPayload.put("user", user);

        Algorithm algorithm = Algorithm.HMAC256(rasaSecret);
        this.jwtToken = JWT.create()
                .withPayload(authPayload)
                .sign(algorithm);

        return this.jwtToken;
    }

    @EventListener
    public void onMessageSent(RealTimeEvent<V4MessageSent> event) throws IOException {

        if (this.jwtToken == null) {
            logger.debug("authenticating");
            this.jwtToken = authenticate(botId.toString());
        }
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(rasaActionServer);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Authorization", String.format("Bearer %s", jwtToken));

            HashMap values = new HashMap<String,String>() {{
                put("text", PresentationMLParser.getTextContent(event.getSource().getMessage().getMessage()));
                put("message_id", event.getSource().getMessage().getMessageId());
            }};

            String requestBody = objectMapper.writeValueAsString(values);
            StringEntity entity = new StringEntity(requestBody);
            httpPost.setEntity(entity);

            CloseableHttpResponse response = httpclient.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            ParseModel rasaResponse = objectMapper.readValue(responseBody, ParseModel.class);
            extractEntities(rasaResponse, event);
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void extractEntities(ParseModel response, RealTimeEvent<V4MessageSent> message){
        String intent = response.getIntent().getName();
        logger.debug("-------------------------------------------------------------------------------------------");
        logger.debug("intent : " + intent);
        logger.debug("entities : " + response.getEntities().toString());
        logger.debug("-------------------------------------------------------------------------------------------");
        HashMap<String, String> entities = new HashMap<String, String>();
        if (response.getEntities().isEmpty()){
            entities.put("trade_state", "all");
        }
        else {
            entities.put(response.getEntities().get(0).getEntity(), response.getEntities().get(0).getValue());
        }
        routeAction(intent, entities, message);
    }

    public void routeAction(String intent, HashMap<String,String> entities, RealTimeEvent<V4MessageSent> message){
        if (intent.equals("request_trade_status")) {
            this.requestTradeAction.executeAction(entities, message);
        }
        else {
            return;
        }
    }
}

