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
package org.openwms.common.comm.osip.synq;

import org.openwms.common.comm.osip.Payload;
import org.openwms.common.comm.osip.ResponseHeader;

import java.io.Serializable;
import java.util.Date;

/**
 * A TimesyncResponse.
 *
 * @author <a href="mailto:hscherrer@openwms.org">Heiko Scherrer</a>
 */
public class TimesyncResponse extends Payload implements Serializable {

    /** Message identifier {@value} . */
    public static final String IDENTIFIER = "SYNC";
    private Date senderTime;

    /*~------------ Constructors ------------*/
    TimesyncResponse() {
        super();
        senderTime = new Date();
    }

    private TimesyncResponse(Builder builder) {
        setHeader(builder.header);
        setErrorCode(builder.errorCode);
        setCreated(builder.created);
        senderTime = builder.senderTime;
    }

    /*~------------ Accessors ------------*/
    public Date getSenderTime() {
        return senderTime;
    }

    /*~------------ Builders ------------*/
    public static final class Builder {
        private ResponseHeader header;
        private String errorCode;
        private Date created;
        private Date senderTime;

        public Builder() {
        }

        public Builder header(ResponseHeader val) {
            header = val;
            return this;
        }

        public Builder errorCode(String val) {
            errorCode = val;
            return this;
        }

        public Builder created(Date val) {
            created = val;
            return this;
        }

        public Builder senderTime(Date val) {
            senderTime = val;
            return this;
        }

        public TimesyncResponse build() {
            return new TimesyncResponse(this);
        }
    }

    /*~------------ Overrides ------------*/
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
}
