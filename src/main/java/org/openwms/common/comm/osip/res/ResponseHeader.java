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
package org.openwms.common.comm.osip.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.openwms.common.comm.CommHeader;

import java.util.HashMap;
import java.util.Map;

/**
 * A ResponseHeader.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
public class ResponseHeader {

    @JsonProperty
    private String sender, receiver;
    @JsonProperty
    private short sequenceNo;

    public Map<String, Object> getAll() {
        Map<String, Object> result = new HashMap<>(3);
        result.put(CommHeader.SENDER_FIELD_NAME, sender);
        result.put(CommHeader.RECEIVER_FIELD_NAME, receiver);
        result.put(CommHeader.SEQUENCE_FIELD_NAME, sequenceNo);
        return result;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public short getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(short sequenceNo) {
        this.sequenceNo = sequenceNo;
    }
}
