# Purpose

This library is the implementation of the [OSIP message specification](https://interface21-io.gitbook.io/osip) that is used by the
[OpenWMS.org TCP/IP driver](https://openwms.github.io/org.openwms.common.comm) by default. 

# Resources
Find further documentation in the [Wiki](https://wiki.butan092.startdedicated.de/projects/common-osip-tcp-slash-ip-driver/wiki/main-page)

[![Build status](https://github.com/openwms/org.openwms.common.comm.osip/actions/workflows/master-build.yml/badge.svg)](https://github.com/openwms/org.openwms.common.comm.osip/actions/workflows/master-build.yml)
[![Quality](https://sonarcloud.io/api/project_badges/measure?project=org.openwms:org.openwms.common.comm.osip&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.openwms:org.openwms.common.comm.osip)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Maven central](https://img.shields.io/maven-central/v/org.openwms/org.openwms.common.comm.osip)](https://search.maven.org/search?q=a:org.openwms.common.comm.osip)
[![Join the chat at https://gitter.im/openwms/org.openwms](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/openwms/org.openwms?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# Build and Release

Build the component locally and release to Maven Central
```
$ mvn deploy -Prelease,gpg
```

[1]: src/site/resources/images/module_composition.png
