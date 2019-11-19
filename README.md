# Gatling OpenPaaS

This project enables to proceed to OpenPaaS load testing using [Gatling](https://gatling.io/) technology.

It aims to provide building blocks, specific OpenPaaS APIs steps as well as more complex scenario.

## Configuration

File path: `src/test/scala/com/linagora/openpaas/gatling/Configuration.scala`

Available settings:
 - OpenPaaS platform admin credentials for domain provisioning
 - Domain used for load testing
 - Platform admin & Domain admin credentials
 - Base URL for OpenPaaS endpoints
 - Base URL for Web Socket server
 - Base URL for Jmap server
 - User count for scenario
 - Scenario duration
 - Inject duration
 - Number of contacts per virtual user
 - Number of calendar events per virtual user
 - Number of emails per virtual user

Environment variable:
 - `OPENPAAS_HOSTNAME` which is set to `localhost` by default
 - `OPENPAAS_PORT` which is set to `8080` by default
 - `OPENPAAS_PROTOCOL` which is set to `http` by default (you can use `https` instead for example)
 - `WEBSOCKET_HOSTNAME` which is set to `OPENPAAS_HOSTNAME` by default
 - `WEBSOCKET_PORT` which is set to `OPENPAAS_PORT` by default
 - `WEBSOCKET_PROTOCOL` which is set to `ws` by default (you can use `wss` instead for example)
 - `JMAP_HOSTNAME` which is set to `OPENPAAS_HOSTNAME` by default
 - `JMAP_PORT` which is set to `1080` by default
 - `JMAP_PROTOCOL` which is set to `OPENPAAS_PROTOCOL` by default
 - `INJECT_DURATION` which is set to `10` by default. Unit is second
 - `SCENARIO_DURATION` which is set to `10` by default. Unit is second
 - `USER_COUNT` which is set to `1` by default
 
For example, to run with OpenPaaS port `8000`:

```bash
$ export OPENPAAS_PORT="8000"
$ sbt
  > gatling:test
```

## Gatling Recorder

The Gatling Recorder helps you to quickly generate scenarios, by either acting as a HTTP proxy between the 
browser and the HTTP server or converting HAR (Http ARchive) files. Either way, the Recorder generates a 
simple simulation that mimics your recorded navigation.

All the instructions to install and use are available at this [documentation](https://gatling.io/docs/current/http/recorder/?highlight=proxy)

## Run scenario

You can run all the scenario via sbt :

```bash
$ sbt
 > gatling:test
```

Run a specific scenario via sbt :
```bash
$ sbt
 > gatling:testOnly SCENARIO_FQDN
```

For example: this scenario to search and open a calendar event. To run it:

```
$ sbt
> gatling:testOnly com.linagora.openpaas.gatling.calendar.SearchEventsScenario
```

## Main Scenarios
### Send an email
#### Scenario
- user login on OpenPaaS
- go in "inbox"
- open a compose window
- write an email, attach a 200kB file
- send
- disconnect

#### Run
```
$ sbt
> gatling:testOnly com.linagora.openpaas.gatling.unifiedinbox.SendEmailScenario
```

### Search and open an calendar 
#### Scenario
- user login on OP
- go in calendar
- lookup an event using search
- click on the event
- disconnect

#### Run
```
$ sbt
> gatling:testOnly com.linagora.openpaas.gatling.calendar.SearchEventsScenario
```

### Open a contact in collected address book
#### Scenario
- user login on OP
- go in contacts
- click on collected contacts
- open a contact
- disconnect

#### Run
```
$ sbt
> gatling:testOnly com.linagora.openpaas.gatling.addressbook.OpenContactScenario
```

### Send a chat message in general channel
#### Scenario
- user login on OP
- go in chat
- go in general channel
- writes a message
- disconnect

#### Run
```
$ sbt
> gatling:testOnly com.linagora.openpaas.gatling.chat.SendMessageScenario
```

### OpenPaaS mix scenario

#### Scenario
- Execute 4 scenarios above during scenario duration, each scenario has 25% of total time
- Pause between scenarios from 7.5 to 15 seconds
- Number of users: 20000
- Injection duration: 2000 seconds (10 users/sec)
- Scenario duration is 3 hours

#### Run
```
$ export INJECT_DURATION="2000" SCENARIO_DURATION="10800" USER_COUNT="20000"
$ sbt
> gatling:testOnly com.linagora.openpaas.gatling.OpenPaaSMixScenario
```