/*
 * Copyright 2005-2025 the original author or authors.
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
package org.openwms.common.comm.app;

import org.ameba.tenancy.TenantHolder;
import org.openwms.common.comm.osip.OSIPHeader;
import org.openwms.core.SpringProfiles;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * A OSIPAppConfiguration is a Spring configuration class that bootstraps OSIP specific beans active with ASYNCHRONOUS profile .
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@Configuration
class OSIPAppConfiguration {

    @Bean
    MessagePostProcessor osipMessagePostProcessor() {
        return m -> {
            m.getMessageProperties().getHeaders().put(OSIPHeader.TENANT_FIELD_NAME, TenantHolder.getCurrentTenant());
            return m;
        };
    }
}
