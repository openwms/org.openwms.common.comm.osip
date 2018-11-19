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

import org.openwms.common.comm.CommHeader;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

/**
 * A TimesyncHandler.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Component
class TimesyncHandler implements Function<GenericMessage<TimesyncRequest>, Message<TimesyncResponse>> {

    /**
     * Builds response message with the current time and the same request header to preserve header information (seq. number etc.) in post
     * transformation steps.
     *
     * @param timesyncRequest the request
     * @return the response
     */
    @Override
    public Message<TimesyncResponse> apply(GenericMessage<TimesyncRequest> timesyncRequest) {

        TimesyncResponse payload = TimesyncResponse.builder().senderTimer(new Date()).build();
        payload.getHeader().setReceiver((String) timesyncRequest.getHeaders().get(CommHeader.RECEIVER_FIELD_NAME));
        payload.getHeader().setSender((String) timesyncRequest.getHeaders().get(CommHeader.SENDER_FIELD_NAME));
        payload.getHeader().setSequenceNo(Short.valueOf(String.valueOf(timesyncRequest.getHeaders().get(CommHeader.SEQUENCE_FIELD_NAME))));
        Message<TimesyncResponse> result = MessageBuilder
                .withPayload(payload)
                .setReplyChannelName("inboundChannel")
                .copyHeaders(timesyncRequest.getHeaders())
                .build();
        return result;
    }
}
