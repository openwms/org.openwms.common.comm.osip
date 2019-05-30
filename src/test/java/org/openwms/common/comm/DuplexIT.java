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
package org.openwms.common.comm;

import org.ameba.exception.NotFoundException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.openwms.common.comm.config.Driver;
import org.openwms.common.comm.config.Subsystem;
import org.openwms.common.comm.osip.OSIPHeader;
import org.openwms.common.comm.osip.res.ResponseMessageSender;
import org.openwms.common.comm.tcp.ConnectionHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.integration.ip.IpHeaders;
import org.springframework.integration.ip.dsl.Tcp;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.transformer.ObjectToStringTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openwms.common.comm.app.DriverConfiguration.PREFIX_ENRICHED_OUTBOUND_CHANNEL;

/**
 * A DuplexIT.
 *
 * @author Heiko Scherrer
 */
@SpringBootTest
@ImportResource("classpath:test.xml")
@ActiveProfiles("ASYNCHRONOUS")
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DirtiesContext
public class DuplexIT {

    @Autowired
    private Driver driver;
    @Autowired
    private ConnectionHolder connectionHolder;

    @ComponentScan
    public static class TestConfig {

    }

    @Autowired
    private ResponseMessageSender sender;
    @Autowired
    private AbstractServerConnectionFactory connectionFactory_SPS03_outbound;

    @Autowired
    @Qualifier(PREFIX_ENRICHED_OUTBOUND_CHANNEL + "SPS03")
    private MessageChannel channel;

    @Test
    public void testTcpAdapters() throws Exception {
        System.out.println(connectionFactory_SPS03_outbound.getComponentName());
        ApplicationEventPublisher publisher = e -> { };
        Subsystem.Duplex duplex = driver.getConnections().getSubsystems().stream().filter(c -> c.getDuplex() != null).findFirst().orElseThrow(NotFoundException::new).getDuplex();

        AbstractClientConnectionFactory client = Tcp.netClient(duplex.getHostname(), duplex.getPort()).id("client").get();
        final AtomicReference<Message<?>> received = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);
        client.registerListener(m -> {
            received.set(new ObjectToStringTransformer().transform(m));
            latch.countDown();
            return false;
        });
        client.setApplicationEventPublisher(publisher);
        client.afterPropertiesSet();

        client.start();
        client.getConnection().send(new GenericMessage<>("###00160SPS03MFC__00001REQ_000000000S0000004711FGINIPNT000100000000????????????????????0000009020131123225959***************************************************"));
        Map<String, Object> headers = new HashMap<>();
        headers.put(IpHeaders.CONNECTION_ID, connectionHolder.getConnectionId("SPS03"));
        headers.put(OSIPHeader.RECEIVER_FIELD_NAME, "SPS03");

        sender.send("###00160MFC__SPS0300002RES_000000000S0000004711FGINIPNT000100000000FGINCONV000100000000********************0000009020190527184424*******************************", headers);
        assertThat(latch.await(10, TimeUnit.SECONDS)).isTrue();
        assertThat(received.get().getPayload()).isEqualTo("###00160nullnull00001RES_000000000S0000004711FGINIPNT000100000000FGINCONV000100000000********************0000009020190527184424*********************************");
        client.stop();
    }

}
