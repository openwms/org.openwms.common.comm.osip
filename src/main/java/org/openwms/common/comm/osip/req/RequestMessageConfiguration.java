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
package org.openwms.common.comm.osip.req;

import org.openwms.common.comm.osip.OSIP;
import org.openwms.core.SpringProfiles;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

/**
 * A RequestMessageConfiguration is the JavaConfig for the {@link RequestMessage} handling
 * part.
 *
 * @author Heiko Scherrer
 */
@OSIP
@Configuration
class RequestMessageConfiguration {

    @Bean(name = RequestMessageServiceActivator.INPUT_CHANNEL_NAME)
    public MessageChannel getMessageChannel() {
        return new DirectChannel();
    }

    @Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
    @Bean
    TopicExchange reqExchange(
            @Value("${owms.driver.osip.req.exchange-name}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }
}
