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
package org.openwms.common.comm.synq.tcp;

import org.openwms.common.comm.CommConstants;
import org.openwms.common.comm.CommonMessageFactory;
import org.openwms.common.comm.MessageMapper;
import org.openwms.common.comm.MessageMismatchException;
import org.openwms.common.comm.synq.TimesyncRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Map;

import static org.openwms.common.comm.CommHeader.LENGTH_HEADER;
import static org.openwms.common.comm.ParserUtils.asDate;
import static org.openwms.common.comm.Payload.DATE_LENGTH;

/**
 * A TimesyncTelegramMapper.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
@Component
class TimesyncTelegramMapper implements MessageMapper<TimesyncRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimesyncTelegramMapper.class);
    private static final Logger TELEGRAM_LOGGER = LoggerFactory.getLogger(CommConstants.CORE_INTEGRATION_MESSAGING);

    /**
     * {@inheritDoc}
     */
    @Override
    public Message<TimesyncRequest> mapTo(String telegram, Map<String, Object> headers) {
        if (TELEGRAM_LOGGER.isDebugEnabled()) {
            TELEGRAM_LOGGER.debug("Incoming: [{}]", telegram);
        }
        int startSendertime = LENGTH_HEADER + forType().length();
        TimesyncRequest request = new TimesyncRequest();
        try {
            request.setSenderTimer(asDate(telegram.substring(startSendertime, startSendertime + DATE_LENGTH)));
            GenericMessage<TimesyncRequest> result =
                    new GenericMessage<>(request, CommonMessageFactory.createHeaders(telegram, headers));
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
