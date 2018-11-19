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
package org.openwms.common.comm.res;

import org.ameba.annotation.Measured;
import org.openwms.common.comm.CommHeader;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

/**
 * A ResController.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@MessageEndpoint("responseMessageServiceActivator")
public class ResponseMessageServiceActivator {

    @Measured
    public Message<ResponseMessage> transform(ResponseMessage in) {
        return MessageBuilder
                .withPayload(in)
                .setHeader(CommHeader.RECEIVER_FIELD_NAME, in.getHeader().getReceiver())
                .setHeader(CommHeader.SENDER_FIELD_NAME, in.getHeader().getSender())
                .setHeader(CommHeader.SEQUENCE_FIELD_NAME, in.getHeader().getSequenceNo())
                .setReplyChannelName("outboundChannel")
                .build();
    }

}
