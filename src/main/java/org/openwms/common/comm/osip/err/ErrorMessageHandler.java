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

import org.openwms.common.comm.Channels;
import org.openwms.common.comm.osip.OSIP;
import org.openwms.common.comm.osip.OSIPHeader;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;

import java.util.Map;

/**
 * A ErrorMessageHandler.
 *
 * @author Heiko Scherrer
 */
@OSIP
@MessageEndpoint
class ErrorMessageHandler {

    private final Channels channels;

    ErrorMessageHandler(Channels channels) {
        this.channels = channels;
    }

    /**
     * Send the incoming ErrorMessage out over TCP/IP.
     *
     * @param msg The ErrorMessage to send
     * @param headers The headers to apply
     */
    public void handle(ErrorMessage msg, Map<String, String> headers) {
        MessageChannel channel = channels.getOutboundChannel(headers.get(OSIPHeader.SENDER_FIELD_NAME));
        MessagingTemplate template = new MessagingTemplate();
        Message<ErrorMessage> message =
            MessageBuilder
                .withPayload(msg)
                .copyHeaders(msg.getHeader().getAll())
            .setHeader(MessageHeaders.REPLY_CHANNEL, "inboundChannel")
            .build();
        template.send(channel, message);
    }
}
