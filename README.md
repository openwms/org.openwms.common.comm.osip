# Purpose

This standalone and runnable Spring Boot application is an implementation of the [OSIP specification](https://interface21-io.gitbook.io/osip/)
that communicates with the subsystems, like a PLC or a Raspberry Pi, using the OSIP TCP/IP message format.
This kind of subsystems lack of resources and do not provide a higher level protocol on top of TCP/IP.
The implementation is aware of multiple tenants (e.g. projects) and may run in the cloud with different
port settings.
Note: Instantiating multiple instances of the driver component with same port settings is not possible.
Each instance must have its own configuration, in particular its own TCP/IP port settings. A project
(tenant) may have multiple drivers deployed, all running on different ports.

# Resources

Documentation at [OpenProject Wiki](https://openproject.butan092.startdedicated.de/projects/common-osip-tcp-slash-ip-driver/wiki/main-page)

[![Build status](https://github.com/openwms/org.openwms.common.comm/actions/workflows/master-build.yml/badge.svg)](https://github.com/openwms/org.openwms.common.comm/actions/workflows/master-build.yml)
[![Quality](https://sonarcloud.io/api/project_badges/measure?project=org.openwms:org.openwms.common.comm&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.openwms:org.openwms.common.comm)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Maven central](https://img.shields.io/maven-central/v/org.openwms/org.openwms.common.comm)](https://search.maven.org/search?q=a:org.openwms.common.comm)
[![Docker pulls](https://img.shields.io/docker/pulls/openwms/org.openwms.common.comm)](https://hub.docker.com/r/openwms/org.openwms.common.comm)
[![Join the chat at https://gitter.im/openwms/org.openwms](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/openwms/org.openwms?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# Operation Modes

A driver instance can be started in different operation modes: **Simplex** or **Duplex**
communication with either **Client** or **Server** connection mode. All four can be arbitrary combined.

## Simplex Communication

Simplex communication means a client application connecting to the driver uses one socket
for inbound communication and another socket for the outbound. So sending and receiving
messages is handled through separated and dedicated socket connections.

With the simplex communication mode the inbound communication is configured differently as
the outbound communication. This way the driver creates two separate `ConnectionFactories`,
one for inbound and one for outbound. Each socket can be configured either in **Client**
or **Server** mode. A _Client_ configured Socket tries to connect to a listening server
whereas configured as _Server_ the driver opens a socket itself and listens for incoming
connections. 

A typical simplex configuration looks like:
```
owms:
  driver:
    connections:
      hostname: 0.0.0.0
      subsystems:
        - name: SPS01
          inbound:
            mode: server
            port: 30001
            so-receive-buffer-size: 200
          outbound:
            mode: client
            port: 30002
            so-send-buffer-size: 200
            identified-by-field: "RECV"
            identified-by-value: "SPS01"
```

Each communication direction is configured with a different port setting and with _mode_
set to _server_ and _client_. To correlate outbound messages with previously received
ones, the _identified-by-*_ fields are used.

## Duplex Communication

In contrast to simplex connections the driver instance can also be configured for
bidirectional duplex mode where only one socket is used for inbound and outbound
communication. Instead of configuring _inbound_ or _outbound_ only one _duplex_ configuration
must be present. 

```
owms:
  driver:
    connections:
      subsystems:
        - name: SPS03
          duplex:
            mode: server
            hostname: localhost
            port: 30003
            so-send-buffer-size: 200
            so-receive-buffer-size: 200
            identified-by-field: "RECV"
            identified-by-value: "SPS03"
```

# Architecture

The module uses a couple of the well known Enterprise Integration Patterns ([EIP](http://www.enterpriseintegrationpatterns.com)), like a Router, Transformer or an Enricher. For that reason
[Spring Integration](https://projects.spring.io/spring-integration) is used as integration framework. In addition this is a very convenient and flexible way to adopt new transport channels
beside TCP/IP.

The overall integration architecture is shown below. The entry point is the `inboundAdapter` that is connected to a `TcpNetServerConnectionFactory` (not shown) and forwards incoming telegrams
to the `inboundChannel`. A first transformer (`telegramTransformer`) terminates the ASCII string and converts into a Spring [Message](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/messaging/Message.html).
This is done with support of the appropriate `MessageMapper` that must exist for each telegram type. For instance the [TimesyncTelegramMapper](src/main/java/org/openwms/common/comm/osip/synq/tcp/TimesyncTelegramMapper.java) knows best
how to transform a String into a [TimesyncRequest](src/main/java/org/openwms/common/comm/osip/synq/TimesyncRequest.java). After the telegram is transformed into a valid message type the generic 
[`messageRouter`](src/main/java/org/openwms/common/comm/router/CommonMessageRouter.java) picks up the right queue and activates the proper `ServiceActivator`. Notice that the service activators
queue name is built on the fly and follows a naming convention. This is one aspect to support requirements NR003.

![Architecture][4]

# Deployment

The TCP/IP driver can run completely independent of cloud infrastructure services. This
could be the appropriate deployment model during development or for small projects where
no central infrastructure services are required. In a larger project setup with lots of
subsystems and where the driver component is instantiated multiple times it makes sense to
keep configuration on a central config server, this is where [OpenWMS.org Configuration](https://github.com/spring-labs/org.openwms.configuration)
comes into play.

## Standalone Deployment

As said all required configuration must be passed at startup time because no
infrastructure services are required to access. Without any configuration the driver is
started with the provided default configuration that is suitable for one driver instance:

```yaml
owms:
  driver:
    server:
      port: 30001
```

Property | Description
-------- | ---
owms.driver.server.port | The unique port number the driver receives connections on. Multiple driver instances must have different port numbers.

In case you want to override the port number on startup just set the environment variable
accordingly. In the following example 2 drivers are started, with different ports.

```
$ java -Dowms.driver.server.port=30001 -jar tcpip-driver-exec.jar
$ java -Dowms.driver.server.port=30002 -jar tcpip-driver-exec.jar
```

Afterwards simply send a OSIP SYNQ telegram to driver with port 30001 to get a response
telegram:

```
$ telnet localhost 30001
Trying ::1...
Connected to localhost.
Escape character is '^]'.
###00160SPS01MFC__00001SYNQ20171123225959***********************************************************************************************************************
###00160MFC__SPS0100002SYNC20180927152848***********************************************************************************************************************
```
The first telegram string (SYNQ) is sent to the driver, whereas the driver responds with a
SYNC telegram to synchronize the current system time.

## Distributed Deployment

In case of multiple driver components and lots of microservices it makes sense to keep
service configuration at a central place and the [OpenWMS.org Configuration](https://github.com/spring-labs/org.openwms.configuration)
server is the right choice. This infrastructure service takes the configuration of each
process from a configured Git repository and passes it down to the processes at process 
startup. By using a configuration sever it is also possible to change configuration at
runtime without the need to restart processes. Configuration is pushed down into the
microservices and the service configuration is refreshed dynamically.

To run the driver component in a distributed fashion, the Spring profile "DISTRIBUTED"
must be enabled. It's also a good practice to provide unique application names, at least
when the driver is instantiated multiple times:

```
$ java -Dspring.profiles.active=DISTRIBUTED -Dspring.application.name=tcpip-palett1 -jar tcpip-driver-exec.jar
$ java -Dspring.profiles.active=DISTRIBUTED -Dspring.application.name=tcpip-palett2 -jar tcpip-driver-exec.jar
```

Each driver is now starting up and looking for a configuration server. The application
name is used to load the appropriate driver configuration from. For example the Git
repository
[KARL](https://github.com/spring-labs/org.openwms.projects.KARL/tree/master/conf) includes
YAML configuration files for all processes used in KARL project, same is true for
tcpip-palett1.yml and tcpip-palett2.yml.

tcpip-palett1.yml
```yaml
owms:
  tenant: KARL
  driver:
    server:
      port: 30001
      so-timeout: 300000
      so-receive-buffer-size: 160
      so-send-buffer-size: 160
      routing-service-name: routing-service # is default
```

tcpip-palett2.yml
```yaml
owms:
  tenant: KARL
  driver:
    server:
      port: 30002
      so-timeout: 300000
      so-receive-buffer-size: 160
      so-send-buffer-size: 160
      routing-service-name: routing-service # is default
```
# Communication Protocols

The way how the driver communicates to other OpenWMS.org microservices can be defined by
setting a Spring profile. In **ASYNCHRONOUS** mode the driver uses AMQP and sends the
messages to RabbitMQ exchanges. In **SYNCHRONOUS** mode the driver calls defined REST
endpoints of microservices.

Synchronous communication is used by default.

## Synchronous Communication

Either set the SYNCHRONOUS profile explicitly or omit it. 

```
$ java -Dspring.profiles.active=SYNCHRONOUS -Dspring.application.name=tcpip-palett1 -jar tcpip-driver-exec.jar
```

## Asynchronous Communication (RabbitMQ)

To enable asynchronous communication over RabbitMQ set the Spring profile **ASYNCHRONOUS**

```
$ java -Dspring.profiles.active=ASYNCHRONOUS -Dspring.application.name=tcpip-palett1 -jar tcpip-driver-exec.jar
```

# Configuration

Important configuration properties of the driver component are the following.

Property | Description
-------- | ---
owms.driver.timezone                      | The ZoneId (java.time.ZoneId) used to create timestamps
owms.driver.serialization                 | (De-)Serialization method used for asynchronous communication. Possible values are `barray`and `json`
owms.driver.osip.enabled                  | Whether OSIP telegram support is enabled or not
owms.driver.osip.sync-field               | Value of the SYNC field used to detect the start of a telegram
owms.driver.osip.date-pattern             | Date pattern used in OSIP telegrams
owms.driver.routing-service.name          | The logical service name of the TMS Routing Service
owms.driver.routing-service.protocol      | The protocol used to connect to the TMS Routing Service (eg. https) 
owms.driver.routing-service.username      | The username for BASIC authentication 
owms.driver.routing-service.password      | The password for BASIC authentication
owms.driver.connections.hostname          | The hostname setting inherited to all subsequent subsystem configurations
owms.driver.connections.port-rest         | The driver accepts incoming connections at this port in synchronous communication
owms.driver.connections.so-timeout        | The socket timeout inherited to all subsequent subsystem configurations
owms.driver.connections.so-receive-buffer-size | The receiving buffer size inherited to all subsequent subsystem configurations
owms.driver.connections.so-send-buffer-size | The sending buffer size inherited to all subsequent subsystem configurations
owms.driver.connections.identified-by-field | The identified-by-field inherited to all subsequent subsystem configurations
owms.driver.connections.subsystems        | A list of subsystems. A driver can handle multiple subsystems in different modes
owms.driver.connections.subsystems[].name | Unique name of the subsystem
owms.driver.connections.subsystems[].inbound.mode | The operational mode. Either `server` or `client`
owms.driver.connections.subsystems[].inbound.hostname | The hostname to connect to or the name of the interface to listen on (if mode is `server`)
owms.driver.connections.subsystems[].inbound.port | The port to connect to or to listen on
owms.driver.connections.subsystems[].inbound.so-timeout | The socket timeout 
owms.driver.connections.subsystems[].inbound.so-receive-buffer-size | The size of the receiving buffer
owms.driver.connections.subsystems[].outbound.mode | The operational mode. Either `server` or `client`
owms.driver.connections.subsystems[].outbound.hostname | The hostname to connect to or the name of the interface to listen on (if mode is `server`)
owms.driver.connections.subsystems[].outbound.port | The port to connect to or to listen on
owms.driver.connections.subsystems[].outbound.so-timeout | The socket timeout 
owms.driver.connections.subsystems[].outbound.so-send-buffer-size | The size of the send buffer
owms.driver.connections.subsystems[].outbound.identified-by-field | The name of the telegram field that identifies the telegram receiver
owms.driver.connections.subsystems[].outbound.identified-by-value | The actual telegram receiver name
owms.driver.connections.subsystems[].duplex.mode | The operational mode. Either `server` or `client`
owms.driver.connections.subsystems[].duplex.hostname | The hostname to connect to or the name of the interface to listen on (if mode is `server`)
owms.driver.connections.subsystems[].duplex.port | The port to connect to or to listen on
owms.driver.connections.subsystems[].duplex.so-timeout | The socket timeout 
owms.driver.connections.subsystems[].duplex.so-send-buffer-size | The size of the send buffer
owms.driver.connections.subsystems[].duplex.so-receive-buffer-size | The size of the receiving buffer
owms.driver.connections.subsystems[].duplex.identified-by-field | The name of the telegram field that identifies the telegram receiver
owms.driver.connections.subsystems[].duplex.identified-by-value | The actual telegram receiver name

# Logging

The driver component is configured for tenant aware logging. If, for instance, the tenant
is configured to 'myProject' (owms.tenant=myProject) a tslog file (Technical Service Log)
is written with name 'myProject-COMMON.tslog' that contains the time consumption
processing each telegram took. An example tslog file looks like:

```
myProject COMMON 2018-09-27 15:26:27.280  INFO  [MEASURED                                ] : [TSL]>> ErrorMessageServiceActivator#wakeUp
myProject COMMON 2018-09-27 15:26:27.305  INFO  [MEASURED                                ] : [TSL]<< ErrorMessageServiceActivator#wakeUp took 26 [ms]
myProject COMMON 2018-09-27 15:27:01.586  INFO  [MEASURED                                ] : [TSL]>> TimesyncServiceActivator#wakeUp
myProject COMMON 2018-09-27 15:27:01.587  INFO  [MEASURED                                ] : [TSL]<< TimesyncServiceActivator#wakeUp took 1 [ms]
myProject COMMON 2018-09-27 15:28:48.638  INFO  [MEASURED                                ] : [TSL]>> TimesyncServiceActivator#wakeUp
myProject COMMON 2018-09-27 15:28:48.657  INFO  [MEASURED                                ] : [TSL]<< TimesyncServiceActivator#wakeUp took 22 [ms]
myProject COMMON 2018-09-27 15:28:50.704  INFO  [MEASURED                                ] : [TSL]>> TimesyncServiceActivator#wakeUp
```

Column   | Description
-------- | ---
#1  | Tenant name
#2  | Module name (CORE, COMMON, TMS or WMS)
#3  | Date of log entry written
#4  | Time of log entry written
#5  | Log level (INFO)
#6  | Log category. All tslogs are using the category MEASURED
#7  | TSL is another identifier used in log processing systems like logstash
#8  | >>: incoming or <<: outgoing
#9  | Type of telegram activator and indirectly the telegram handler used. By this information the processing telegram can be determined
#10 | How long the message processing took in ms

# Build and Release

```
$ mvn deploy -Prelease,gpg -Ddebug.info=true
```

 [4]: src/site/resources/images/integration_patterns.png
