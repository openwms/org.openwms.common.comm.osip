/*
 * Copyright 2005-2019 the original author or authors.
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

import org.ameba.tenancy.TenantHolder;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.integration.ip.tcp.connection.TcpConnectionInterceptorSupport;
import org.springframework.messaging.Message;

/**
 * A TenantInterception.
 *
 * @author Heiko Scherrer
 */
class TenantInterception extends TcpConnectionInterceptorSupport {

    public TenantInterception(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    @Override
    public boolean onMessage(Message<?> message) {
        String cfName = getTheConnection().getConnectionFactoryName();
        TenantHolder.setCurrentTenant(cfName.substring(cfName.indexOf('_') + 1, cfName.lastIndexOf('_')));
        MDC.put("Tenant", TenantHolder.getCurrentTenant());
        try {
            return super.onMessage(message);
        } finally {
            MDC.clear();
            TenantHolder.destroy();
        }
    }
}
