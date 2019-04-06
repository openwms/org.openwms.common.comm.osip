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
package org.openwms.common.comm.transformer.tcp;

import org.openwms.common.comm.CommConstants;
import org.openwms.common.comm.MessageMismatchException;
import org.openwms.common.comm.Payload;
import org.openwms.common.comm.api.MessageMapper;
import org.openwms.common.comm.tcp.TCPCommConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Headers;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * A TelegramTransformer transforms incoming String telegram structures to {@link Payload}s.
 * Therefor it delegates to an appropriate MessageMapper instance that is able to map the
 * incoming telegram String into a supported Java message type. This mechanism can be
 * easily extended by putting new bean instances of {@link MessageMapper} to the classpath.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 * @see MessageMapper
 */
@MessageEndpoint("telegramTransformer")
public class TelegramTransformer<T extends Payload> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramTransformer.class);
    private final List<MessageMapper<T>> mappers;
    private final String tenant;
    private Map<String, MessageMapper<T>> mappersMap;

    public TelegramTransformer(List<MessageMapper<T>> mappers, @Value("${owms.tenant:spring.application.name}") String tenant) {
        this.mappers = mappers;
        this.tenant = tenant;
    }

    @PostConstruct
    void onPostConstruct() {
        mappersMap = mappers.stream().collect(Collectors.toMap(MessageMapper::forType, m -> m));
    }

    /**
     * Transformer method to transform a telegram String {@code telegram} into a {@link Payload}.
     *
     * @param telegram The incoming telegram String
     * @return The {@link Payload} is transformable
     * @throws MessageMismatchException if no appropriate type was found.
     */
    @Transformer
    public Message<T> transform(String telegram, @Headers Map<String, Object> headers) {
        if (telegram == null || telegram.isEmpty()) {
            LOGGER.info("Received telegram was null or of length == 0, just skip");
            return null;
        }
        String telegramType = TCPCommConstants.getTelegramType(telegram);
        MDC.put(CommConstants.LOG_TELEGRAM_TYPE, telegramType);
        MDC.put(CommConstants.LOG_TENANT, tenant);
        MessageMapper<T> mapper = mappersMap.get(telegramType);
        if (mapper == null) {
            LOGGER.error("No mapper found for telegram type [{}]", TCPCommConstants.getTelegramType(telegram));
            throw new MessageMismatchException(format("No mapper found for telegram type [%s]", TCPCommConstants.getTelegramType(telegram)));
        }
        return mapper.mapTo(telegram, headers);
    }
}
