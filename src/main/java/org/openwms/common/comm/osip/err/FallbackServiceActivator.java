/*
 * Copyright 2005-2023 the original author or authors.
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
package org.openwms.common.comm.osip.err;

import org.ameba.annotation.Measured;
import org.openwms.common.comm.osip.CommonMessageFactory;
import org.openwms.common.comm.osip.OSIP;
import org.openwms.common.comm.osip.OSIPHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

import java.util.Date;

/**
 * A FallbackServiceActivator takes all other telegrams not defined and not mapped to any
 * specific channel and publishes an OSIP ERR message.
 * 
 * @author Heiko Scherrer
 */
@OSIP
@MessageEndpoint("errorServiceActivator")
public class FallbackServiceActivator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FallbackServiceActivator.class);

    /**
     * Reply to incoming OSIP telegrams on the inputChannel with an {@link ErrorMessage} o the outputChannel.
     * 
     * @param telegram
     *            The incoming OSIP telegram
     * @return An {@link ErrorMessage}
     */
    @Measured
    @OSIP
    @ServiceActivator(inputChannel = "commonExceptionChannel", outputChannel = "outboundChannel")
    public ErrorMessage handle(String telegram) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Common error: {}", telegram);
        }
        OSIPHeader header = CommonMessageFactory.createHeader(telegram);
        String sender = header.getSender();
        header.setSender(header.getReceiver());
        header.setReceiver(sender);
        return ErrorMessage.newBuilder().errorCode(ErrorCodes.UNKNOWN_MESSAGE_TYPE).created(new Date()).build();
    }
}
