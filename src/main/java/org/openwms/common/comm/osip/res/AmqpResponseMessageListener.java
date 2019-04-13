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
package org.openwms.common.comm.osip.res;

import org.ameba.annotation.Measured;
import org.openwms.common.comm.osip.OSIPComponent;
import org.openwms.core.SpringProfiles;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.support.GenericMessage;

import java.util.Map;

/**
 * A AmqpResponseMessageListener.
 *
 * @author <a href="mailto:hscherrer@openwms.org">Heiko Scherrer</a>
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@OSIPComponent
class AmqpResponseMessageListener {

    private final ResponseMessageHandler handler;

    AmqpResponseMessageListener(ResponseMessageHandler handler) {
        this.handler = handler;
    }

    @Measured
    @RabbitListener(queues = "${owms.driver.osip.res.queue-name}")
    void handle(GenericMessage<ResponseMessage> res, @Headers Map<String, String> headers) {
        try {
            handler.handle(res.getPayload(), headers);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
        }
    }
}
