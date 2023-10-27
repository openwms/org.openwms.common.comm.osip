/*
 * Copyright 2005-2023 the original author or authors.
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
package org.openwms.common.comm.osip.err;

import org.openwms.common.comm.MessageProcessingException;
import org.openwms.common.comm.osip.OSIPComponent;
import org.openwms.core.SecurityUtils;
import org.openwms.core.SpringProfiles;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.client.RestTemplate;

import java.util.function.Function;

/**
 * An HttpErrorMessageHandler is the handler function to accept {@link ErrorMessage}s and
 * forward them for processing over HTTP to the routing-service.
 *
 * @author Heiko Scherrer
 */
@Profile("!"+ SpringProfiles.ASYNCHRONOUS_PROFILE)
@OSIPComponent
@RefreshScope
class HttpErrorMessageHandler implements Function<GenericMessage<ErrorMessage>, Void> {

    private final RestTemplate restTemplate;
    private final String routingServiceName;
    private final String routingServiceProtocol;
    private final String routingServiceUsername;
    private final String routingServicePassword;

    HttpErrorMessageHandler(RestTemplate restTemplate,
            @Value("${owms.driver.routing-service.name}") String routingServiceName,
            @Value("${owms.driver.routing-service.protocol}") String routingServiceProtocol,
            @Value("${owms.driver.routing-service.username}") String routingServiceUsername,
            @Value("${owms.driver.routing-service.password}") String routingServicePassword) {
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
    public Void apply(GenericMessage<ErrorMessage> msg) {
        try {
            restTemplate.exchange(
                    routingServiceProtocol + "://" + routingServiceName + "/err",
                    HttpMethod.POST,
                    new HttpEntity<>(msg.getPayload(), SecurityUtils.createHeaders(routingServiceUsername, routingServicePassword)),
                    Void.class
            );
            return null;
        } catch (Exception e) {
            throw new MessageProcessingException(e.getMessage(), e);
        }
    }
}
