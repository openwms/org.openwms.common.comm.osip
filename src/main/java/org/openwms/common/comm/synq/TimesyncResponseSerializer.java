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
package org.openwms.common.comm.synq;

import org.openwms.common.comm.CommConstants;
import org.openwms.common.comm.CommHeader;
import org.openwms.common.comm.MessageMismatchException;
import org.openwms.common.comm.tcp.OSIPSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static java.lang.String.format;
import static org.openwms.common.comm.ParserUtils.padRight;

/**
 * A TimesyncResponseSerializer.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Component
class TimesyncResponseSerializer implements OSIPSerializer<TimesyncResponse> {

    private final short maxTelegramLength;
    private final String syncField;

    public TimesyncResponseSerializer(@Value("${owms.driver.server.so-send-buffer-size}") short maxTelegramLength,
            @Value("${owms.driver.osip.sync-field}") String syncField) {
        this.maxTelegramLength = maxTelegramLength;
        this.syncField = syncField;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageIdentifier() {
        return TimesyncResponse.IDENTIFIER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String serialize(TimesyncResponse obj) {

        CommHeader header = CommHeader.builder()
                .sync(syncField)
                .messageLength(maxTelegramLength)
                .sender(obj.getHeader().getSender())
                .receiver(obj.getHeader().getReceiver())
                .sequenceNo(obj.getHeader().getSequenceNo()+1)
                .build();
        String s = header + obj.asString();
        if (s.length() > maxTelegramLength) {
            throw new MessageMismatchException(format("Defined telegram length exceeds configured size of owms.driver.server.so-send-buffer-size=[%d]. Actual length is [%d]", maxTelegramLength, s.length()));
        }
        return padRight(s, CommConstants.TELEGRAM_LENGTH, CommConstants.TELEGRAM_FILLER_CHARACTER);
    }
}
