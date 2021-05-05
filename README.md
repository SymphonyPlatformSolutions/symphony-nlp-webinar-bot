# NLP Webinar Bot

### This bot leverages Rasa NLP to create an interactive Symphony chatbot. 

## Symphony Bot Requirements
* JDK 8+
* [Symphony Java BDK](https://github.com/finos/symphony-bdk-java)
* Maven 3
* MongoDB (
* [Rasa NLP](https://rasa.com/docs/rasa/)

## Rasa Requirments
* Python 3.6, 3.7, or 3.8 
* pip3 

## Symphony Bot Configuration

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
## Getting Started with NLP

For our Symphony Bot, we are leveraging Rasa NLP as our NLP engine.  We chose to use Rasa NLP because it is open source and able to be trained/run locally.  The business logic of this bot application is decoupled from the NLP engine so that one could replace Rasa with an NLP engine of choice.

### Rasa Quick Installation:
```
// create and activate your virtual environment
$ python3 -m venv env
$ source env/bin/activate

// install rasa: 
(env) $ pip3 install -U pip
(env) $ pip3 install rasa
```
After you have installed Rasa NLP, generate a new project scaffold:
```
(env) $ rasa init
```







