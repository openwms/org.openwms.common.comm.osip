/*
 * Copyright 2005-2022 the original author or authors.
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
package org.openwms.common.comm.osip.locu;

import org.ameba.annotation.Measured;
import org.openwms.common.comm.CommConstants;
import org.openwms.common.comm.NotRespondingServiceActivator;
import org.openwms.common.comm.osip.OSIP;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

import java.util.function.Function;

/**
 * A LocationUpdateMessageServiceActivator implements the Service Activator pattern and
 * delegates incoming {@link LocationUpdateMessage}s to the appropriate handler function.
 *
 * @author Heiko Scherrer
 */
@OSIP
@MessageEndpoint("locationupdateServiceActivator")
class LocationUpdateMessageServiceActivator implements NotRespondingServiceActivator<LocationUpdateMessage> {

    /** The name of the MessageChannel used as input-channel of this message processor. */
    static final String INPUT_CHANNEL_NAME = LocationUpdateMessage.IDENTIFIER + CommConstants.CHANNEL_SUFFIX;
    private final ApplicationContext ctx;
    private final Function<GenericMessage<LocationUpdateMessage>, Void> handler;

    LocationUpdateMessageServiceActivator(ApplicationContext ctx, Function<GenericMessage<LocationUpdateMessage>, Void> handler) {
        this.ctx = ctx;
        this.handler = handler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @OSIP
    @Measured
    @ServiceActivator(inputChannel = INPUT_CHANNEL_NAME)
    public void wakeUp(GenericMessage<LocationUpdateMessage> message) {
        handler.apply(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageChannel getChannel() {
        return ctx.getBean(INPUT_CHANNEL_NAME, MessageChannel.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getChannelName() {
        return INPUT_CHANNEL_NAME;
    }
}
