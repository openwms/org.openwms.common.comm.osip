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
package org.openwms.common.comm.res;

import org.openwms.common.comm.CommConstants;
import org.openwms.common.comm.CommHeader;
import org.openwms.common.comm.MessageMismatchException;
import org.openwms.common.comm.tcp.OSIPSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static java.lang.String.format;
import static org.openwms.common.comm.ParserUtils.padRight;

/**
 * A ResponseMessageSerializer.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Component
public class ResponseMessageSerializer implements OSIPSerializer<ResponseMessage> {

    private final short maxTelegramLength;
    private final String syncField;

    public ResponseMessageSerializer(@Value("${owms.driver.server.so-send-buffer-size}") short maxTelegramLength,
            @Value("${owms.driver.osip.sync-field}") String syncField) {
        this.maxTelegramLength = maxTelegramLength;
        this.syncField = syncField;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageIdentifier() {
        return ResponseMessage.IDENTIFIER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String serialize(ResponseMessage obj) {
        CommHeader header = CommHeader.builder()
                .sync(syncField)
                .messageLength(maxTelegramLength)
                .sender(obj.getHeader().getSender())
                .receiver(obj.getHeader().getReceiver())
                .sequenceNo(obj.getHeader().getSequenceNo()+1)
                .build();
        String s = header + obj.asString();
        if (s.length() > maxTelegramLength) {
            throw new MessageMismatchException(format("Defined telegram length exceeds configured size of owms.driver.server.so-send-buffer-size=[%d]. Actual length is [%d]", maxTelegramLength, s.length()));
        }
        return padRight(s, CommConstants.TELEGRAM_LENGTH, CommConstants.TELEGRAM_FILLER_CHARACTER);
    }
}
