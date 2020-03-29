/*
 * Copyright 2005-2020 the original author or authors.
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

import org.springframework.messaging.support.GenericMessage;

/**
 * A NotRespondingServiceActivator is an Service Activator implementation that accepts a
 * message but does not reply with a message.
 *
 * @param <T> A type of incoming payload
 * @author Heiko Scherrer
 */
public interface NotRespondingServiceActivator<T> extends CustomServiceActivator {

    /**
     * Wake up a service, processor or bean that accepts incoming messages of type {@code T}.
     *
     * @param message The message to forward
     */
    void wakeUp(GenericMessage<T> message);
}
