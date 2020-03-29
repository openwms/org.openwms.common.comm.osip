/*
 * Copyright 2005-2020 the original author or authors.
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
import org.openwms.common.comm.osip.OSIPSerializer;
import org.openwms.common.comm.osip.Payload;
import org.openwms.common.comm.tcp.TelegramDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Headers;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * A TelegramTransformer transforms incoming String telegram structures to {@link Payload}s.
 * Therefor it delegates to an appropriate {@link TelegramDeserializer} instance that is able to
 * map the incoming telegram String into a supported Java message type. This mechanism can
 * be easily extended by putting new bean instances of {@link TelegramDeserializer} to the
 * classpath.
 *
 * @author Heiko Scherrer
 * @see TelegramDeserializer
 */
public class TelegramTransformer<T> implements Transformable<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramTransformer.class);
    private final List<TelegramDeserializer<T>> deserializers;
    private Map<String, TelegramDeserializer<T>> deserializersMap;

    public TelegramTransformer(@Autowired(required = false) List<TelegramDeserializer<T>> deserializers) {
        this.deserializers = deserializers;
    }

    @PostConstruct
    void onPostConstruct() {
        deserializersMap = deserializers == null ?
                Collections.emptyMap() :
                deserializers
                        .stream()
                        .collect(Collectors.toMap(TelegramDeserializer::forType, m -> m));
    }

    /**
     * Transformer method to transform a telegram String {@code telegram} into a {@link Payload}.
     *
     * @param telegram The incoming telegram String
     * @return The {@link Payload} is transformable
     * @throws MessageMismatchException if no appropriate type was found.
     */
    @Transformer
    @Override
    public Message<T> transform(String telegram, @Headers Map<String, Object> headers) {
        if (telegram == null || telegram.isEmpty()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Received telegram was null or of length == 0, just skip");
            }
            return null;
        }
        String telegramType = OSIPSerializer.getTelegramType(telegram);
        MDC.put(CommConstants.LOG_TELEGRAM_TYPE, telegramType);
        TelegramDeserializer<T> deserializer = deserializersMap.get(telegramType);
        if (deserializer == null) {
            throw new MessageMismatchException(format("No deserializer found for telegram type [%s]. Is that type supported?", OSIPSerializer.getTelegramType(telegram)));
        }
        return deserializer.deserialize(telegram, headers);
    }
}
