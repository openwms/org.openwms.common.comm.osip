/*
 * Copyright 2005-2020 the original author or authors.
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
package org.openwms.common.comm.osip.synq;

import org.openwms.common.comm.Channels;
import org.openwms.common.comm.TimeProvider;
import org.openwms.common.comm.osip.CommonMessageFactory;
import org.openwms.common.comm.osip.OSIPComponent;
import org.openwms.common.comm.osip.OSIPHeader;
import org.openwms.common.comm.osip.ResponseHeader;
import org.openwms.common.comm.tcp.ConnectionHolder;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.ip.IpHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.support.MutableMessageHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;

import java.util.function.Function;

/**
 * A TimesyncHandler.
 *
 * @author Heiko Scherrer
 */
@OSIPComponent
class TimesyncHandler implements Function<GenericMessage<TimesyncRequest>, Void> {

    private final Channels channels;
    private final ConnectionHolder connectionHolder;
    private final TimeProvider timeProvider;

    TimesyncHandler(Channels channels, ConnectionHolder connectionHolder, TimeProvider timeProvider) {
        this.channels = channels;
        this.connectionHolder = connectionHolder;
        this.timeProvider = timeProvider;
    }

    /**
     * Builds response message with the current time and the same request header to preserve header information (seq. number etc.) in post
     * transformation steps.
     *
     * @param timesyncRequest the generic technology agnostic message object
     * @return the response
     */
    @Override
    public Void apply(GenericMessage<TimesyncRequest> timesyncRequest) {

        TimesyncResponse payload =
                new TimesyncResponse.Builder()
                        .senderTime(timeProvider.now())
                        .header(ResponseHeader.newBuilder()
                                .receiver((String) timesyncRequest.getHeaders().get(OSIPHeader.SENDER_FIELD_NAME))
                                .sender((String) timesyncRequest.getHeaders().get(OSIPHeader.RECEIVER_FIELD_NAME))
                                .sequenceNo(Short.valueOf(String.valueOf(timesyncRequest.getHeaders().get(OSIPHeader.SEQUENCE_FIELD_NAME))))
                                .build()
                        )
                        .build();

        MessageHeaders headers = new MutableMessageHeaders(CommonMessageFactory.getOSIPHeaders(timesyncRequest));
        String sender = headers.get(OSIPHeader.SENDER_FIELD_NAME, String.class);
        headers.put(OSIPHeader.SENDER_FIELD_NAME, headers.get(OSIPHeader.RECEIVER_FIELD_NAME));
        headers.put(OSIPHeader.RECEIVER_FIELD_NAME, sender);
        headers.put(IpHeaders.CONNECTION_ID, connectionHolder.getConnectionId(sender));

        Message<TimesyncResponse> result =
                MessageBuilder
                        .withPayload(payload)
                        .setReplyChannelName("inboundChannel")
                        .copyHeaders(headers)
                        .build();

        new MessagingTemplate(channels.getOutboundChannel((String) headers.get(OSIPHeader.RECEIVER_FIELD_NAME)))
                .send(result);
        return null;
    }
}
