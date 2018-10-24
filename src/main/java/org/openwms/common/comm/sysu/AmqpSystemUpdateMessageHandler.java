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
package org.openwms.common.comm.sysu;

import org.ameba.annotation.Measured;
import org.openwms.core.SpringProfiles;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * A AmqpSystemUpdateMessageHandler.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@Component
@RefreshScope
class AmqpSystemUpdateMessageHandler implements Function<GenericMessage<SystemUpdateMessage>, Void> {

    private final AmqpTemplate amqpTemplate;
    private final String queueName;

    AmqpSystemUpdateMessageHandler(AmqpTemplate amqpTemplate, @Value("${owms.driver.sysu.queue-name}") String queueName) {
        this.amqpTemplate = amqpTemplate;
        this.queueName = queueName;
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    public Void apply(GenericMessage<SystemUpdateMessage> systemUpdateMessageGenericMessage) {
        amqpTemplate.convertAndSend(queueName, systemUpdateMessageGenericMessage.getPayload());
        return null;
    }
}
