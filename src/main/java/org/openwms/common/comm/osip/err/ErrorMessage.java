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
package org.openwms.common.comm.osip.err;

import org.openwms.common.comm.Payload;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An ErrorMessage signals any error or failure situation from an external system and to external systems.
 * 
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
public class ErrorMessage extends Payload implements Serializable {

    /** Message identifier {@value} . */
    public static final String IDENTIFIER = "ERR_";

    public ErrorMessage() {
        super();
    }

    public String getType() {
        return IDENTIFIER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageIdentifier() {
        return IDENTIFIER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWithoutReply() {
        return true;
    }

    /**
     * A Builder.
     * 
     * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
     */
    public static class Builder {

        private final ErrorMessage message;

        /**
         * Create a new Builder.
         */
        public Builder() {
            this.message = new ErrorMessage();
        }

        /**
         * Add an error code to the message.
         * 
         * @param errorCode
         *            The error code
         * @return The builder
         */
        public Builder withErrorCode(String errorCode) {
            message.setErrorCode(errorCode);
            return this;
        }

        /**
         * Add the date of creation in an expected format.
         *
         * @param createDate The creation date as String
         * @param pattern The datetime pattern used for the date
         * @return The builder
         */
        public Builder withCreateDate(String createDate, String pattern) throws ParseException {
            message.setCreated(new SimpleDateFormat(pattern).parse(createDate));
            return this;
        }

        /**
         * Add a new instance of Date to the Message.
         * 
         * @return The builder
         */
        public Builder withCreateDate() {
            message.setCreated(new Date());
            return this;
        }

        /**
         * Build and return the Message.
         * 
         * @return The Message
         */
        public ErrorMessage build() {
            return message;
        }
    }
}
