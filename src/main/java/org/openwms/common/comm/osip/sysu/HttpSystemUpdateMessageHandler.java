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
package org.openwms.common.comm.osip.sysu;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.openwms.core.SecurityUtils;
import org.openwms.core.SpringProfiles;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.Date;
import java.util.function.Function;

/**
 * A HttpSystemUpdateMessageHandler forwards system updates to the LocationGroup services directly without using the routing service.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
@Profile("!"+ SpringProfiles.ASYNCHRONOUS_PROFILE)
@Component
@RefreshScope
class HttpSystemUpdateMessageHandler implements Function<GenericMessage<SystemUpdateMessage>, Void> {

    private final RestTemplate restTemplate;
    private final String routingServiceName;
    private final String routingServiceProtocol;
    private final String routingServiceUsername;
    private final String routingServicePassword;

    HttpSystemUpdateMessageHandler(RestTemplate restTemplate,
                              @Value("${owms.driver.server.routing-service.name:routing-service}") String routingServiceName,
                              @Value("${owms.driver.server.routing-service.protocol:http}") String routingServiceProtocol,
                              @Value("${owms.driver.server.routing-service.username:user}") String routingServiceUsername,
                              @Value("${owms.driver.server.routing-service.password:sa}") String routingServicePassword) {
        this.restTemplate = restTemplate;
        this.routingServiceName = routingServiceName;
        this.routingServiceProtocol = routingServiceProtocol;
        this.routingServiceUsername = routingServiceUsername;
        this.routingServicePassword = routingServicePassword;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void apply(GenericMessage<SystemUpdateMessage> msg) {
        restTemplate.exchange(
                routingServiceProtocol+"://"+routingServiceName+"/sysu",
                HttpMethod.POST,
                new HttpEntity<>(new RequestVO.Builder()
                        .locationGroupName(msg.getPayload().getLocationGroupName())
                        .errorCode(msg.getPayload().getErrorCode())
                        .created(msg.getPayload().getCreated())
                        .build(), SecurityUtils.createHeaders(routingServiceUsername, routingServicePassword)),
                Void.class
        );
        return null;
    }

    private static class RequestVO implements Serializable {
        @JsonProperty
        Date created;
        @JsonProperty
        String locationGroupName, errorCode;

        private RequestVO(Builder builder) {
            setCreated(builder.created);
            setLocationGroupName(builder.locationGroupName);
            setErrorCode(builder.errorCode);
        }

        public Date getCreated() {
            return created;
        }

        public void setCreated(Date created) {
            this.created = created;
        }

        public String getLocationGroupName() {
            return locationGroupName;
        }

        public void setLocationGroupName(String locationGroupName) {
            this.locationGroupName = locationGroupName;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }


        public static final class Builder {
            private Date created;
            private String locationGroupName;
            private String errorCode;

            public Builder() {
            }

            public Builder created(Date val) {
                created = val;
                return this;
            }

            public Builder locationGroupName(String val) {
                locationGroupName = val;
                return this;
            }

            public Builder errorCode(String val) {
                errorCode = val;
                return this;
            }

            public RequestVO build() {
                return new RequestVO(this);
            }
        }
    }
}
