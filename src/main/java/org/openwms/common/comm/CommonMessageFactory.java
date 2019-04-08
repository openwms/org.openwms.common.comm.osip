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
package org.openwms.common.comm;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.openwms.common.comm.CommHeader.PREFIX;

/**
 * A CommonMessageFactory.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
public final class CommonMessageFactory {

    /**
     * Hide Constructor.
     */
    private CommonMessageFactory() {
    }

    /**
     * Create a {@link CommHeader} from a passed telegram structure.
     *
     * @param telegram The telegram
     * @return A {@link CommHeader} instance
     */
    public static CommHeader createHeader(String telegram) {
        String sync = telegram.substring(0, CommHeader.LENGTH_SYNC_FIELD);

        int start = sync.length();
        int end = start + CommHeader.LENGTH_MESSAGE_LENGTH_FIELD;
        short messageLength = Short.parseShort(telegram.substring(start, end));

        start = end;
        end += CommHeader.LENGTH_SENDER_FIELD;
        String sender = telegram.substring(start, end);

        start = end;
        end += CommHeader.LENGTH_RECEIVER_FIELD;
        String receiver = telegram.substring(start, end);

        start = end;
        end += CommHeader.LENGTH_SEQUENCE_NO_FIELD;
        short sequenceNo = Short.parseShort(telegram.substring(start, end));
        return new CommHeader(sync, messageLength, sender, receiver, sequenceNo);
    }

    public static MessageHeaders createHeaders(String telegram, Map<String, Object> headers) {
        String sync = telegram.substring(0, CommHeader.LENGTH_SYNC_FIELD);

        int start = sync.length();
        int end = start + CommHeader.LENGTH_MESSAGE_LENGTH_FIELD;
        short messageLength = Short.parseShort(telegram.substring(start, end));

        start = end;
        end += CommHeader.LENGTH_SENDER_FIELD;
        String sender = telegram.substring(start, end);

        start = end;
        end += CommHeader.LENGTH_RECEIVER_FIELD;
        String receiver = telegram.substring(start, end);

        start = end;
        end += CommHeader.LENGTH_SEQUENCE_NO_FIELD;
        short sequenceNo = Short.parseShort(telegram.substring(start, end));
        Map<String, Object> h = new HashMap<>(headers);
        h.put(CommHeader.SYNC_FIELD_NAME, sync);
        h.put(CommHeader.MSG_LENGTH_FIELD_NAME, messageLength);
        h.put(CommHeader.SENDER_FIELD_NAME, sender);
        h.put(CommHeader.RECEIVER_FIELD_NAME, receiver);
        h.put(CommHeader.SEQUENCE_FIELD_NAME, sequenceNo);
        return new MessageHeaders(h);
    }

    public static <T extends Payload> MessageHeaders getOSIPHeaders(Message<T> message) {
        return new MessageHeaders(message.getHeaders().entrySet().stream().filter(e -> e.getKey().startsWith(PREFIX)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }
}
