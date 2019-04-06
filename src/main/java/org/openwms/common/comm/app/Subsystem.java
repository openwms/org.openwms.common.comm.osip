/*
 * Copyright 2018 Heiko Scherrer
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
package org.openwms.common.comm.app;

/**
 * A Subsystem.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
public class Subsystem {

    private String name;
    private Inbound inbound;
    private Outbound outbound;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Inbound getInbound() {
        return inbound;
    }

    public void setInbound(Inbound inbound) {
        this.inbound = inbound;
    }

    public Outbound getOutbound() {
        return outbound;
    }

    public void setOutbound(Outbound outbound) {
        this.outbound = outbound;
    }

    public enum MODE {
        client, server;
    }

    public static class Inbound {
        private MODE mode;
        private String hostname = "localhost";
        private Integer port;
        private Integer soTimeout;
        private Integer soReceiveBufferSize;

        public MODE getMode() {
            return mode;
        }

        public void setMode(MODE mode) {
            this.mode = mode;
        }

        public String getHostname() {
            return hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public Integer getSoTimeout() {
            return soTimeout;
        }

        public void setSoTimeout(Integer soTimeout) {
            this.soTimeout = soTimeout;
        }

        public Integer getSoReceiveBufferSize() {
            return soReceiveBufferSize;
        }

        public void setSoReceiveBufferSize(Integer soReceiveBufferSize) {
            this.soReceiveBufferSize = soReceiveBufferSize;
        }
    }

    public static class Outbound {
        private MODE mode;
        private String hostname;
        private Integer port;
        private Integer soTimeout;
        private Integer soSendBufferSize;
        private String identifiedByField;
        private String identifiedByValue;

        public MODE getMode() {
            return mode;
        }

        public void setMode(MODE mode) {
            this.mode = mode;
        }

        public String getHostname() {
            return hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public Integer getSoTimeout() {
            return soTimeout;
        }

        public void setSoTimeout(Integer soTimeout) {
            this.soTimeout = soTimeout;
        }

        public Integer getSoSendBufferSize() {
            return soSendBufferSize;
        }

        public void setSoSendBufferSize(Integer soSendBufferSize) {
            this.soSendBufferSize = soSendBufferSize;
        }

        public String getIdentifiedByField() {
            return identifiedByField;
        }

        public void setIdentifiedByField(String identifiedByField) {
            this.identifiedByField = identifiedByField;
        }

        public String getIdentifiedByValue() {
            return identifiedByValue;
        }

        public void setIdentifiedByValue(String identifiedByValue) {
            this.identifiedByValue = identifiedByValue;
        }
    }
}
