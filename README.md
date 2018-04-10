# Gatling OpenPaaS

This project enables to proceed to OpenPaaS load testing using [Gatling]() technology.

It aims to provide building blocks, specific OpenPaaS APIs steps as well as more complex scenari.

## Configuration

File path: `src/test/scala/com/linagora/openpaas/gatling/Configuration.scala`

Available settings:
 - User count for scenari
 - Scenari duration
 - OpenPaaS platform admin credentials for user provisionning
 - Base for OpenPaaS endpoints
 - Domain used for load testing (id and name)

## Scenari availables

### Provisioning demo

This scenario create users that then read their profile. To run it:

```
$ sbt
> gatling:testOnly com.linagora.openpaas.gatling.ProvisioningScenario
```
