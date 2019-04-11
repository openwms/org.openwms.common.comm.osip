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
package org.openwms.common.comm.osip.res;

import org.openwms.common.comm.osip.OSIP;
import org.openwms.core.SpringProfiles;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * A TimesyncConfiguration is the JavaConfig for the {@link ResponseMessage} handling
 * part.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@OSIP
@Configuration
class ResponseMessageConfiguration {

    @Bean
    DirectExchange resExchange(@Value("${owms.driver.osip.res.exchange-name}") String exchangeName) {
        return new DirectExchange(exchangeName);
    }

    @Bean
    Queue resQueue(@Value("${owms.driver.osip.res.queue-name}") String queueName) {
        return new Queue(queueName);
    }

    @Bean
    Binding resBinding(
            @Value("${owms.driver.osip.res.exchange-name}") String exchangeName,
            @Value("${owms.driver.osip.res.queue-name}") String queueName,
            @Value("${owms.driver.osip.res.routing-key}") String routingKey
    ) {
        return BindingBuilder
                .bind(resQueue(queueName))
                .to(resExchange(exchangeName))
                .with(routingKey);
    }
}
