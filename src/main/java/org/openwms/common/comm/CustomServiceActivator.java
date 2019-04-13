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
package org.openwms.common.comm;

import org.springframework.messaging.MessageChannel;

/**
 * A CustomServiceActivator.
 *
 * @author <a href="mailto:hscherrer@openwms.org">Heiko Scherrer</a>
 */
public interface CustomServiceActivator {

    /**
     * The input-channel instance that is used by the processor to process messages from.
     *
     * @return The encapsulated MessageChannel instance.
     */
    MessageChannel getChannel();

    /**
     * Returns the unique name of the MessageChannel that is used as input-channel for the
     * processing messages.
     *
     * @return Expected to be the unique name of the message concatenated with a suffix,
     * defined in {@link CommConstants#CHANNEL_SUFFIX}
     */
    String getChannelName();

}
