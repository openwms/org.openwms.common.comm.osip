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
package org.openwms.common.comm.req;

import org.openwms.common.comm.CommHeader;
import org.springframework.messaging.support.GenericMessage;

/**
 * A RequestHelper.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
class RequestHelper {

    static RequestVO getRequest(GenericMessage<RequestMessage> msg) {
        return RequestVO.builder()
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
