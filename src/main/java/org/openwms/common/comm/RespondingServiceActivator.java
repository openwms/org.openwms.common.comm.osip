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

import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

/**
 * A RespondingServiceActivator is a Service Activator implementation that accepts a
 * message and replies with a response message.
 *
 * @param <T> Type of incoming message that is being processed
 * @param <U> Type of outgoing message that is returned
 * @author Heiko Scherrer
 */
public interface RespondingServiceActivator<T, U> extends CustomServiceActivator {

    /**
     * Wake up a service, processor or bean that accepts incoming messages of type {@code T}
     * and returns messages of type {@code U}.
     *
     * @param message The message to forward
     * @return The response returned from the service
     */
    Message<U> wakeUp(GenericMessage<T> message);
}
