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
import org.openwms.common.comm.MessageMismatchException;
import org.openwms.common.comm.ParserUtils;
import org.openwms.common.comm.config.Driver;
import org.openwms.common.comm.osip.CommonMessageFactory;
import org.openwms.common.comm.osip.OSIPComponent;
import org.openwms.common.comm.osip.err.ErrorMessage;
import org.openwms.common.comm.spi.FieldLengthProvider;
import org.openwms.common.comm.tcp.TelegramDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import static org.openwms.common.comm.osip.OSIPHeader.LENGTH_HEADER;
import static org.openwms.common.comm.osip.Payload.DATE_LENGTH;
import static org.openwms.common.comm.osip.Payload.ERROR_CODE_LENGTH;

/**
 * A ErrorTelegramDeserializer deserializes OSIP ERR telegram String into
 * {@link ErrorMessage}s.
 *
 * @author <a href="mailto:hscherrer@openwms.org">Heiko Scherrer</a>
 * @see ErrorMessage
 */
@OSIPComponent
class ErrorTelegramDeserializer implements TelegramDeserializer<ErrorMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorTelegramDeserializer.class);
    private static final Logger TELEGRAM_LOGGER = LoggerFactory.getLogger(CommConstants.CORE_INTEGRATION_MESSAGING);
    private final Driver driver;
    private final FieldLengthProvider lengthProvider;

    ErrorTelegramDeserializer(Driver driver, FieldLengthProvider lengthProvider) {
        this.driver = driver;
        this.lengthProvider = lengthProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message<ErrorMessage> deserialize(String telegram, Map<String, Object> headers) {
        if (TELEGRAM_LOGGER.isDebugEnabled()) {
            TELEGRAM_LOGGER.debug("Incoming: [{}]", telegram);
        }
        int startLocationGroup = LENGTH_HEADER + forType().length();
        int startErrorCode = startLocationGroup + lengthProvider.lengthLocationGroupName();
        int startCreateDate = startErrorCode + ERROR_CODE_LENGTH;
        try {
            GenericMessage<ErrorMessage> result =
                new GenericMessage<>(
                    ErrorMessage.newBuilder()
                        .errorCode(telegram.substring(startErrorCode, startCreateDate))
                        .locationGroupName(ParserUtils.trimRight(telegram.substring(startLocationGroup, startErrorCode), '*'))
                        .created(
                            new SimpleDateFormat(driver
                                .getOsip().getDatePattern())
                                .parse(telegram.substring(startCreateDate, startCreateDate + DATE_LENGTH))
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
