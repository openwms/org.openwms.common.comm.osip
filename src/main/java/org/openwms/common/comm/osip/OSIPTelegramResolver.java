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
import org.openwms.common.comm.TelegramResolver;
import org.springframework.stereotype.Component;

import static org.openwms.common.comm.osip.OSIPHeader.LENGTH_HEADER;

/**
 * A OSIPTelegramResolver.
 *
 * @author Heiko Scherrer
 */
@Component
class OSIPTelegramResolver implements TelegramResolver {

    /**
     * A CommonMessage is able to define the type of message from the telegram String. Currently, the type identifier starts directly after
     * the header and has a length of 4 characters.
     *
     * @param telegram The telegram String to resolve the type for
     * @return The telegram type as case-insensitive String
     */
    public String getTelegramType(String telegram) {
        if (telegram == null || telegram.length() < LENGTH_HEADER) {
            throw new MessageMismatchException("Received an invalid OSIP telegram type");
        }
        return telegram.substring(LENGTH_HEADER, LENGTH_HEADER + Payload.MESSAGE_IDENTIFIER_LENGTH);
    }
}
