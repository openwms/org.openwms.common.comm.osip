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
package org.openwms.common.comm;

import org.springframework.messaging.MessageChannel;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

/**
 * A Channels.
 *
 * @author Heiko Scherrer
 */
public class Channels {
	
	private Map<String, MessageChannel> outboundChannels;

	public Channels() {
		outboundChannels = new HashMap<>();
	}
	
	public void addOutboundChannel(String fullIdentifier, MessageChannel channel) {
		outboundChannels.put(fullIdentifier, channel);
	}
	
	public MessageChannel getOutboundChannel(String identifiedByField) {
		MessageChannel channel = outboundChannels.get("enrichedOutboundChannel_" + identifiedByField);
		if (channel == null) {
			throw new MessageChannelNotFoundException(format("No MessageChannel found for identifying value [%s]", identifiedByField));
		}
		return channel;
	}
}
