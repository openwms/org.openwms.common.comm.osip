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
package org.openwms.common.comm.tcp;

import org.openwms.common.comm.MessageChannelNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.integration.ip.tcp.connection.TcpConnectionCloseEvent;
import org.springframework.integration.ip.tcp.connection.TcpConnectionOpenEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

/**
 * A ConnectionHolder keeps track of established connections per ConnectionFactory. It
 * listens on events whenever a new Connection is opened and stores the ConnectionId
 * that can be used as header argument in outbound messages.
 *
 * @author <a href="mailto:hscherrer@openwms.org">Heiko Scherrer</a>
 */
@Component
public class ConnectionHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionHolder.class);
    private Map<String, String> connectionIds = new ConcurrentHashMap<>();

    /**
     * On an {@link TcpConnectionOpenEvent} we store the connectionId along with the name
     * of the ConnectionFactory.
     *
     * @param event The event
     */
    @EventListener
    public void onOpenMessage(TcpConnectionOpenEvent event) {
        connectionIds.putIfAbsent(event.getConnectionFactoryName(), event.getConnectionId());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("New connection for factory [{}] established with id [{}]", event.getConnectionFactoryName(), event.getConnectionId());
        }
    }

    /**
     * On an {@link TcpConnectionCloseEvent} we try to remove the existing assignment.
     *
     * @param event The event
     */
    @EventListener
    public void onClosedMessage(TcpConnectionCloseEvent event) {
        String connectionId = connectionIds.remove(event.getConnectionFactoryName());
        if (connectionId != null && LOGGER.isDebugEnabled()) {
            LOGGER.debug("Deregister connection for factory [{}] with id [{}]", event.getConnectionFactoryName(), event.getConnectionId());
        }
    }

    /**
     * Return the current active connectionId for the ConnectionFactory given by the
     * {@code connectionFactoryName}.
     *
     * @param connectionFactoryName Name of the ConnectionFactory
     * @return The connectionId of the active Connection
     */
    public String getConnectionId(String connectionFactoryName) {
        String result = connectionIds.get(connectionFactoryName);
        if (result == null) {
            throw new MessageChannelNotFoundException(format("No Connection found for ConnectionFactory [%s]", connectionFactoryName));
        }
        return result;
    }
}
