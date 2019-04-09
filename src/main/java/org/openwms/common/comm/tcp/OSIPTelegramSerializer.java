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

import org.springframework.core.serializer.Serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * An OSIPTelegramSerializer is able to read OSIP telegram structures from an InputStream (deserialization) and can also serialize Object
 * structures into OSIP telegrams.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
public class OSIPTelegramSerializer implements Serializer<Map<?, ?>> {

    private static final byte[] CRLF = "\r\n".getBytes();

    /**
     * Writes the source object to an output stream using Java Serialization. The source object must implement {@link Serializable}.
     */
    @Override
    public void serialize(Map<?, ?> map, OutputStream outputStream) throws IOException {
        /*
        BufferedOutputStream os = new BufferedOutputStream(outputStream);
        Map<String, String> headers = (Map<String, String>) map.get("headers");
        String header = String.valueOf(headers.get(CommHeader.SYNC_FIELD_NAME)) +
                padLeft(String.valueOf(CommConstants.TELEGRAM_LENGTH), CommHeader.LENGTH_MESSAGE_LENGTH_FIELD, "0") +
                String.valueOf(headers.get(CommHeader.SENDER_FIELD_NAME)) +
                String.valueOf(headers.get(CommHeader.RECEIVER_FIELD_NAME) +
                        padLeft(String.valueOf(headers.get(CommHeader.SEQUENCE_FIELD_NAME)), CommHeader.LENGTH_SEQUENCE_NO_FIELD, "0"));
        String s = header + ((Payload) map.get("payload")).asString();
        if (s.length() > CommConstants.TELEGRAM_LENGTH) {
            throw new MessageMismatchException(format("Defined telegram length exceeded, size is [%d]", s.length()));
        }
        os.write(padRight(s, CommConstants.TELEGRAM_LENGTH, CommConstants.TELEGRAM_FILLER_CHARACTER).getBytes(Charset.defaultCharset()));
        os.write(CRLF);
        os.flush();

         */
    }
}
