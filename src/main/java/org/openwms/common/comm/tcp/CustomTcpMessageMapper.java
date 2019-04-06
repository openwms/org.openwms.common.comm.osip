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
package org.openwms.common.comm.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.ip.tcp.connection.TcpConnection;
import org.springframework.integration.ip.tcp.connection.TcpMessageMapper;
import org.springframework.integration.support.AbstractIntegrationMessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.util.Assert;

import static org.openwms.common.comm.CommConstants.CORE_INTEGRATION_MESSAGING;

/**
 * A CustomTcpMessageMapper.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
public class CustomTcpMessageMapper extends TcpMessageMapper {

    private final MessageConverter inboundMessageConverter;
    private final MessageConverter outboundMessageConverter;

    private static final Logger TELEGRAM_LOGGER = LoggerFactory.getLogger(CORE_INTEGRATION_MESSAGING);
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomTcpMessageMapper.class);

    public CustomTcpMessageMapper(MessageConverter inboundMessageConverter, MessageConverter outboundMessageConverter) {
        Assert.notNull(inboundMessageConverter, "'inboundMessageConverter' must not be null");
        Assert.notNull(outboundMessageConverter, "'outboundMessageConverter' must not be null");
        this.inboundMessageConverter = inboundMessageConverter;
        this.outboundMessageConverter = outboundMessageConverter;
    }

    @Override
    public Message<?> toMessage(TcpConnection connection) throws Exception {
        Object data = connection.getPayload();
        if (data != null) {
            Message<?> message = this.inboundMessageConverter.toMessage(data, null);
            AbstractIntegrationMessageBuilder<?> messageBuilder = this.getMessageBuilderFactory().fromMessage(message);
            this.addStandardHeaders(connection, messageBuilder);
            this.addCustomHeaders(connection, messageBuilder);
            return messageBuilder.build();
        } else {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Received null as payload from connection with id [{}]", connection.getConnectionId());
            }
            // Garbage in, garbage out
            return null;
        }
    }

    @Override
    public Object fromMessage(Message<?> message) {
        Object data = this.outboundMessageConverter.fromMessage(message, Object.class);
        if (TELEGRAM_LOGGER.isTraceEnabled()) {
            TELEGRAM_LOGGER.trace("Outgoing: [{}]", data);
        }
        return data;
    }
}
