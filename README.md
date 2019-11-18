# Gatling OpenPaaS

This project enables to proceed to OpenPaaS load testing using [Gatling](https://gatling.io/) technology.

It aims to provide building blocks, specific OpenPaaS APIs steps as well as more complex scenario.

## Configuration

File path: `src/test/scala/com/linagora/openpaas/gatling/Configuration.scala`

Available settings:
 - User count for scenario
 - Scenario duration
 - OpenPaaS platform admin credentials for domain provisioning
 - Domain used for load testing
 - Platform admin & Domain admin credentials
 - Base URL for OpenPaaS endpoints
 - Base URL for Web Socket server
 - Base URL for Jmap server

Environment variable:
 * `OPENPAAS_HOSTNAME` which is set to `localhost` by default
 * `OPENPAAS_PORT` which is set to `8080` by default
 * `OPENPAAS_PROTOCOL` which is set to `http` by default (you can use `https` instead for example)
 * `WEBSOCKET_HOSTNAME` which is set to `OPENPAAS_HOSTNAME` by default
 * `WEBSOCKET_PORT` which is set to `OPENPAAS_PORT` by default
 * `WEBSOCKET_PROTOCOL` which is set to `ws` by default (you can use `wss` instead for example)
 * `JMAP_HOSTNAME` which is set to `OPENPAAS_HOSTNAME` by default
 * `JMAP_PORT` which is set to `1080` by default
 * `JMAP_PROTOCOL` which is set to `OPENPAAS_PROTOCOL` by default
 
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

### Run scenario

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