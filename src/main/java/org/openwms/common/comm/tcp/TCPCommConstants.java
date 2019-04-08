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
package org.openwms.common.comm.tcp;

import org.openwms.common.comm.Payload;

import static org.openwms.common.comm.CommHeader.LENGTH_HEADER;

/**
 * A TCPCommConstants.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
public final class TCPCommConstants {

    private TCPCommConstants() {
    }

    /**
     * A CommonMessage is able to define the type of message from the telegram String. Currently the type identifier starts directly after
     * the header and has a length of 4 characters.
     *
     * @param telegram The telegram String to resolve the type for
     * @return The telegram type as case-insensitive String
     */
    public static String getTelegramType(String telegram) {
        short headerLength = LENGTH_HEADER;
        return telegram.substring(headerLength, headerLength + Payload.MESSAGE_IDENTIFIER_LENGTH);
    }
}
