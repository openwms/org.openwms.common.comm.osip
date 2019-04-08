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
package org.openwms.common.comm.req;

import org.openwms.common.comm.CommHeader;
import org.springframework.messaging.support.GenericMessage;

/**
 * A RequestHelper.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
class RequestHelper {

    static RequestVO getRequest(GenericMessage<RequestMessage> msg) {
        return RequestVO.builder()
                .type("REQ_")
                .actualLocation(msg.getPayload().getActualLocation())
                .targetLocation(msg.getPayload().getTargetLocation())
                .errorCode(msg.getPayload().getErrorCode())
                .created(msg.getPayload().getCreated())
                .barcode(msg.getPayload().getBarcode())
                .header(RequestVO.RequestHeaderVO.builder()
                        .receiver(msg.getHeaders().get(CommHeader.RECEIVER_FIELD_NAME, String.class))
                        .sender(msg.getHeaders().get(CommHeader.SENDER_FIELD_NAME, String.class))
                        .sequenceNo(""+msg.getHeaders().get(CommHeader.SEQUENCE_FIELD_NAME, Short.class))
                        .build())
                .build();
    }
}
