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
package org.openwms.common.comm.osip.synq.tcp;

import org.openwms.common.comm.CommConstants;
import org.openwms.common.comm.MessageMismatchException;
import org.openwms.common.comm.config.Driver;
import org.openwms.common.comm.osip.CommonMessageFactory;
import org.openwms.common.comm.osip.OSIPComponent;
import org.openwms.common.comm.osip.synq.TimesyncRequest;
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

/**
 * A TimesyncTelegramDeserializer deserializes OSIP SYNC telegram String into
 * {@link TimesyncRequest}s.
 *
 * @author Heiko Scherrer
 * @see TimesyncRequest
 */
@OSIPComponent
class TimesyncTelegramDeserializer implements TelegramDeserializer<TimesyncRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimesyncTelegramDeserializer.class);
    private static final Logger TELEGRAM_LOGGER = LoggerFactory.getLogger(CommConstants.CORE_INTEGRATION_MESSAGING);
    private final Driver driver;

    TimesyncTelegramDeserializer(Driver driver) {
        this.driver = driver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message<TimesyncRequest> deserialize(String telegram, Map<String, Object> headers) {
        if (TELEGRAM_LOGGER.isDebugEnabled()) {
            TELEGRAM_LOGGER.debug("Incoming: [{}]", telegram);
        }
        int startSendertime = LENGTH_HEADER + forType().length();
        TimesyncRequest request = new TimesyncRequest();
        try {
            request.setSenderTimer(
                    new SimpleDateFormat(driver
                            .getOsip().getDatePattern())
                            .parse(telegram.substring(startSendertime, startSendertime + DATE_LENGTH))
            );

            GenericMessage<TimesyncRequest> result =
                    new GenericMessage<>(
                            request,
                            CommonMessageFactory.createHeaders(telegram, headers)
                    );

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Transformed telegram into TimesyncRequest message: [{}]", result);
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
        return TimesyncRequest.IDENTIFIER;
    }
}
