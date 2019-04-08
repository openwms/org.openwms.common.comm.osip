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
package org.openwms.common.comm.err.tcp;

import org.openwms.common.comm.CommConstants;
import org.openwms.common.comm.CommHeader;
import org.openwms.common.comm.MessageMismatchException;
import org.openwms.common.comm.app.Driver;
import org.openwms.common.comm.err.ErrorMessage;
import org.openwms.common.comm.tcp.OSIPSerializer;
import org.springframework.stereotype.Component;

import static java.lang.String.format;
import static org.openwms.common.comm.ParserUtils.padRight;

/**
 * A ErrorMessageSerializer.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Component
public class ErrorMessageSerializer implements OSIPSerializer<ErrorMessage> {

    private final Driver driver;

    public ErrorMessageSerializer(Driver driver) {
        this.driver = driver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageIdentifier() {
        return ErrorMessage.IDENTIFIER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String serialize(ErrorMessage obj) {
        short maxTelegramLength = driver.getOsip().getTelegramLength();
        CommHeader header = CommHeader.builder()
                .sync(driver.getOsip().getSyncField())
                .messageLength(maxTelegramLength)
                .sender(obj.getHeader().getSender())
                .receiver(obj.getHeader().getReceiver())
                .sequenceNo(obj.getHeader().getSequenceNo())
                .build();
        String s = header + obj.asString();
        if (s.length() > maxTelegramLength) {
            throw new MessageMismatchException(format("Defined telegram length exceeds configured size of owms.driver.osip.telegram-length=[%d]. Actual length is [%d]", maxTelegramLength, s.length()));
        }
        return padRight(s, maxTelegramLength, CommConstants.TELEGRAM_FILLER_CHARACTER);
    }
}
