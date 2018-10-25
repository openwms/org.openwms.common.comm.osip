/*
 * openwms.org, the Open Warehouse Management System.
 * Copyright (C) 2018 Heiko Scherrer
 *
 * This file is part of openwms.org.
 *
 * openwms.org is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * openwms.org is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openwms.common.comm.res;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;

/**
 * A ResponseMessageHandler.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@MessageEndpoint("responseMessageHandler")
class ResponseMessageHandler {

    private final MessageChannel channel;

    ResponseMessageHandler(@Qualifier("enrichedOutboundChannel") MessageChannel channel) {
        this.channel = channel;
    }

    public void handleRES(ResponseMessage res) {
        MessagingTemplate template = new MessagingTemplate();
        Message<ResponseMessage> message =
                MessageBuilder
                        .withPayload(res)
                        .copyHeaders(res.getHeader().getAll())
                .setHeader(MessageHeaders.REPLY_CHANNEL, "inboundChannel")
                .build();
        template.send(channel, message);
    }
}
