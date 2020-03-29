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
package org.openwms.common.comm.osip;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.openwms.common.comm.osip.OSIPHeader.PREFIX;

/**
 * A CommonMessageFactory.
 *
 * @author Heiko Scherrer
 */
public final class CommonMessageFactory {

    private CommonMessageFactory() {
    }

    /**
     * Create a {@link OSIPHeader} from a passed telegram structure.
     *
     * @param telegram The telegram
     * @return A {@link OSIPHeader} instance
     */
    public static OSIPHeader createHeader(String telegram) {
        String sync = telegram.substring(0, OSIPHeader.LENGTH_SYNC_FIELD);

        int start = sync.length();
        int end = start + OSIPHeader.LENGTH_MESSAGE_LENGTH_FIELD;
        short messageLength = Short.parseShort(telegram.substring(start, end));

        start = end;
        end += OSIPHeader.LENGTH_SENDER_FIELD;
        String sender = telegram.substring(start, end);

        start = end;
        end += OSIPHeader.LENGTH_RECEIVER_FIELD;
        String receiver = telegram.substring(start, end);

        start = end;
        end += OSIPHeader.LENGTH_SEQUENCE_NO_FIELD;
        short sequenceNo = Short.parseShort(telegram.substring(start, end));
        return new OSIPHeader.Builder()
                .sync(sync)
                .messageLength(messageLength)
                .sender(sender)
                .receiver(receiver)
                .sequenceNo(sequenceNo)
                .build();
    }

    public static MessageHeaders createHeaders(String telegram, Map<String, Object> headers) {
        String sync = telegram.substring(0, OSIPHeader.LENGTH_SYNC_FIELD);

        int start = sync.length();
        int end = start + OSIPHeader.LENGTH_MESSAGE_LENGTH_FIELD;
        short messageLength = Short.parseShort(telegram.substring(start, end));

        start = end;
        end += OSIPHeader.LENGTH_SENDER_FIELD;
        String sender = telegram.substring(start, end);

        start = end;
        end += OSIPHeader.LENGTH_RECEIVER_FIELD;
        String receiver = telegram.substring(start, end);

        start = end;
        end += OSIPHeader.LENGTH_SEQUENCE_NO_FIELD;
        short sequenceNo = Short.parseShort(telegram.substring(start, end));
        Map<String, Object> h = new HashMap<>(headers);
        h.put(OSIPHeader.SYNC_FIELD_NAME, sync);
        h.put(OSIPHeader.MSG_LENGTH_FIELD_NAME, messageLength);
        h.put(OSIPHeader.SENDER_FIELD_NAME, sender);
        h.put(OSIPHeader.RECEIVER_FIELD_NAME, receiver);
        h.put(OSIPHeader.SEQUENCE_FIELD_NAME, sequenceNo);
        return new MessageHeaders(h);
    }

    public static <T extends Payload> MessageHeaders getOSIPHeaders(Message<T> message) {
        return new MessageHeaders(message.getHeaders().entrySet().stream().filter(e -> e.getKey().startsWith(PREFIX)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }
}
