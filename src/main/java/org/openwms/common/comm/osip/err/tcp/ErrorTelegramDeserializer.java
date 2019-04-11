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
package org.openwms.common.comm.osip.err.tcp;

import org.openwms.common.comm.CommConstants;
import org.openwms.common.comm.CommonMessageFactory;
import org.openwms.common.comm.MessageMismatchException;
import org.openwms.common.comm.app.Driver;
import org.openwms.common.comm.osip.err.ErrorMessage;
import org.openwms.common.comm.tcp.TelegramDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Map;

import static org.openwms.common.comm.CommHeader.LENGTH_HEADER;
import static org.openwms.common.comm.Payload.DATE_LENGTH;
import static org.openwms.common.comm.Payload.ERROR_CODE_LENGTH;

/**
 * A ErrorTelegramDeserializer deserializes OSIP ERR telegram String into
 * {@link ErrorMessage}s.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 * @see ErrorMessage
 */
@Component
class ErrorTelegramDeserializer implements TelegramDeserializer<ErrorMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorTelegramDeserializer.class);
    private static final Logger TELEGRAM_LOGGER = LoggerFactory.getLogger(CommConstants.CORE_INTEGRATION_MESSAGING);
    private final Driver driver;

    ErrorTelegramDeserializer(Driver driver) {
        this.driver = driver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message<ErrorMessage> deserialize(String telegram, Map<String, Object> headers) {
        if (TELEGRAM_LOGGER.isDebugEnabled()) {
            TELEGRAM_LOGGER.debug("Telegram to transform: [{}]", telegram);
        }
        int startPayload = LENGTH_HEADER + forType().length();
        int startCreateDate = startPayload + ERROR_CODE_LENGTH;
        try {
            GenericMessage<ErrorMessage> result =
                new GenericMessage<>(
                    new ErrorMessage.Builder()
                        .withErrorCode(telegram.substring(startPayload, startCreateDate))
                        .withCreateDate(
                            telegram.substring(startCreateDate, startCreateDate + DATE_LENGTH),
                            driver.getOsip().getDatePattern()
                        )
                        .build(),
                        CommonMessageFactory.createHeaders(telegram, headers)
                );
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Transformed telegram into ErrorMessage message: [{}]", result);
            }
            return result;
        } catch (ParseException e) {
            throw new MessageMismatchException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String forType() {
        return ErrorMessage.IDENTIFIER;
    }
}
