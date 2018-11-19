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
package org.openwms.common.comm.api;

import org.openwms.common.comm.Payload;
import org.springframework.messaging.Message;

import java.util.Map;

/**
 * A MessageMapper is able to map from a String telegram to a {@link Payload}.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
public interface MessageMapper<T> {

    /**
     * Investigate the telegram String {@code telegram} and retrieve from the telegram type a subtype of {@link
     * Payload CommonMessage}. Implementations probably throw some kind of RuntimeExceptions if no telegram
     * type was found. An implementation can expect that the caller has checked the telegram length.
     *
     * @param telegram The telegram String to investigate
     * @param headers A map of the underlying protocol headers
     * @return The mapped CommonMessage
     */
    Message<T> mapTo(String telegram, Map<String, Object> headers);

    /**
     * Return the telegram type, this mapper is responsible for.
     *
     * @return the telegram type as String
     */
    String forType();
}
