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
package org.openwms.common.comm.osip.app;

import org.openwms.common.comm.osip.res.ResponseMessage;
import org.openwms.common.comm.osip.res.ResponseMessageServiceActivator;
import org.openwms.core.SpringProfiles;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.http.dsl.Http;
import org.springframework.messaging.MessageChannel;

import java.util.concurrent.Executors;

/**
 * A OSIPSynchronousReplyConfig.
 *
 * @author Heiko Scherrer
 */
@Profile("!" + SpringProfiles.ASYNCHRONOUS_PROFILE)
@Configuration
@EnableDiscoveryClient
class OSIPSynchronousReplyConfig {

    /*~ ----------------- Inbound ------------------ */
    @Bean
    MessageChannel resInChannel() {
        return MessageChannels.executor(Executors.newCachedThreadPool()).get();
    }

    @Bean
    MessageChannel resOutChannel() {
        return MessageChannels.executor(Executors.newCachedThreadPool()).get();
    }

    @Bean
    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter();
    }

    @Bean
    public IntegrationFlow httpPostAtms(ResponseMessageServiceActivator responseMessageServiceActivator) {
        return IntegrationFlows.from(Http.inboundGateway("/res")
                .requestMapping(
                        m -> m.methods(HttpMethod.POST))
                .messageConverters(
                        mappingJackson2HttpMessageConverter(),
                        new ByteArrayHttpMessageConverter(),
                        new StringHttpMessageConverter())
                .requestPayloadType(ResponseMessage.class))
                .channel("resInChannel")
                .transform(responseMessageServiceActivator)
                .channel("enrichedOutboundChannel")
                .get();
    }
}
