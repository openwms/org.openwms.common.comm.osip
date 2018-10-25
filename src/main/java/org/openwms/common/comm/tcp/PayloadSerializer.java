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
package org.openwms.common.comm.tcp;

import org.openwms.common.comm.CommConstants;
import org.openwms.common.comm.CommHeader;
import org.openwms.common.comm.MessageMismatchException;
import org.openwms.common.comm.MessageProcessingException;
import org.openwms.common.comm.Payload;
import org.openwms.common.comm.api.CustomServiceActivator;
import org.openwms.common.comm.res.ResponseMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.serializer.Serializer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.openwms.common.comm.ParserUtils.padRight;

/**
 * A PayloadSerializer.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Component
public class PayloadSerializer<T extends Payload> implements Serializer<T> {

    private static final byte[] CRLF = "\r\n".getBytes();
    private final List<OSIPSerializer<T>> serializers;
    private Map<String, OSIPSerializer<T>> serializersMap;

    public PayloadSerializer(List<OSIPSerializer<T>> serializers) {
        this.serializers = serializers;
    }

    @PostConstruct
    void onPostConstruct() {
        serializersMap = serializers.stream().collect(Collectors.toMap(OSIPSerializer::getMessageIdentifier, p -> p));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(T obj, OutputStream outputStream) throws IOException {
        BufferedOutputStream os = new BufferedOutputStream(outputStream);

        OSIPSerializer<T> serializer = serializersMap.get(obj.getMessageIdentifier());
        if (serializer == null) {
            throw new MessageProcessingException(format("No serializer for message of type [%s] registered", obj.getMessageIdentifier()));
        }

        String res = serializer.serialize(obj);

        os.write(padRight(res, CommConstants.TELEGRAM_LENGTH, CommConstants.TELEGRAM_FILLER_CHARACTER).getBytes(Charset.defaultCharset()));
        os.write(CRLF);
        os.flush();
    }
}
