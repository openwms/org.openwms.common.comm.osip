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
package org.openwms.common.comm.osip.synq;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

/**
 * A TimesyncConfiguration is the JavaConfig for the {@link TimesyncRequest} handling
 * part.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
@Configuration
class TimesyncConfiguration {

    @Bean(name = TimesyncServiceActivator.INPUT_CHANNEL_NAME)
    public MessageChannel getMessageChannel() {
        return new DirectChannel();
    }
}
