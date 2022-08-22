/*
 * Copyright 2005-2022 the original author or authors.
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
package org.openwms.common.comm.osip.upd;

import org.openwms.common.comm.osip.OSIPComponent;
import org.openwms.common.comm.osip.OSIPHeader;
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
 * A HttpUpdateMessageHandler forwards updates to the transportUnits services to the routing service.
 *
 * @author Heiko Scherrer
 */
@Profile("!"+ SpringProfiles.ASYNCHRONOUS_PROFILE)
@OSIPComponent
@RefreshScope
class HttpUpdateMessageHandler implements Function<GenericMessage<UpdateMessage>, Void> {

    private final RestTemplate restTemplate;
    private final String routingServiceName;
    private final String routingServiceProtocol;
    private final String routingServiceUsername;
    private final String routingServicePassword;

    HttpUpdateMessageHandler(
            RestTemplate restTemplate,
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

    static UpdateVO getRequest(GenericMessage<UpdateMessage> msg) {
        return new UpdateVO.Builder()
                .type("UPD_")
                .barcode(msg.getPayload().getBarcode())
                .actualLocation(msg.getPayload().getActualLocation())
                .errorCode(msg.getPayload().getErrorCode())
                .created(msg.getPayload().getCreated())
                .header(new UpdateVO.UpdateHeaderVO.Builder()
                        .receiver(msg.getHeaders().get(OSIPHeader.RECEIVER_FIELD_NAME, String.class))
                        .sender(msg.getHeaders().get(OSIPHeader.SENDER_FIELD_NAME, String.class))
                        .sequenceNo(""+msg.getHeaders().get(OSIPHeader.SEQUENCE_FIELD_NAME, Short.class))
                        .build())
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void apply(GenericMessage<UpdateMessage> msg) {
        restTemplate.exchange(
                routingServiceProtocol+"://"+routingServiceName+"/upd",
                HttpMethod.POST,
                new HttpEntity<>(getRequest(msg), SecurityUtils.createHeaders(routingServiceUsername, routingServicePassword)),
                Void.class
        );
        return null;
    }
}
