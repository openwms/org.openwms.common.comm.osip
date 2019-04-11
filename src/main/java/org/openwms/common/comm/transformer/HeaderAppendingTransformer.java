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
package org.openwms.common.comm.transformer;

import org.openwms.common.comm.osip.OSIPHeader;
import org.openwms.common.comm.osip.Payload;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageHeaderAccessor;

/**
 * A HeaderAppendingTransformer.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
@MessageEndpoint
public class HeaderAppendingTransformer {

    @Transformer
    public Message<Payload> transform(Message<Payload> msg) {
        MessageHeaderAccessor mha = new MessageHeaderAccessor();
        mha.copyHeaders(msg.getHeaders());
        mha.setReplyChannelName("enrichedOutboundChannel");
        mha.setHeader(OSIPHeader.SYNC_FIELD_NAME, msg.getHeaders().get(OSIPHeader.SYNC_FIELD_NAME));
        // FIXME [openwms]: 2019-04-09 d
        //mha.setHeader(CommHeader.MSG_LENGTH_FIELD_NAME, headerLength(msg.getHeaders()) + msg.getPayload().asString().length());
        mha.setHeader(OSIPHeader.SENDER_FIELD_NAME, msg.getHeaders().get(OSIPHeader.RECEIVER_FIELD_NAME));
        mha.setHeader(OSIPHeader.RECEIVER_FIELD_NAME, msg.getHeaders().get(OSIPHeader.SENDER_FIELD_NAME));
        mha.setHeader(OSIPHeader.SEQUENCE_FIELD_NAME, ""+(Integer.parseInt(String.valueOf(msg.getHeaders().get(OSIPHeader.SEQUENCE_FIELD_NAME))) + 1));
        return org.springframework.messaging.support.MessageBuilder.withPayload(msg.getPayload()).setHeaders(mha).build();
    }

    private int headerLength(MessageHeaders h) {
        return String.valueOf(h.get(OSIPHeader.SYNC_FIELD_NAME)).length() +
                String.valueOf( h.get(OSIPHeader.MSG_LENGTH_FIELD_NAME)).length() +
                String.valueOf( h.get(OSIPHeader.SENDER_FIELD_NAME)).length() +
                String.valueOf( h.get(OSIPHeader.RECEIVER_FIELD_NAME)).length() +
                String.valueOf( h.get(OSIPHeader.SEQUENCE_FIELD_NAME)).length();
    }
}