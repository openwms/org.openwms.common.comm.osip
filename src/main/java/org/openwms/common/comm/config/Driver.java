/*
 * Copyright 2019 Heiko Scherrer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openwms.common.comm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

/**
 * A Driver.
 *
 * @author Heiko Scherrer
 */
@Component
@ConfigurationProperties("owms.driver")
public class Driver {

    /** Timezone. */
    private ZoneId timezone = ZoneId.of("UTC+00:00");
    /** Serialization methods: json, barray */
    private String serialization = "json";
    /** Port number used for synchronous RESTful communication. */
    private String portRest;
    /** OSIP protocol configuration. */
    private Osip osip;
    /** Global valid and individual connection parameters of the driver instance. */
    private Connections connections = new Connections();

    public ZoneId getTimezone() {
        return timezone;
    }

    public void setTimezone(ZoneId timezone) {
        this.timezone = timezone;
    }

    public String getPortRest() {
        return portRest;
    }

    public void setPortRest(String portRest) {
        this.portRest = portRest;
    }

    public String getSerialization() {
        return serialization;
    }

    public void setSerialization(String serialization) {
        this.serialization = serialization;
    }

    public Osip getOsip() {
        return osip;
    }

    public void setOsip(Osip osip) {
        this.osip = osip;
    }

    public Connections getConnections() {
        return connections;
    }

    public void setConnections(Connections connections) {
        this.connections = connections;
    }
}
