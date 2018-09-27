# Intention

This standalone and runnable Spring Boot application is an implementation of the OSIP specification that communicates in OSIP TCP/IP message format
with subsystems underneath. Those subsystems almost lack of resources and do not provide a higher level protocol on top of TCP/IP. The
implementation is aware of multiple tenants (e.g. projects) and may run in the cloud with different port settings. Note: This does not allow
to instantiate multiple instances of the same driver component at one time. Each instance must have it's own configuration, in particular
TCP/IP port settings. A project (tenant) may have multiple drivers deployed, with all running on different ports.

# Requirements

## Functional Requirements

ID | Name | Priority | Description
--- | --- | --- | ---
FR001 | Support OSIP 1.0 | HIGH | All functionality by OSIP defined must be implemented

## Non-functional Requirements

ID | Group | Priority | Description
--- | --- | --- | ---
NR001 | Performance | HIGH | All expected responses to OSIP requests must be sent within **150 milliseconds** from message arrival.
NR002 | Scalability | MEDIUM | The component must be capable to **scale out horizontally** within a projects scope (same tenant).
NR003 | Extendability | MEDIUM | New telegram types (OSIP versions) must be integrated in an encapsulated fashion. At best a new library can be dropped onto the classpath, at minimum  all artifacts of the new telegram implementation must be located in the same Java package without the need to touch existing other packages.

# Architecture

The module uses a couple of the well known Enterprise Integration Patterns ([EIP](http://www.enterpriseintegrationpatterns.com)), like a Router, Transformer or an Enricher. For that reason
[Spring Integration](https://projects.spring.io/spring-integration) is used as integration framework. In addition this is a very convenient and flexible way to adopt new transport channels
beside TCP/IP.

The overall integration architecture is shown below. The entry point is the `inboundAdapter` that is connected to a `TcpNetServerConnectionFactory` (not shown) and forwards incoming telegrams
to the `inboundChannel`. A first transformer (`telegramTransformer`) terminates the ASCII string and converts into a Spring [Message](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/messaging/Message.html).
This is done with support of the appropriate `MessageMapper` that should exist for each telegram type. For instance the [TimesyncTelegramMapper](src/main/java/org/openwms/common/comm/synq/tcp/TimesyncTelegramMapper.java) knows best
how to transform from a String into a [TimesyncRequest](src/main/java/org/openwms/common/comm/synq/TimesyncRequest.java). After the telegram is transformed into a valid message type the generic 
[`messageRouter`](src/main/java/org/openwms/common/comm/router/CommonMessageRouter.java) picks up the right queue and activates the proper `ServiceActivator`. Notice that the service activators
queue name is built on the fly and follows a naming convention. This is one aspect to support requirements NR003.

![Architecture][4]

# Deployment

The TCP/IP driver can run completely independent of cloud infrastructure services. This may the appropriate deployment model during development or for smaller projects where no central
infrastructure services are required. In a larger project setup where the driver component is instantiated multiple times it makes sense to keep configuration on a central config server,
this is where [OpenWMS.org Configuration](https://github.com/spring-labs/org.openwms.configuration) comes into play.

## Standalone Deployment

As said all required configuration must be passed at startup time because no infrastructure services are required to access. Without any configuration the driver is started with the provided
default configuration that is suitable for one driver instance:

```yaml
owms:
  driver:
    server:
      port: 30001
```

Property | Description
-------- | ---
owms.driver.server.port | The unique port number the driver receives connections on. Multiple driver instances must have different port numbers.

In case you want to override the port number on startup just set the environment variable accordingly. In the following example 2 drivers are started, with different ports.

```
$ java -Dowms.driver.server.port=30001 -jar tcpip-driver.jar
$ java -Dowms.driver.server.port=30002 -jar tcpip-driver.jar
```

Afterwards simply send a OSIP SYNQ telegram to driver with port 30001 to get a response telegram:

```
$ telnet localhost 30001
Trying ::1...
Connected to localhost.
Escape character is '^]'.
###00160RAS10MFC__00001SYNQ20171123225959***********************************************************************************************************************
###00160MFC__RAS1000002SYNC20180927152848***********************************************************************************************************************
```
The first telegram string (SYNQ) is sent to the driver, whereas the driver responds with a SYNC telegram to syncronize the current system time.

## Distributed Deployment

In case of multiple driver components and lots of microservices it makes sense to keep service configuration at a central place, the [OpenWMS.org Configuration](https://github.com/spring-labs/org.openwms.configuration)
server is the right choice. This infrastructure service takes the configuration of each process from a configured Git repository and passes it down to the process at startup. By using a
configuration sever it is also possible to change configuration at runtime without the need to restart processes. Configuration is pushed down into the microservices and the service
configuration is refreshed dynamically.

To run the driver component in this manner, the Spring profile "DISTRIBUTED" must be enabled. It's also a good practice to provide unique application names, at least when the driver
is instantiated multiple times:

```
$ java -Dspring.profiles.active=DISTRIBUTED -Dspring.application.name=tcpip-palett1 -jar tcpip-driver.jar
$ java -Dspring.profiles.active=DISTRIBUTED -Dspring.application.name=tcpip-palett2 -jar tcpip-driver.jar
```

Each driver is now starting up and looking for a configuration server. The application name is used to load the appropriate driver configuration from. For example the Git repository
[ZILE](https://github.com/spring-labs/org.openwms.zile/tree/master/conf) includes YAML configuration files for all processes used in ZILE project, same is true for tcpip-palett1.yml and
tcpip-palett2.yml.

tcpip-palett1.yml
```yaml
owms:
  tenant: ZILE
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
  tenant: ZILE
  driver:
    server:
      port: 30002
      so-timeout: 300000
      so-receive-buffer-size: 160
      so-send-buffer-size: 160
      routing-service-name: routing-service # is default
```

# Configuration

The most important configuration properties of the driver component are the following.

Property | Description
-------- | ---
owms.tenant | The tenant defined the branch in the Git repository to look up configuration. It is also used to separate log files per tenant. Call it after your project name
owms.driver.server.port | The unique port number the driver receives connections on. Multiple driver instances must have different port numbers.
owms.driver.server.so-timeout | Socket timeout wheth the socket is closed after idle time, see [Spring Integration Reference](https://docs.spring.io/spring-integration/docs/4.3.9.RELEASE/reference/html/ip.html#connection-factories)
owms.driver.server.so-receive-buffer-size | The expected telegram size of the receive buffer, by default OSIP telegrams have a length of 160 chars
owms.driver.server.so-send-buffer-size | The expected telegram size of the send buffer, by default OSIP telegrams have a length of 160 chars
owms.driver.server.routing-service-name | Most OSIP telegrams require to contact the routing service for further action. This is the Spring application name of the TMS Routing Service how it can be discovered from the service registry.

# Logging

The driver component is configured for tenant aware logging. If, for instance, the tenant is configured to 'myProject' (owms.tenant=myProject) a tslog file (Technical Service Log) is written with
name 'myProject-COMMON.tslog' that contains the time consumption processing each telegram took. An example tslog file looks like:

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
$ mvn clean deploy -Prelease,gpg
```

 [4]: src/site/resources/images/integration_patterns.png
