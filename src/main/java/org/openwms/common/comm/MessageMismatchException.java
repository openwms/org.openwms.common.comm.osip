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
package org.openwms.common.comm;

/**
 * A MessageMismatchException indicates that an incoming message is not in expected format.
 *
 * @author Heiko Scherrer
 */
public class MessageMismatchException extends MessageProcessingException {

    /**
     * Create a new MessageMismatchException.
     *
     * @param message Detail message
     * @param cause Cause to be propagated
     */
    public MessageMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create a new MessageMismatchException.
     *
     * @param message Detail message
     */
    public MessageMismatchException(String message) {
        super(message);
    }
}
