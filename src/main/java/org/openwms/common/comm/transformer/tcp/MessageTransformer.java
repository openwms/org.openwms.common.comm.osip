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
package org.openwms.common.comm.transformer.tcp;

import org.openwms.common.comm.MessageMismatchException;
import org.openwms.common.comm.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Headers;

import java.util.Map;

/**
 * A MessageTransformer.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@MessageEndpoint("messageTransformer")
public class MessageTransformer<T extends Payload>{

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageTransformer.class);

    /**
     * Transformer method to transform a message into a telegram String.
     *
     * @param message The incoming message
     * @return The {@link Payload} is transformable
     * @throws MessageMismatchException if no appropriate type was found.
     */
    @Transformer
    public Message<T> transform(Message<T> message, @Headers Map<String, Object> headers) {
        if (message == null) {
            LOGGER.info("Received message was null, just skip");
            return null;
        }
        return message;
    }
}
