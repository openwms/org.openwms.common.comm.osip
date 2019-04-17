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
package org.openwms.common.comm.osip.app;

import org.openwms.common.comm.osip.OSIP;
import org.openwms.core.SpringProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.Map;

import static org.ameba.LoggingCategories.BOOT;

/**
 * A AsynchronousConfiguration.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@Configuration
class AsynchronousConfiguration {

    private static final Logger BOOT_LOGGER = LoggerFactory.getLogger(BOOT);

    @Bean
    @OSIP
    @RefreshScope
    Map<String, Integer> asyncBootLogger(
            @Value("${owms.driver.osip.res.queue-name}") String queueName,
            @Value("${owms.driver.osip.res.exchange-name}") String exchangeMapping,
            @Value("${owms.driver.osip.res.routing-key-in}") String routingKey
    ) {
        Map<String, Integer> res = new HashMap<>();
        BOOT_LOGGER.info("In ASYNCHRONOUS mode. OSIP RES bound to Queue [{}], Exchange [{}] and using Routing Key [{}]", queueName, exchangeMapping, routingKey);
        return res;
    }
}
