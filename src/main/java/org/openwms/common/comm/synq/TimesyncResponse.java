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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.openwms.common.comm.ParserUtils;
import org.openwms.common.comm.Payload;

import java.io.Serializable;
import java.util.Date;

/**
 * A TimesyncResponse.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Data
@Builder
@Slf4j
@AllArgsConstructor
class TimesyncResponse extends Payload implements Serializable {

    /** Message identifier {@value} . */
    public static final String IDENTIFIER = "SYNC";
    private Date senderTimer;

    /**
     * Create a new CommonMessage.
     */
    TimesyncResponse() {
        super();
        senderTimer = new Date();
    }

    /**
     * Subclasses have to return an unique, case-sensitive message identifier.
     *
     * @return The message TYPE field (see OSIP specification)
     */
    @Override
    public String getMessageIdentifier() {
        return IDENTIFIER;
    }

    /**
     * Does this type of message needs to be replied to?
     *
     * @return {@literal true} no reply needed, otherwise {@literal false}
     */
    @Override
    public boolean isWithoutReply() {
        return false;
    }

    @Override
    public String asString() {
        return IDENTIFIER + ParserUtils.asString(senderTimer);
    }

    @Override
    public String toString() {
        return IDENTIFIER + ParserUtils.asString(senderTimer);
    }
}
