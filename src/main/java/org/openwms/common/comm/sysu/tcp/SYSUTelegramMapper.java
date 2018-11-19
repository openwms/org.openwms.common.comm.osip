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
package org.openwms.common.comm.sysu.tcp;

import org.openwms.common.comm.CommonMessageFactory;
import org.openwms.common.comm.MessageMismatchException;
import org.openwms.common.comm.api.MessageMapper;
import org.openwms.common.comm.sysu.SystemUpdateMessage;
import org.openwms.common.comm.sysu.spi.SystemUpdateFieldLengthProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Map;

import static org.openwms.common.comm.CommHeader.LENGTH_HEADER;
import static org.openwms.common.comm.Payload.DATE_LENGTH;
import static org.openwms.common.comm.Payload.ERROR_CODE_LENGTH;

/**
 * A SYSUTelegramMapper maps the incoming SYSU telegram String into an object representation.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Component
class SYSUTelegramMapper implements MessageMapper<SystemUpdateMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SYSUTelegramMapper.class);
    @Autowired(required = false)
    private SystemUpdateFieldLengthProvider provider;

    /**
     * {@inheritDoc}
     */
    @Override
    public Message<SystemUpdateMessage> mapTo(String telegram, Map<String, Object> headers) {
        LOGGER.debug("Telegram to transform: [{}]", telegram);
        if (provider == null) {
            throw new RuntimeException("Telegram handling " + SystemUpdateMessage.IDENTIFIER + " not supported");
        }
        int startLocationGroup = LENGTH_HEADER + forType().length();
        int startErrorCode = startLocationGroup + provider.lengthLocationGroupName();
        int startCreateDate = startErrorCode + ERROR_CODE_LENGTH;

        SystemUpdateMessage message;
        try {
            message = new SystemUpdateMessage.Builder()
                    .withLocationGroupName(telegram.substring(startLocationGroup, startErrorCode))
                    .withErrorCode(telegram.substring(startErrorCode, startCreateDate))
                    .withCreateDate(telegram.substring(startCreateDate, startCreateDate + DATE_LENGTH)).build();
            return new GenericMessage<>(message, CommonMessageFactory.createHeaders(telegram, headers));
        } catch (ParseException e) {
            throw new MessageMismatchException(e.getMessage());
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
