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
package org.openwms.common.comm.osip.sysu.tcp;

import org.ameba.exception.NotFoundException;
import org.openwms.common.comm.CommConstants;
import org.openwms.common.comm.MessageMismatchException;
import org.openwms.common.comm.config.Driver;
import org.openwms.common.comm.osip.CommonMessageFactory;
import org.openwms.common.comm.osip.OSIPComponent;
import org.openwms.common.comm.osip.sysu.SystemUpdateMessage;
import org.openwms.common.comm.spi.FieldLengthProvider;
import org.openwms.common.comm.tcp.TelegramDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import java.text.ParseException;
import java.util.Map;

import static java.lang.String.format;
import static org.openwms.common.comm.osip.OSIPHeader.LENGTH_HEADER;
import static org.openwms.common.comm.osip.Payload.DATE_LENGTH;
import static org.openwms.common.comm.osip.Payload.ERROR_CODE_LENGTH;

/**
 * A SystemUpdateTelegramDeserializer deserializes OSIP SYSU telegram String into
 * {@link SystemUpdateMessage}s.
 *
 * @author Heiko Scherrer
 * @see SystemUpdateMessage
 */
@OSIPComponent
class SystemUpdateTelegramDeserializer implements TelegramDeserializer<SystemUpdateMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemUpdateTelegramDeserializer.class);
    private static final Logger TELEGRAM_LOGGER = LoggerFactory.getLogger(CommConstants.CORE_INTEGRATION_MESSAGING);
    @Autowired(required = false)
    private FieldLengthProvider provider;
    private final Driver driver;

    SystemUpdateTelegramDeserializer(Driver driver) {
        this.driver = driver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message<SystemUpdateMessage> deserialize(String telegram, Map<String, Object> headers) {
        if (TELEGRAM_LOGGER.isDebugEnabled()) {
            TELEGRAM_LOGGER.debug("Incoming: [{}]", telegram);
        }
        if (provider == null) {
            throw new NotFoundException(format("Telegram handling [%s] not supported", SystemUpdateMessage.IDENTIFIER));
        }
        int startLocationGroup = LENGTH_HEADER + forType().length();
        int startErrorCode = startLocationGroup + provider.lengthLocationGroupName();
        int startCreateDate = startErrorCode + ERROR_CODE_LENGTH;

        try {
            GenericMessage<SystemUpdateMessage> result =
                new GenericMessage<>(
                    new SystemUpdateMessage.Builder()
                        .withLocationGroupName(telegram.substring(startLocationGroup, startErrorCode))
                        .withErrorCode(telegram.substring(startErrorCode, startCreateDate))
                        .withCreateDate(
                                telegram.substring(startCreateDate, startCreateDate + DATE_LENGTH),
                                driver.getOsip().getDatePattern()
                        )
                        .build(),
                        CommonMessageFactory.createHeaders(telegram, headers)
                );
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Transformed telegram into SystemUpdateMessage message: [{}]", result);
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
        return SystemUpdateMessage.IDENTIFIER;
    }
}
