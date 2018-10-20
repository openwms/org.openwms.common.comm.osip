/*
 * openwms.org, the Open Warehouse Management System.
 * Copyright (C) 2014 Heiko Scherrer
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
package org.openwms.common.comm.sysu;

import org.openwms.common.comm.CommConstants;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.amqp.Amqp;
import org.springframework.messaging.MessageChannel;

/**
 * A SYSUConfiguration is the Spring configuration for the SYSU component.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Configuration
class SYSUConfiguration {

    /**
     * Create a MessageChannel with the proper name dynamically.
     *
     * @return A DirectChannel instance
     */
//    @Bean(name = SystemUpdateServiceActivator.INPUT_CHANNEL_NAME)
    public MessageChannel getMessageChannel() {
        return new DirectChannel();
    }

    @Profile(CommConstants.ASYNCHRONOUS)
    @Bean
    public AsyncRabbitTemplate asyncTemplate(RabbitTemplate rabbitTemplate,
            SimpleMessageListenerContainer replyContainer) {
        return new AsyncRabbitTemplate(rabbitTemplate, replyContainer);
    }

    @Profile(CommConstants.ASYNCHRONOUS)
    @Bean
    public SimpleMessageListenerContainer replyContainer(CachingConnectionFactory ccf) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(ccf);
        container.setQueueNames("sps01sysu");
        return container;
    }

//    @Bean
    public IntegrationFlow amqpOutbound(AmqpTemplate amqpTemplate) {
        return IntegrationFlows
                .from(getMessageChannel())
                .handle(Amqp.outboundAdapter(amqpTemplate).routingKey("sps01sysu")) // default exchange - route to queue 'foo'
                .get();
    }
}
