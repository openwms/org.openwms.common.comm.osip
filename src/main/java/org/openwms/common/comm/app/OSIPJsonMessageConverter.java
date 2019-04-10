/*
 * Copyright 2019 Heiko Scherrer
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
package org.openwms.common.comm.app;

import org.openwms.common.comm.CommHeader;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.messaging.support.GenericMessage;

import java.util.Map;

/**
 * A OSIPJsonMessageConverter.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
class OSIPJsonMessageConverter extends Jackson2JsonMessageConverter {

    @Override
    protected Message createMessage(Object objectToConvert, MessageProperties messageProperties)
            throws MessageConversionException {

        if (objectToConvert instanceof GenericMessage) {
            GenericMessage gm = (GenericMessage) objectToConvert;
            for (Map.Entry<String, Object> entry : gm.getHeaders().entrySet()) {
                if (entry.getKey().startsWith(CommHeader.PREFIX)) {
                    messageProperties.setHeader(entry.getKey(), entry.getValue());
                }
            }
        }

        return super.createMessage(objectToConvert, messageProperties);
    }
}
