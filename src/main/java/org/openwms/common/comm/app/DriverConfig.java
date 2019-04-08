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

import org.openwms.common.comm.ConfigurationException;
import org.openwms.common.comm.tcp.CustomTcpMessageMapper;
import org.openwms.common.comm.tcp.OSIPTelegramSerializer;
import org.openwms.common.comm.tcp.PayloadSerializer;
import org.openwms.common.comm.transformer.tcp.TelegramTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.handler.AbstractMessageHandler;
import org.springframework.integration.ip.IpHeaders;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.AbstractConnectionFactory;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpMessageMapper;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayCrLfSerializer;
import org.springframework.integration.support.converter.MapMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.ByteArrayMessageConverter;

import java.util.Optional;
import java.util.concurrent.Executors;

import static java.lang.String.format;
import static org.ameba.LoggingCategories.BOOT;

/**
 * A DriverConfig.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Configuration
@IntegrationComponentScan
@EnableIntegration
class DriverConfig implements ApplicationEventPublisherAware {

    private static final Logger BOOT_LOGGER = LoggerFactory.getLogger(BOOT);
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private AnnotationConfigApplicationContext applicationContext;

    void registerBean(String beanName, Object singletonObject) {
        applicationContext.getBeanFactory().registerSingleton(beanName, singletonObject);
    }

    /*~ ---------------- TCP/IP stuff -------------
    @Bean("tcpServerConnectionFactory")
    @DependsOn({"propertyHolder", "clientConnectionFactory"})
    AbstractConnectionFactory tcpConnectionFactory(Map<String, Integer> propertyHolder,
                                                   TcpMessageMapper customTcpMessageMapper) {
        TcpNetServerConnectionFactory connectionFactory = new TcpNetServerConnectionFactory(propertyHolder.get("owms.driver.server.port"));
        connectionFactory.setSoTimeout(propertyHolder.get("owms.driver.server.so-timeout"));
        connectionFactory.setSerializer(telegramSerializer());
        connectionFactory.setDeserializer(byteArraySerializer());
        connectionFactory.setSoReceiveBufferSize(propertyHolder.get("owms.driver.server.so-receive-buffer-size"));
        connectionFactory.setSoSendBufferSize(propertyHolder.get("owms.driver.server.so-send-buffer-size"));
        connectionFactory.setMapper(customTcpMessageMapper);
        connectionFactory.setSingleUse(false);
        return connectionFactory;
    }
 */
    private AbstractServerConnectionFactory createInboundConnectionFactory(
            Connections connections,
            Subsystem subsystem,
            TcpMessageMapper customTcpMessageMapper) {

        Subsystem.Inbound inbound = subsystem.getInbound();
        int port = Optional.ofNullable(inbound.getPort()).orElseThrow(() -> new ConfigurationException(format("Port not configured for outbound connection server [%s]", subsystem.getName())));

        TcpNetServerConnectionFactory connectionFactory = new TcpNetServerConnectionFactory(port);
        connectionFactory.setDeserializer(byteArrayCrLfSerializer());
        connectionFactory.setMapper(customTcpMessageMapper);
        connectionFactory.setApplicationEventPublisher(applicationEventPublisher);

        Integer soTimeout = inbound.getSoTimeout() == null ? connections.getSoTimeout() : inbound.getSoTimeout();
        if (soTimeout != null) {
            connectionFactory.setSoTimeout(soTimeout);
        }

        Integer soReceiveBufferSize = inbound.getSoReceiveBufferSize() == null ? connections.getSoReceiveBufferSize() : inbound.getSoReceiveBufferSize();
        if (soReceiveBufferSize != null) {
            connectionFactory.setSoReceiveBufferSize(soReceiveBufferSize);
        }
        return connectionFactory;
    }

    private AbstractServerConnectionFactory createOutboundConnectionFactory(
            Connections connections,
            Subsystem subsystem,
            TcpMessageMapper customTcpMessageMapper) {

        Subsystem.Outbound outbound = subsystem.getOutbound();
        int port = Optional.ofNullable(outbound.getPort()).orElseThrow(() -> new ConfigurationException(format("Port not configured for outbound connection server [%s]", subsystem.getName())));

        TcpNetServerConnectionFactory connectionFactory = new TcpNetServerConnectionFactory(port);
        connectionFactory.setDeserializer(byteArrayCrLfSerializer());
        connectionFactory.setMapper(customTcpMessageMapper);
        connectionFactory.setApplicationEventPublisher(applicationEventPublisher);

        Integer soTimeout = outbound.getSoTimeout() == null ? connections.getSoTimeout() : outbound.getSoTimeout();
        if (soTimeout != null) {
            connectionFactory.setSoTimeout(soTimeout);
        }

        Integer soSendBufferSize = outbound.getSoSendBufferSize() == null ? connections.getSoSendBufferSize() : outbound.getSoSendBufferSize();
        if (soSendBufferSize != null) {
            connectionFactory.setSoSendBufferSize(soSendBufferSize);
        }
        return connectionFactory;
    }

    private TcpReceivingChannelAdapter tcpReceivingChannelAdapter(AbstractConnectionFactory connectionFactory, MessageChannel inboundChannel) {
        TcpReceivingChannelAdapter adapter = new TcpReceivingChannelAdapter();
        adapter.setConnectionFactory(connectionFactory);
        adapter.setOutputChannel(inboundChannel);
        return adapter;
    }

    private void createInbound(Connections connections, Subsystem subsystem,
            TcpMessageMapper customTcpMessageMapper, MessageChannel inboundChannel, PayloadSerializer payloadSerializer) {

        Subsystem.Inbound inbound = subsystem.getInbound();
        if (inbound.getMode() == Subsystem.MODE.server) {

            AbstractServerConnectionFactory connectionFactory = createInboundConnectionFactory(connections, subsystem, customTcpMessageMapper);
            connectionFactory.setBeanName("connectionFactory_" + subsystem.getName() + "_inbound");
            attachReceivingChannelAdapter(subsystem, inboundChannel, connectionFactory);
            BOOT_LOGGER.info("[{}] Inbound  TCP/IP connection configured as server: Port [{}]", subsystem.getName(), inbound.getPort());
        } else if (inbound.getMode() == Subsystem.MODE.client) {

            AbstractClientConnectionFactory connectionFactory = createClientConnectionFactory(inbound.getHostname(), inbound.getPort(), customTcpMessageMapper, payloadSerializer);
            attachReceivingChannelAdapter(subsystem, inboundChannel, connectionFactory);
            BOOT_LOGGER.info("[{}] Inbound  TCP/IP connection configured as client: Hostname [{}], port [{}]", subsystem.getName(), inbound.getHostname(), inbound.getPort());
        } else {
            throw new ConfigurationException(format("Mode [%s] for subsystem [%s] inbound not supported. Please use [server] or [client]", inbound.getMode(), subsystem.getName()));
        }
    }

    private void attachReceivingChannelAdapter(Subsystem subsystem, MessageChannel inboundChannel, AbstractConnectionFactory connectionFactory) {
        TcpReceivingChannelAdapter channelAdapter = tcpReceivingChannelAdapter(connectionFactory, inboundChannel);

        registerBean("connectionFactory_" + subsystem.getName() + "_inbound", connectionFactory);
        registerBean("channelAdapter_" + subsystem.getName() + "_inbound", channelAdapter);
    }

    private AbstractClientConnectionFactory createClientConnectionFactory(String clientHostname, int clientPort, TcpMessageMapper tcpMessageMapper, PayloadSerializer payloadSerializer) {
        TcpNetClientConnectionFactory connectionFactory = new TcpNetClientConnectionFactory(clientHostname, clientPort);
        connectionFactory.setSerializer(payloadSerializer);
        connectionFactory.setDeserializer(byteArrayCrLfSerializer());
        //connectionFactory.setMapper(tcpMessageMapper);
        connectionFactory.start();
        return connectionFactory;
    }

    private void createOutbound(Connections connections, Subsystem subsystem, TcpMessageMapper customTcpMessageMapper, Channels channels, PayloadSerializer payloadSerializer) {

        Subsystem.Outbound outbound = subsystem.getOutbound();
        if (outbound.getMode() == Subsystem.MODE.server) {

            AbstractServerConnectionFactory connectionFactory = createOutboundConnectionFactory(connections, subsystem, customTcpMessageMapper);
            connectionFactory.setBeanName("connectionFactory_" + subsystem.getName() + "_outbound");
            connectionFactory.setComponentName("connectionFactory_" + subsystem.getName() + "_outbound");
            connectionFactory.setSingleUse(false);
            connectionFactory.setApplicationEventPublisher(applicationEventPublisher);

            TcpSendingMessageHandler sendingMessageHandler = createSendingMessageHandler(connectionFactory);
            DirectChannel channel = createEnrichedOutboundChannel(sendingMessageHandler);

            // This adapter is only required to let the SCF work as a CCF!!
            TcpReceivingChannelAdapter adapter = new TcpReceivingChannelAdapter();
            adapter.setConnectionFactory(connectionFactory);
            adapter.setClientMode(false);
            adapter.start();

            channels.addOutboundChannel("enrichedOutboundChannel_" + outbound.getIdentifiedByValue(), channel);
            registerBean("connectionFactory_" + subsystem.getName() + "_outbound", connectionFactory);
            registerBean("outboundAdapter_" + subsystem.getName() + "_outbound", adapter);
            registerBean("sendingMessageHandler_" + subsystem.getName() + "_outbound", sendingMessageHandler);
            registerBean("enrichedOutboundChannel_" + outbound.getIdentifiedByValue(), channel);

            BOOT_LOGGER.info("[{}] Outbound TCP/IP connection configures as server: Port [{}]", outbound.getIdentifiedByValue(), outbound.getPort());
        } else if (outbound.getMode() == Subsystem.MODE.client) {

            AbstractClientConnectionFactory connectionFactory = createClientConnectionFactory(outbound.getHostname(), outbound.getPort(), customTcpMessageMapper, payloadSerializer);
            registerBean("connectionFactory_" + subsystem.getName() + "_outbound", connectionFactory);

            TcpSendingMessageHandler sendingMessageHandler = createSendingMessageHandler(connectionFactory);
            registerBean("sendingMessageHandler_" + subsystem.getName() + "_outbound", sendingMessageHandler);

            DirectChannel channel = createEnrichedOutboundChannel(sendingMessageHandler);
            registerBean("enrichedOutboundChannel_" + outbound.getIdentifiedByValue(), channel);

            channels.addOutboundChannel("enrichedOutboundChannel_" + outbound.getIdentifiedByValue(), channel);
            BOOT_LOGGER.info("[{}] Outbound TCP/IP connection configured as client: Hostname [{}], port [{}]", outbound.getIdentifiedByValue(), outbound.getHostname(), outbound.getPort());
        } else {
            throw new ConfigurationException(format("Mode [%s] for outbound not supported. Please use [server] or [client]", outbound.getMode()));
        }
    }

    TcpSendingMessageHandler createSendingMessageHandler(AbstractConnectionFactory connectionFactory) {
        TcpSendingMessageHandler handler = new TcpSendingMessageHandler();
        handler.setConnectionFactory(connectionFactory);
        handler.setClientMode(true);
        return handler;
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

    DirectChannel createEnrichedOutboundChannel(AbstractMessageHandler messageHandler) {
        DirectChannel channel = MessageChannels.direct().get();
        channel.subscribe(messageHandler);
        return channel;
    }

    @Bean
    @DependsOn("connections")
    Channels channels(Connections connections, TcpMessageMapper tcpMessageMapper, MessageChannel inboundChannel, PayloadSerializer payloadSerializer) {
        Channels channels =  new Channels();
        for(Subsystem subsystem : connections.getSubsystems()) {
            createInbound(connections, subsystem, tcpMessageMapper, inboundChannel, payloadSerializer);
            createOutbound(connections, subsystem, tcpMessageMapper, channels, payloadSerializer);
        }

        return channels;
    }

    /*~ --------- Serializer / Deserializer -------- */
    @Bean
    ByteArrayCrLfSerializer byteArrayCrLfSerializer() {
        return new ByteArrayCrLfSerializer();
    }

    @Bean
    OSIPTelegramSerializer telegramSerializer() {
        return new OSIPTelegramSerializer();
    }

    /*~ ----------------   Converter---------------- */
    @Bean
    TcpMessageMapper customTcpMessageMapper(ByteArrayMessageConverter byteArrayMessageConverter, MapMessageConverter mapMessageConverter) {
        CustomTcpMessageMapper messageMapper = new CustomTcpMessageMapper(mapMessageConverter);
        return messageMapper;
    }

    @Bean
    ByteArrayMessageConverter byteArrayMessageConverter() {
        return new ByteArrayMessageConverter();
    }

    @Bean
    MapMessageConverter mapMessageConverter() {
        MapMessageConverter result = new MapMessageConverter();
        result.setHeaderNames("SYNC_FIELD", "SENDER", "MSG_LENGTH", "SEQUENCENO", "RECEIVER", IpHeaders.CONNECTION_ID);
        return result;
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

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
