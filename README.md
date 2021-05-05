# NLP Webinar Bot

### This bot leverages Rasa NLP to create an interactive Symphony chatbot. 

## Symphony Bot Requirements
* JDK 8+
* [Symphony Java BDK](https://github.com/finos/symphony-bdk-java)
* Maven 3
* MongoDB
* Rasa NLP

## Rasa Requirments
* Python 3.6, 3.7, or 3.8 
* pip3 

## Configuration

```
bdk:
  host: develop2.symphony.com
  bot:
    username: nlp-bdk-bot
    privateKey:
      path: rsa/privatekey.pem

rasa-action-server: http://localhost:5005/model/parse

logging:
  level:
    com.symphony: debug
```


