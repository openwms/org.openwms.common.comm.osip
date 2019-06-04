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

import org.openwms.common.comm.Channels;
import org.openwms.common.comm.osip.OSIP;
import org.openwms.common.comm.tcp.ConnectionHolder;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.ip.IpHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;

/**
 * A ResponseMessageHandler.
 *
 * @author Heiko Scherrer
 */
@OSIP
@MessageEndpoint("responseMessageHandler")
class ResponseMessageHandler {

    private final Channels channels;
    private final ConnectionHolder connectionHolder;

    ResponseMessageHandler(Channels channels, ConnectionHolder connectionHolder) {
        this.channels = channels;
        this.connectionHolder = connectionHolder;
    }

    public void handle(ResponseMessage msg, String receiver) {
        MessageChannel channel = channels.getOutboundChannel(receiver);
        MessagingTemplate template = new MessagingTemplate();
        Message<ResponseMessage> message =
                MessageBuilder
                        .withPayload(msg)
                        .copyHeaders(msg.getHeader().getAll())
                .setHeader(MessageHeaders.REPLY_CHANNEL, "inboundChannel")
                .setHeader(IpHeaders.CONNECTION_ID, connectionHolder.getConnectionId(receiver))
                .build();
        template.send(channel, message);
    }
}
