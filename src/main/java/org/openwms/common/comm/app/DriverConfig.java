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
package org.openwms.common.comm.app;

import org.openwms.common.comm.tcp.CustomTcpMessageMapper;
import org.openwms.common.comm.tcp.OSIPTelegramSerializer;
import org.openwms.common.comm.transformer.tcp.TelegramTransformer;
import org.openwms.core.SpringProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.ip.tcp.connection.AbstractConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpMessageMapper;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayCrLfSerializer;
import org.springframework.integration.support.converter.MapMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import static org.ameba.LoggingCategories.BOOT;

/**
 * A DriverConfig.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Configuration
class DriverConfig {

    private static final Logger BOOT_LOGGER = LoggerFactory.getLogger(BOOT);
    public
    @LoadBalanced
    @Bean
    RestTemplate aLoadBalanced() {
        return new RestTemplate();
    }

    @Bean
    MapMessageConverter mapMessageConverter() {
        MapMessageConverter result = new MapMessageConverter();
        result.setHeaderNames("SYNC_FIELD", "SENDER", "MSG_LENGTH", "SEQUENCENO", "RECEIVER", "ip_connectionId");
        return result;
    }

    @Bean
    TcpMessageMapper customTcpMessageMapper(ByteArrayMessageConverter byteArrayMessageConverter, MapMessageConverter mapMessageConverter) {
        return new CustomTcpMessageMapper(byteArrayMessageConverter, mapMessageConverter);
    }

    /**
     * We need to put this property resolving bean in between, because CGLIB is used to build a proxy around refreshscope beans. Doing
     * this for the tcpip factory does not work
     */
    @Bean
    @RefreshScope
    Map<String, Integer> propertyHolder(
            @Value("${owms.driver.server.port}") int port,
            @Value("${owms.driver.client.port}") int clientPort,
            @Value("${owms.driver.server.so-timeout}") int soTimeout,
            @Value("${owms.driver.server.so-receive-buffer-size}") int soReceiveBufferSize,
            @Value("${owms.driver.server.so-send-buffer-size}") int soSendBufferSize,
            @Value("${owms.driver.res.queue-name}") String queueName,
            @Value("${owms.driver.res.exchange-name}") String exchangeMapping,
            @Value("${owms.driver.res.routing-key}") String routingKey,
            Environment environment
    ) {
        Map<String, Integer> res = new HashMap<>();
        res.put("owms.driver.server.port", port);
        res.put("owms.driver.client.port", clientPort);
        res.put("owms.driver.server.so-timeout", soTimeout);
        res.put("owms.driver.server.so-receive-buffer-size", soReceiveBufferSize);
        res.put("owms.driver.server.so-send-buffer-size", soSendBufferSize);
        BOOT_LOGGER.info("Running driver on ports: [{}, {}]", port, clientPort);
        if (environment.acceptsProfiles(SpringProfiles.ASYNCHRONOUS_PROFILE)) {
            BOOT_LOGGER.info("> in asynchronous mode. Bound to Queue [{}], Exchange [{}] and using Routing Key [{}]", queueName, exchangeMapping, routingKey);
        }
        return res;
    }

    /*~ ---------------- TCP/IP stuff ------------- */
    @Bean("tcpServerConnectionFactory")
    @DependsOn("propertyHolder")
    AbstractConnectionFactory tcpConnectionFactory(Map<String, Integer> propertyHolder,
                                                   TcpMessageMapper customTcpMessageMapper) {
        TcpNetServerConnectionFactory connectionFactory = new TcpNetServerConnectionFactory(propertyHolder.get("owms.driver.server.port"));
        connectionFactory.setSoTimeout(propertyHolder.get("owms.driver.server.so-timeout"));
        connectionFactory.setSerializer(telegramSerializer());
        connectionFactory.setDeserializer(byteArraySerializer());
        connectionFactory.setSoReceiveBufferSize(propertyHolder.get("owms.driver.server.so-receive-buffer-size"));
        connectionFactory.setSoSendBufferSize(propertyHolder.get("owms.driver.server.so-send-buffer-size"));
        connectionFactory.setMapper(customTcpMessageMapper);
        return connectionFactory;
    }

    /*~ --------------- MessageChannels ------------ */
    @Bean
    MessageChannel commonExceptionChannel() {
        return MessageChannels.executor(Executors.newCachedThreadPool()).get();
    }

    @Bean
    MessageChannel inboundChannel() {
        return MessageChannels.executor(Executors.newCachedThreadPool()).get();
    }

    @Bean
    MessageChannel outboundChannel() {
        return MessageChannels.direct().get();//executor(Executors.newCachedThreadPool()).get();
    }

    @Bean
    MessageChannel enrichedOutboundChannel() {
        return MessageChannels.direct().get();//executor(Executors.newCachedThreadPool()).get();
    }
    /*~ --------- Serializer / Deserializer -------- */
    @Bean
    ByteArrayCrLfSerializer byteArraySerializer() {
        return new ByteArrayCrLfSerializer();
    }

    @Bean
    OSIPTelegramSerializer telegramSerializer() {
        return new OSIPTelegramSerializer();
    }

    /*~ ----------------   Converter---------------- */
    @Bean
    ByteArrayMessageConverter byteArrayMessageConverter() {
        return new ByteArrayMessageConverter();
    }

    /*~ -------------------- Flows ----------------- */
    @Bean
    IntegrationFlow inboundFlow(TelegramTransformer telegramTransformer) {
        return IntegrationFlows.from("inboundChannel")
                .transform(telegramTransformer)
                .channel("transformerOutputChannel")
                .route("messageRouter")
                .get();
    }
}
