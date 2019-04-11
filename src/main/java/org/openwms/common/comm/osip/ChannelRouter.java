/*
 * Copyright 2019 Heiko Scherrer
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
package org.openwms.common.comm.osip;

import org.openwms.common.comm.Channels;
import org.openwms.common.comm.MessageProcessingException;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Router;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import static java.lang.String.format;

/**
 * A ChannelRouter.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
@MessageEndpoint
class ChannelRouter {

    private final Channels channels;

    ChannelRouter(Channels channels) {
        this.channels = channels;
    }

    /**
     * Routing method, tries to map an incoming {@link Payload} to a MessageChannel.
     *
     * @param message
     *            The message to process
     * @return The MessageChannel where to put the message
     */
    @Router(inputChannel = "outboundChannel", defaultOutputChannel = "commonExceptionChannel")
    public MessageChannel resolve(Message<Payload> message) {
        MessageChannel result = channels.getOutboundChannel(message.getPayload().getHeader().getReceiver());
        if (result == null) {
            throw new MessageProcessingException(format("No processor for message of type [%s] registered", message.getPayload().getMessageIdentifier()));
        }
        return result;
    }
}
