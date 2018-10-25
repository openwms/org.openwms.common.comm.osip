/*
 * openwms.org, the Open Warehouse Management System.
 * Copyright (C) 2014 Heiko Scherrer
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
                .setReplyChannelName("outboundChannel")
                .copyHeaders(timesyncRequest.getHeaders())
                .build();
        return result;
    }
}
