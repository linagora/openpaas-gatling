# Gatling OpenPaaS

This project enables to proceed to OpenPaaS load testing using [Gatling](https://gatling.io/) technology.  
It aims to provide building blocks, specific OpenPaaS APIs steps as well as more complex scenarios.

## Configuration

The configuration is defined in the `src/main/scala/com/linagora/openpaas/gatling/Configuration.scala` file.
However, all the parameters can be set through environment variables. Just copy the
**env.sh.tpl** file as **env.sh**, update the values and source it: `source env.sh`

Available settings:
 - OpenPaaS platform admin credentials for domain provisioning
 - Domain used for load testing
 - Platform admin & Domain admin credentials
 - Base URL for OpenPaaS endpoints
 - Base URL for Sabre endpoints
 - Base URL for Web Socket server
 - Base URL for Jmap server
 - User count for scenario
 - Scenario duration
 - Inject duration
 - Number of contacts per virtual user
 - Number of calendar events per virtual user
 - Number of emails per virtual user
 - Authentication strategy
 - Base URL for LemonLDAP portal page

Environment variables:
 - `OPENPAAS_HOSTNAME` which is set to `localhost` by default
 - `OPENPAAS_PORT` which is set to `8080` by default
 - `OPENPAAS_PROTOCOL` which is set to `http` by default (you can use `https` instead for example)
 - `SABRE_BASE_URL` which is set to `` by default. If a url is provided, it will be used as the base URL for CalDAV requests, otherwise the ESN's DAV Proxy will handle CalDAV requests.
 - `WEBSOCKET_HOSTNAME` which is set to `OPENPAAS_HOSTNAME` by default
 - `WEBSOCKET_PORT` which is set to `OPENPAAS_PORT` by default
 - `WEBSOCKET_PROTOCOL` which is set to `ws` by default (you can use `wss` instead for example)
 - `JMAP_HOSTNAME` which is set to `OPENPAAS_HOSTNAME` by default
 - `JMAP_PORT` which is set to `1080` by default
 - `JMAP_PROTOCOL` which is set to `OPENPAAS_PROTOCOL` by default
 - `INJECT_DURATION` which is set to `10` by default. Unit is second
 - `SCENARIO_DURATION` which is set to `10` by default. Unit is second
 - `USER_COUNT` which is set to `1` by default
 - `HUMAN_ACTION_MIN_DELAY` which is set to `7` by default. The minimum amount of seconds between 2 human actions.
 - `HUMAN_ACTION_MAX_DELAY` which is set to `15` by default. The maximum amount of seconds between 2 human actions.
 - `AUTHENTICATION_STRATEGY` which is set to `basic` by default (you can use `lemonldap`, `oidc` or `pkce` instead)
 - `OIDC_CLIENT` oidc client to use for `oidc` and `pkce` authentication strategies
 - `OIDC_CALLBACK` url to redirect to after a successful login with `oidc` and `pkce` authentication strategies. Make sure to add redirect URL with explicit port in the lemonLDAP manager
 - `LEMONLDAP_PORTAL_HOSTNAME` which is set to `auth.latest.integration-open-paas.org` by default.
 - `PLATFORM_ADMIN_USER` is the user name of the platform administrator.
 - `PLATFORM_ADMIN_PWD` is the password of the platform administrator.
 - `INBOX_SPA_PATH` is the path to access the Inbox SPA. which is set to `inbox` by default
 - `CALENDAR_SPA_PATH` is the path to access the Calendar SPA, which is set to `calendar` by default
 - `CONTACTS_SPA_PATH` is the path to access the Contacts SPA, which is set to `contacts` by default 

For example, to run with OpenPaaS port `8000`:

```bash
$ export OPENPAAS_PORT="8000"
$ sbt
  > gatling:test
```

## User pool

There are two ways of creating a user pool for running tests:

1. Using a `src/test/resources/users.csv` file containing credentials of users.
This is the default method to retrieve user credentials. It is expected to have all users provisioned in the testing platform.

2. Including the provisioning steps in each scenario. This method requires platform admin credentials in configuration.

## Running tests in LemonLDAP integrated platform

For OpenPaaS instances that are protected by LemonLDAP, you need to provide the LemonLDAP portal page url in the configuration. You also have to change the `AUTHENTICATION_STRATEGY` configuration to `lemonldap`. Plus, all users credentials must be stored in `src/test/resources/users.csv` file.

## Gatling Recorder

The Gatling Recorder helps you to quickly generate scenarios, by either acting as a HTTP proxy between the 
browser and the HTTP server or converting HAR (Http ARchive) files. Either way, the Recorder generates a 
simple simulation that mimics your recorded navigation.

All the instructions to install and use are available at this [documentation](https://gatling.io/docs/current/http/recorder/?highlight=proxy)

## Run scenario

You can run all the scenario via sbt:

```bash
$ sbt
 > gatling:test
```

Run a specific scenario via sbt:

```bash
$ sbt
 > gatling:testOnly SCENARIO_FQDN
```

For example, you can run this scenario to open Calendar and open an event to view it details:

```
$ sbt
> gatling:testOnly com.linagora.openpaas.gatling.calendar.ViewEventDetailsSimulation
```

## Scenarios for Inbox

### Send an email

#### Scenario

In this scenario, each user will:

- Login to OpenPaaS
- Go to Inbox
- Open a composer window
- Write an email, attach a 200kB file
- Send the email
- Log out

#### Run

```
$ sbt
> gatling:testOnly com.linagora.openpaas.gatling.unifiedinbox.SendEmailSimulation
```

## Scenarios for Calendar

### 1. List calendars

#### Scenario

In this scenario, each user will:

- Login to OpenPaaS
- Go to Calendar, and all the usable calendars are listed

The equivalent simulation will ramp `USER_COUNT` users over `INJECT_DURATION`. 

#### Run

```
$ sbt
> gatling:testOnly com.linagora.openpaas.gatling.calendar.ListCalendarsSimulation
```

### 2. Create a new calendar

In this scenario, each user will:

- Login to OpenPaaS
- Go to Calendar
- Create a new calendar

The equivalent simulation will ramp `USER_COUNT` users over `INJECT_DURATION`. 

#### Run

```
$ sbt
> gatling:testOnly com.linagora.openpaas.gatling.calendar.CreateCalendarSimulation
```

### 3. View a calendar's details and update the calendar

In this scenario, each user will:

- Login to OpenPaaS
- Go to Calendar
- Choose a calendar to view its details
- Update the calendar

The equivalent simulation will ramp `USER_COUNT` users over `INJECT_DURATION`. 

#### Run

```
$ sbt
> gatling:testOnly com.linagora.openpaas.gatling.calendar.ViewAndUpdateCalendarSimulation
```

### 4. View a calendar's details and then delete the calendar

In this scenario, each user will:

- Login to OpenPaaS
- Go to Calendar
- Choose a calendar to view its details
- Delete the calendar

The equivalent simulation will ramp `USER_COUNT` users over `INJECT_DURATION`. 

#### Run

```
$ sbt
> gatling:testOnly com.linagora.openpaas.gatling.calendar.ViewAndDeleteCalendarSimulation
```

### 5. Create a new event 

In this scenario, each user will:

- Login to OpenPaaS
- Go to Calendar
- Create a new event in the default calendar

The equivalent simulation will ramp `USER_COUNT` users over `INJECT_DURATION`. 

#### Run

```
$ sbt
> gatling:testOnly com.linagora.openpaas.gatling.calendar.CreateEventSimulation
```

### 6. Create a new event with a lot of attendees

In this scenario, each user will:

- Login to OpenPaaS
- Go to Calendar
- Create a new event in the default calendar with a lof of attendees (between 100 and 200 attendees)

The equivalent simulation will ramp 20 users over 1 second. These numbers are configurable (see the commands below).

#### Run

- Run with the default configuration (ramping 20 users over 1 second):

```
$ sbt
> gatling:testOnly com.linagora.openpaas.gatling.calendar.CreateEventWithLotsOfAttendeesSimulation
```

- Run with a custom configuration (ramping 40 users over 2 seconds):

```
$ sbt
> ;-DrampUserCount=40;-DrampUserDuration=2;gatling:testOnly com.linagora.openpaas.gatling.calendar.CreateEventWithLotsOfAttendeesSimulation
```

### 7. Open an event

In this scenario, each user will:

- Login to OpenPaaS
- Go to Calendar
- Open an event to view its details

The equivalent simulation will ramp `USER_COUNT` users over `INJECT_DURATION`. 

#### Run

```
$ sbt
> gatling:testOnly com.linagora.openpaas.gatling.calendar.ViewEventDetailsSimulation
```

### 8. Open an event and update the event

In this scenario, each user will:

- Login to OpenPaaS
- Go to Calendar
- Open an event to view its details
- Update the event

The equivalent simulation will ramp `USER_COUNT` users over `INJECT_DURATION`. 

#### Run

```
$ sbt
> gatling:testOnly com.linagora.openpaas.gatling.calendar.ViewAndUpdateEventSimulation
```

### 9. Open an event and delete the event

In this scenario, each user will:

- Login to OpenPaaS
- Go to Calendar
- Open an event to view its details
- Delete the event

The equivalent simulation will ramp `USER_COUNT` users over `INJECT_DURATION`. 

#### Run

```
$ sbt
> gatling:testOnly com.linagora.openpaas.gatling.calendar.ViewAndDeleteEventSimulation
```

### 10. Mixed scenario

In this scenario, each user will:

- Login to OpenPaaS
- Go to Calendar
- Do one of the following actions at random:
  - 40%: Open an event
  - 20%: Create an event with a few attendees (between 1 and 10 attendees)
  - 15%: Open an event and update the event
  - 10%: Create a new event
  - 5%: Open an event and delete the event
  - 5%: Open an event and update the event
  - 3%: Create a new calendar
  - 2%: View a calendar's details and then delete the calendar

The equivalent simulation will ramp `USER_COUNT` users over `INJECT_DURATION`. 

#### Run

```
$ sbt
> gatling:testOnly com.linagora.openpaas.gatling.calendar.CalendarMixSimulation
```

## Scenarios for Contacts

### Open a contact in the collected address book

#### Scenario

In this scenario, each user will:

- Login to OpenPaaS
- Go to Contacts
- Open the collected address book
- Open a contact
- Log out

The equivalent simulation will ramp `USER_COUNT` users over `INJECT_DURATION`. 

#### Run

```
$ sbt
> gatling:testOnly com.linagora.openpaas.gatling.addressbook.OpenContactInCollectedAddressBookSimulation
```

## OpenPaaS Mixed Scenario

### Scenario

- Execute the 3 following scenarios randomly (each has a 33% chance to be executed):
  - Scenario 1: Calendar's mixed scenario
  - Scenario 2: Send an email
  - Scenario 3: Open a contact in the collected address book
- Pause between scenarios from 7.5 to 15 seconds
- Number of users: 20000
- Injection duration: 2000 seconds (10 users/sec)
- Scenario duration is 3 hours

### Run

```
$ export INJECT_DURATION="2000" SCENARIO_DURATION="10800" USER_COUNT="20000"
$ sbt
> gatling:testOnly com.linagora.openpaas.gatling.OpenPaaSMixSimulation
```

## Platform Test Simulations

### Calendar & Contacts

In this simulation, each user will:

- Execute one of the following scenarios randomly:
  - Scenario 1 (80% chance): Calendar's mixed scenario
  - Scenario 2 (20% chance): Open a contact in the collected address book
- Pause between 5 and 10 seconds between scenarios
- Number of users: `USER_COUNT`
- Injection duration: `INJECT_DURATION`
- Scenario duration: `SCENARIO_DURATION`

```
$ sbt
> gatling:testOnly com.linagora.openpaas.gatling.CalendarAndContactsPlatformTestSimulation
```

### Inbox
