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
package org.openwms.common.comm.router;

import org.openwms.common.comm.CommConstants;
import org.openwms.common.comm.CustomServiceActivator;
import org.openwms.common.comm.MessageProcessingException;
import org.openwms.common.comm.osip.Payload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Router;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * A CommonMessageRouter collects all {@link CustomServiceActivator}s from the ApplicationContext and tries to find a suitable
 * {@link CustomServiceActivator} when an incoming message arrives. If no suitable processor is found, the message will be delegated to the
 * default exception channel.
 * 
 * @author <a href="mailto:hscherrer@openwms.org">Heiko Scherrer</a>
 */
@MessageEndpoint("messageRouter")
public class CommonMessageRouter {

    private final List<CustomServiceActivator> processors;
    private Map<String, CustomServiceActivator> processorMap;

    @Autowired
    public CommonMessageRouter(@Autowired(required = false) List<CustomServiceActivator> processors) {
        this.processors = processors;
    }

    @PostConstruct
    void onPostConstruct() {
        processorMap = processors == null ?
                Collections.emptyMap() :
                processors.stream().collect(Collectors.toMap(CustomServiceActivator::getChannelName, p -> p));
    }

    /**
     * Routing method, tries to map an incoming {@link Payload} to a MessageChannel.
     * 
     * @param message
     *            The message to process
     * @return The MessageChannel where to put the message
     */
    @Router(inputChannel = "transformerOutputChannel", defaultOutputChannel = "commonExceptionChannel")
    public MessageChannel resolve(Message<Payload> message) {
        CustomServiceActivator result = processorMap.get(message.getPayload().getMessageIdentifier() + CommConstants.CHANNEL_SUFFIX);
        if (result == null) {
            throw new MessageProcessingException(format("No processor for message of type [%s] registered", message.getPayload().getMessageIdentifier()));
        }
        return result.getChannel();
    }
}
