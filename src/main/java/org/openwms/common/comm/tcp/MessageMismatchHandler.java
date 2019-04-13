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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

/**
 * A MessageMismatchHandler cares about incoming error telegrams on a defined error channel with name {@value MessageMismatchHandler#ERROR_CHANNEL_ID}.
 *
 * @author <a href="mailto:hscherrer@openwms.org">Heiko Scherrer</a>
 */
@MessageEndpoint("mismatchServiceActivator")
public class MessageMismatchHandler {

    private static final String ERROR_CHANNEL_ID = "errExceptionChannel";
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageMismatchHandler.class);

    /**
     * Current implementation just logs error messages.
     * 
     * @param telegram
     *            The error telegram
     */
    @ServiceActivator(inputChannel = ERROR_CHANNEL_ID)
    public void handle(String telegram) {
        if (LOGGER.isErrorEnabled()) {
            LOGGER.error("Invalid telegram : [{}]", telegram);
        }
    }
}
