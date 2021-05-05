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

## Updating your NLU Data
For this example, our bot will be focused primarily on post-trade reconcilliation.  To make sure our bot understands these types of requests, we need to make sure that our bot understands intents and entities that are relevant to this domain.  To do so define an intent and add some sample data in you ```data/nlu.yml``` file:

```
nlu:

- intent: request_trade_status
  examples: |
    - Can you show me the trades?
    - Are there any open trades
    - Let's see trades
    - Show me trades
    - Trades?
    - Can I see the trades?
    - Are there any [unresolved](trade_state) trades?
    - Show me the [unresolved](trade_state) trades?
    - Can I see the [unresolved](trade_state) trades please?
    - [unresolved](trade_state) trades?

```

Now our training Rasa model will return an intent=request_trade_status for any of the above messages or ones that are simiar.  The more training data you provide your Rasa NLU server, the more accurate it will be at classifying a message's intent.  

In addition notice the syntax of ```[keyword](entity)``` to properly flag entities within a given messages.  This is an easy way to train your NLU server to extract relevant data from input messages.  

For more information on how to extract entities from a given message continue [here](https://rasa.com/docs/rasa/training-data-format#entities).  Leveraging this functionality is was enables our bot to understand the nature of a given request, thus funneling a user to the correct pathway and returning the appropriate data.
