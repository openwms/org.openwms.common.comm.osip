/*
 * Copyright 2005-2025 the original author or authors.
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

import org.openwms.common.comm.osip.OSIPComponent;
import org.openwms.common.comm.osip.OSIPHeader;
import org.openwms.core.SpringProfiles;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.support.GenericMessage;

import java.util.function.Function;

/**
 * An AmqpErrorMessageHandler is the handler function to accept {@link ErrorMessage}s and
 * forward them for processing over AMQP.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@OSIPComponent
@RefreshScope
class AmqpErrorMessageHandler implements Function<GenericMessage<ErrorMessage>, Void> {

    private final AmqpTemplate amqpTemplate;
    private final String exchangeName;
    private final String routingKey;

    AmqpErrorMessageHandler(AmqpTemplate amqpTemplate,
            @Value("${owms.driver.osip.err.exchange-name}") String exchangeName,
            @Value("${owms.driver.osip.err.routing-key-out}") String routingKey) {
        this.amqpTemplate = amqpTemplate;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void apply(GenericMessage<ErrorMessage> msg) {
        msg.getPayload().getHeader().setReceiver((String) msg.getHeaders().get(OSIPHeader.RECEIVER_FIELD_NAME));
        msg.getPayload().getHeader().setSender((String) msg.getHeaders().get(OSIPHeader.SENDER_FIELD_NAME));
        msg.getPayload().getHeader().setSequenceNo((Short) msg.getHeaders().get(OSIPHeader.SEQUENCE_FIELD_NAME));
        msg.getPayload().getHeader().setTenant((String) msg.getHeaders().get(OSIPHeader.TENANT_FIELD_NAME));
        amqpTemplate.convertAndSend(exchangeName, routingKey, msg.getPayload());
        return null;
    }
}
