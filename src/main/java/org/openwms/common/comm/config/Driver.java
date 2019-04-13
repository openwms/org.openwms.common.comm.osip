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
 * @author <a href="mailto:hscherrer@openwms.org">Heiko Scherrer</a>
 */
@Component
@ConfigurationProperties("owms.driver")
public class Driver {

    /** Timezone. */
    private ZoneId timezone = ZoneId.of("UTC+00:00");
    /** Serialization methods: json, barray */
    private String serialization;
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

    public static class Osip {
        /** Whether OSIP protocol is enabled or not. */
        private boolean enabled = true;
        /** The telegram synchronisation field determines the start of an OSIP telegram. */
        private String syncField = "###";
        /** The timestamp pattern used in telegrams. */
        private String datePattern = "yyyyMMddHHmmss";
        /** The character used to pad telegrams up to the defined telegram length. */
        private String telegramFiller = "*";
        /** The expected length of OSIP telegrams. */
        private short telegramLength = 160;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getSyncField() {
            return syncField;
        }

        public void setSyncField(String syncField) {
            this.syncField = syncField;
        }

        public String getDatePattern() {
            return datePattern;
        }

        public void setDatePattern(String datePattern) {
            this.datePattern = datePattern;
        }

        public String getTelegramFiller() {
            return telegramFiller;
        }

        public void setTelegramFiller(String telegramFiller) {
            this.telegramFiller = telegramFiller;
        }

        public short getTelegramLength() {
            return telegramLength;
        }

        public void setTelegramLength(short telegramLength) {
            this.telegramLength = telegramLength;
        }
    }
}
