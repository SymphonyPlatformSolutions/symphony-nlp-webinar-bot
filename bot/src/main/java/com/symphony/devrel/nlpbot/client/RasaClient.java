package com.symphony.devrel.nlpbot.client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.symphony.bdk.core.service.session.SessionService;
import com.symphony.devrel.nlpbot.model.ParseModel;
import com.symphony.devrel.nlpbot.model.RasaParseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RasaClient {
    @Value("${nlp-bot.rasa-server}")
    private String rasaServer;
    @Value("${nlp-bot.rasa-secret}")
    private String rasaSecret;
    private final HttpHeaders headers = new HttpHeaders();
    private final SessionService sessionService;

    @PostConstruct
    public void init() {
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + getRasaJwt());
    }

    private String getRasaJwt() {
        Map<String, String> user = Map.of(
            "username", sessionService.getSession().getId().toString(),
            "role", "admin"
        );
        return JWT.create()
            .withPayload(Map.of("user", user))
            .sign(Algorithm.HMAC256(rasaSecret));
    }

    public ParseModel parse(RasaParseRequest request) {
        String url = rasaServer + "/model/parse";
        HttpEntity<Object> entity = new HttpEntity<>(request, headers);
        return new RestTemplate().postForObject(url, entity, ParseModel.class);
    }
}
