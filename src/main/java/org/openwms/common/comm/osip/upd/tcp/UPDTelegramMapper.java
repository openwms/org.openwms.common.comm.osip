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
package org.openwms.common.comm.osip.upd.tcp;

import org.openwms.common.comm.CommonMessageFactory;
import org.openwms.common.comm.MessageMapper;
import org.openwms.common.comm.MessageMismatchException;
import org.openwms.common.comm.app.Driver;
import org.openwms.common.comm.osip.upd.UpdateMessage;
import org.openwms.common.comm.osip.upd.spi.UpdateFieldLengthProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Map;

import static java.lang.String.format;
import static org.openwms.common.comm.CommHeader.LENGTH_HEADER;
import static org.openwms.common.comm.Payload.DATE_LENGTH;
import static org.openwms.common.comm.Payload.ERROR_CODE_LENGTH;

/**
 * A UPDTelegramMapper maps the incoming UPD telegram String into an object representation.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
@Component
class UPDTelegramMapper implements MessageMapper<UpdateMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UPDTelegramMapper.class);
    private final UpdateFieldLengthProvider provider;
    private final Driver driver;

    UPDTelegramMapper(UpdateFieldLengthProvider provider, Driver driver) {
        this.provider = provider;
        this.driver = driver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message<UpdateMessage> mapTo(String telegram, Map<String, Object> headers) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Telegram to transform: [{}]", telegram);
        }
        if (provider == null) {
            throw new RuntimeException(format("Telegram handling [%s] not supported", UpdateMessage.IDENTIFIER));
        }
        int startPayload = LENGTH_HEADER + forType().length();
        int startActualLocation = startPayload + provider.barcodeLength();
        int startErrorCode = startActualLocation + provider.locationIdLength();
        int startCreateDate = startErrorCode + ERROR_CODE_LENGTH;

        UpdateMessage message;
        try {
            message = new UpdateMessage.Builder(provider)
                    .withBarcode(telegram.substring(startPayload, startActualLocation))
                    .withActualLocation(telegram.substring(startActualLocation, startErrorCode))
                    .withErrorCode(telegram.substring(startErrorCode, startCreateDate))
                    .withCreateDate(
                            telegram.substring(startCreateDate, startCreateDate + DATE_LENGTH),
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
        return UpdateMessage.IDENTIFIER;
    }
}
