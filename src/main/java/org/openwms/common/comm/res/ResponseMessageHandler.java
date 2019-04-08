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

import org.openwms.common.comm.CommHeader;
import org.openwms.common.comm.app.Channels;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;

import java.util.Map;

/**
 * A ResponseMessageHandler.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
@MessageEndpoint("responseMessageHandler")
class ResponseMessageHandler {

    private final Channels channels;

    ResponseMessageHandler(Channels channels) {
        this.channels = channels;
    }

    public void handle(ResponseMessage msg, Map<String, String> headers) {
        MessageChannel channel = channels.getOutboundChannel(headers.get(CommHeader.RECEIVER_FIELD_NAME));
        MessagingTemplate template = new MessagingTemplate();
        Message<ResponseMessage> message =
                MessageBuilder
                        .withPayload(msg)
                        .copyHeaders(msg.getHeader().getAll())
                .setHeader(MessageHeaders.REPLY_CHANNEL, "inboundChannel")
                .build();
        template.send(channel, message);
    }
}
