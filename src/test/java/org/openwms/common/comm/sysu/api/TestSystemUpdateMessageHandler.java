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
package org.openwms.common.comm.sysu.api;

import org.openwms.common.comm.sysu.SystemUpdateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * A TestSystemUpdateMessageHandler.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Component
class TestSystemUpdateMessageHandler implements Function<SystemUpdateMessage, Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestSystemUpdateMessageHandler.class);
    @Override
    public Void apply(SystemUpdateMessage message) {
        LOGGER.info("!! Handle SYSU in test handler !! {}", message);
        return null;
    }
}
