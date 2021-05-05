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
Since our bot's business logic is decoupled from the NLP service, we will be using Rasa primarily as a NLU service.  To do so we will perform the following: 

1.  Train our models
2.  Test our models
3.  Run our NLU server
4.  Forward Symphony messages to NLU server for processing/predictions/parsing

## Training NLU-only models
To train an NLU model, run inside your newly generated nlp project: 
```
(env) $ rasa train nlu
```
This will look for NLU training data files in the data/ directory and saves a trained model in the models/ directory. The name of the model will start with nlu-.

## Testing your NLU model on the command line
To try out your NLU model on the command line, run the following command:
```
(env) $ rasa shell nlu
```
This will start the rasa shell and ask you to type in a message to test. You can keep typing in as many messages as you like.

Alternatively, you can leave out the nlu argument and pass in an nlu-only model directly:
```
(env) $ rasa shell -m models/nlu-20190515-144445.tar.gz
```

## Running an NLU server
To start a server with your NLU model, pass in the model name at runtime:
```
(env) $ rasa run --enable-api -m models/nlu-20190515-144445.tar.gz
```
You can then request predictions from your model using the /model/parse endpoint. To do this, run:

```
curl localhost:5005/model/parse -d '{"text":"hello"}'
```


