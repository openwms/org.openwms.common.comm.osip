/*
 * Copyright 2005-2020 the original author or authors.
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
package org.openwms.common.comm.osip.res;

import org.openwms.common.comm.osip.OSIPHeader;
import org.openwms.core.SpringProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * A ResponseMessageController exposes an endpoint to send <a href="https://interface21-io.gitbook.io/osip/messaging-between-layer-n-and-layer-n-1#response-telegram-res_">OSIP RES telegrams</a>.
 * The endpoint is only active if NOT {@link SpringProfiles#ASYNCHRONOUS_PROFILE} is active.
 *
 * @author Heiko Scherrer
 */
@Profile("!"+ SpringProfiles.ASYNCHRONOUS_PROFILE)
@RestController
class ResponseMessageController {

    private final ResponseMessageHandler handler;

    ResponseMessageController(ResponseMessageHandler handler) {
        this.handler = handler;
    }

    @PostMapping("/res")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleRes(@RequestBody ResponseMessage response, @RequestHeader(OSIPHeader.RECEIVER_FIELD_NAME) String receiver) {
        handler.handle(response, receiver);
    }
}
