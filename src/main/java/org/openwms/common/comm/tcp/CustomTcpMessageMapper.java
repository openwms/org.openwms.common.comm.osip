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
package org.openwms.common.comm.tcp;

import org.springframework.integration.ip.tcp.connection.TcpMessageMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.util.Assert;

/**
 * A CustomTcpMessageMapper.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
public class CustomTcpMessageMapper extends TcpMessageMapper {

    private final MessageConverter outboundMessageConverter;

    public CustomTcpMessageMapper(MessageConverter outboundMessageConverter) {
        Assert.notNull(outboundMessageConverter, "'outboundMessageConverter' must not be null");
        this.outboundMessageConverter = outboundMessageConverter;
    }

    @Override
    public Object fromMessage(Message<?> message) throws Exception {
        Object data = super.fromMessage(message);
        return data;
    }
}
