/*
 * openwms.org, the Open Warehouse Management System.
 * Copyright (C) 2018 Heiko Scherrer
 *
 * This file is part of openwms.org.
 *
 * openwms.org is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * openwms.org is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openwms.common.comm.app;

import org.openwms.common.comm.res.ResponseMessage;
import org.openwms.common.comm.res.ResponseMessageServiceActivator;
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
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.dsl.http.Http;
import org.springframework.messaging.MessageChannel;

import java.util.concurrent.Executors;

/**
 * A SynchronousReplyConfig.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Profile("!" + SpringProfiles.ASYNCHRONOUS_PROFILE)
@Configuration
@EnableDiscoveryClient
class SynchronousReplyConfig {

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
                .requestMapping(m -> m.methods(HttpMethod.POST)).messageConverters(mappingJackson2HttpMessageConverter(), new ByteArrayHttpMessageConverter(), new StringHttpMessageConverter())
                .requestPayloadType(ResponseMessage.class))
                .channel("resInChannel")
                .transform(responseMessageServiceActivator)
                .channel("enrichedOutboundChannel")
                .get();
    }
}
