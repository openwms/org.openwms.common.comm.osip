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
package org.openwms.common.comm.osip.app;

import org.openwms.common.comm.osip.OSIP;
import org.openwms.common.comm.osip.OSIPHeader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.ip.IpHeaders;
import org.springframework.integration.support.converter.MapMessageConverter;

/**
 * A OSIPConfiguration.
 *
 * @author Heiko Scherrer
 */
@OSIP
@Configuration
class OSIPConfiguration {

    @Bean
    MapMessageConverter mapMessageConverter() {
        MapMessageConverter result = new MapMessageConverter();
        result.setHeaderNames(OSIPHeader.SYNC_FIELD_NAME, OSIPHeader.SENDER_FIELD_NAME,
                OSIPHeader.MSG_LENGTH_FIELD_NAME, OSIPHeader.SEQUENCE_FIELD_NAME,
                OSIPHeader.RECEIVER_FIELD_NAME, IpHeaders.CONNECTION_ID);
        return result;
    }
}
