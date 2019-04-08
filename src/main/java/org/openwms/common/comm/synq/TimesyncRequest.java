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

import org.openwms.common.comm.ParserUtils;
import org.openwms.common.comm.Payload;

import java.io.Serializable;
import java.util.Date;

/**
 * A TimesyncRequest.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
public class TimesyncRequest extends Payload implements Serializable {

    /** Message identifier {@value} . */
    public static final String IDENTIFIER = "SYNQ";
    private Date senderTimer;

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

    public void setSenderTimer(Date senderTimer) {
        this.senderTimer = senderTimer;
    }

    @Override
    public String toString() {
        return "TimesyncRequest{" +
                "senderTimer=" + senderTimer +
                "} " + super.toString();
    }
}
