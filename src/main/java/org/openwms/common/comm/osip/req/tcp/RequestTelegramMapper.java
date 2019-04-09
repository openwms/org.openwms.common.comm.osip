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
package org.openwms.common.comm.osip.req.tcp;

import org.openwms.common.comm.CommonMessageFactory;
import org.openwms.common.comm.MessageMapper;
import org.openwms.common.comm.MessageMismatchException;
import org.openwms.common.comm.Payload;
import org.openwms.common.comm.app.Driver;
import org.openwms.common.comm.osip.req.RequestMessage;
import org.openwms.common.comm.osip.req.spi.RequestFieldLengthProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Map;

import static java.lang.String.format;
import static org.openwms.common.comm.CommHeader.LENGTH_HEADER;

/**
 * A RequestTelegramMapper tries to map a telegram String to a {@link RequestMessage}.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
@Component
class RequestTelegramMapper implements MessageMapper<RequestMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestTelegramMapper.class);
    @Autowired(required = false)
    private RequestFieldLengthProvider provider;
    private final Driver driver;

    RequestTelegramMapper(Driver driver) {
        this.driver = driver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message<RequestMessage> mapTo(String telegram, Map<String, Object> headers) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Telegram to transform: [{}]", telegram);
        }
        if (provider == null) {
            throw new MessageMismatchException(format("Telegram handling [%s] not supported", RequestMessage.IDENTIFIER));
        }
        int startPayload = LENGTH_HEADER + forType().length();
        int startActualLocation = startPayload + provider.barcodeLength();
        int startTargetLocation = startActualLocation + provider.locationIdLength();
        int startErrorCode = startTargetLocation + provider.locationIdLength();
        int startCreateDate = startErrorCode + Payload.ERROR_CODE_LENGTH;

        RequestMessage message;
        try {
            message = new RequestMessage.Builder(provider)
                    .withBarcode(telegram.substring(startPayload, startActualLocation))
                    .withActualLocation(telegram.substring(startActualLocation, startTargetLocation))
                    .withTargetLocation(telegram.substring(startTargetLocation, startErrorCode))
                    .withErrorCode(telegram.substring(startErrorCode, startCreateDate))
                    .withCreateDate(
                            telegram.substring(startCreateDate, startCreateDate + Payload.DATE_LENGTH),
                            driver.getOsip().getDatePattern()
                    ).build();
            return new GenericMessage<>(message, CommonMessageFactory.createHeaders(telegram, headers));
        } catch (ParseException e) {
            throw new MessageMismatchException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String forType() {
        return RequestMessage.IDENTIFIER;
    }
}
