/*
 * Copyright 2005-2025 the original author or authors.
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
package org.openwms.common.comm.osip;

import org.openwms.common.comm.MessageMismatchException;
import org.openwms.common.comm.config.Osip;

import static java.lang.String.format;
import static org.openwms.common.comm.ParserUtils.padRight;

/**
 * A OSIPSerializer is able to serialize OSIP messages into Strings that can be sent over
 * the wire.
 *
 * see https://interface21-io.gitbook.io/osip/
 *
 * @author Heiko Scherrer
 */
public abstract class OSIPSerializer<T extends Payload> {

    private final Osip driver;

    protected OSIPSerializer(Osip driver) {
        this.driver = driver;
    }

    /**
     * Subclasses have to return an unique, case-sensitive message identifier.
     *
     * @return The message TYPE field (see OSIP specification)
     */
    public abstract String getMessageIdentifier();

    /**
     * Serialize the given object {@code obj} into a String.
     *
     * @param obj The message object to serialize
     * @return The telegram String
     */
    public String serialize(T obj) {
        short maxTelegramLength = driver.getTelegramLength();
        OSIPHeader header = new OSIPHeader.Builder()
                .sync(driver.getSyncField())
                .messageLength(maxTelegramLength)
                .sender(obj.getHeader().getSender())
                .receiver(obj.getHeader().getReceiver())
                .sequenceNo(obj.getHeader().getSequenceNo())
                .build();
        String s = header + convert(obj);
        if (s.length() > maxTelegramLength) {
            throw new MessageMismatchException(format("Defined telegram length exceeds configured size of owms.driver.osip.telegram-length=[%d]. Actual length is [%d]", maxTelegramLength, s.length()));
        }
        return padRight(s, maxTelegramLength, driver.getTelegramFiller());
    }

    protected abstract String convert(T message);

    protected Osip getDriver() {
        return driver;
    }
}
