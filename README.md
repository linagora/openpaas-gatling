# Gatling OpenPaaS

This project enables to proceed to OpenPaaS load testing using [Gatling](https://gatling.io/) technology.

It aims to provide building blocks, specific OpenPaaS APIs steps as well as more complex scenario.

## Configuration

File path: `src/test/scala/com/linagora/openpaas/gatling/Configuration.scala`

Available settings:
 - User count for scenario
 - Scenario duration
 - OpenPaaS platform admin credentials for domain provisioning
 - Base URL for OpenPaaS endpoints
 - Domain used for load testing
 - Domain admin credentials
 - Base URL for Jmap server
 
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