/*
 * Copyright 2005-2025 the original author or authors.
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

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.openwms.common.comm.ParserUtils.padLeft;

/**
 * A OSIPHeader represents the header part of a OSIP telegram message.
 *
 * @author Heiko Scherrer
 */
public class OSIPHeader implements Serializable {

    public static final String PREFIX = "osip_";
    private String sync;
    private short messageLength;
    private String sender;
    private String receiver;
    private short sequenceNo;

    public static final String SYNC_FIELD_NAME = PREFIX + "sync_field";
    public static final short LENGTH_SYNC_FIELD = 3;
    public static final String MSG_LENGTH_FIELD_NAME = PREFIX + "msg_length";
    public static final short LENGTH_MESSAGE_LENGTH_FIELD = 5;
    public static final String SENDER_FIELD_NAME = PREFIX + "sender";
    public static final short LENGTH_SENDER_FIELD = 5;
    public static final String RECEIVER_FIELD_NAME = PREFIX + "receiver";
    public static final short LENGTH_RECEIVER_FIELD = 5;
    public static final String SEQUENCE_FIELD_NAME = PREFIX + "sequenceno";
    public static final short LENGTH_SEQUENCE_NO_FIELD = 5;
    public static final String TENANT_FIELD_NAME = PREFIX + "tenant";

    public static final short LENGTH_HEADER =
                    LENGTH_SYNC_FIELD +
                    LENGTH_MESSAGE_LENGTH_FIELD +
                    LENGTH_RECEIVER_FIELD +
                    LENGTH_SENDER_FIELD +
                    LENGTH_SEQUENCE_NO_FIELD;

    private OSIPHeader(Builder builder) {
        sync = builder.sync;
        messageLength = builder.messageLength;
        setSender(builder.sender);
        setReceiver(builder.receiver);
        sequenceNo = builder.sequenceNo;
    }

    public Iterable<Map.Entry<String, Object>> getAll() {
        return Arrays.asList(
                new HashMap.SimpleEntry<>(SYNC_FIELD_NAME, sync),
                new HashMap.SimpleEntry<>(MSG_LENGTH_FIELD_NAME, messageLength),
                new HashMap.SimpleEntry<>(SENDER_FIELD_NAME, sender),
                new HashMap.SimpleEntry<>(RECEIVER_FIELD_NAME, receiver),
                new HashMap.SimpleEntry<>(SEQUENCE_FIELD_NAME, sequenceNo)
        );
    }

    /**
     * Get the sync.
     *
     * @return the sync.
     */
    public String getSync() {
        return sync;
    }

    /**
     * Get the messageLength.
     *
     * @return the messageLength.
     */
    public short getMessageLength() {
        return messageLength;
    }

    /**
     * Get the sender.
     *
     * @return the sender.
     */
    public String getSender() {
        return sender;
    }

    /**
     * Set the sender.
     *
     * @param sender The sender to set.
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Get the receiver.
     *
     * @return the receiver.
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * Set the receiver.
     *
     * @param receiver The receiver to set.
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    /**
     * Get the sequenceNo.
     *
     * @return the sequenceNo.
     */
    public short getSequenceNo() {
        return sequenceNo;
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((receiver == null) ? 0 : receiver.hashCode());
        result = prime * result + ((sender == null) ? 0 : sender.hashCode());
        result = prime * result + sequenceNo;
        result = prime * result + ((sync == null) ? 0 : sync.hashCode());
        result = prime * result + messageLength;
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OSIPHeader that = (OSIPHeader) o;
        return messageLength == that.messageLength && sequenceNo == that.sequenceNo && Objects.equals(sync, that.sync) && Objects.equals(sender, that.sender) && Objects.equals(receiver, that.receiver);
    }

    @Override
    public String toString() {
        return sync + padLeft(String.valueOf(messageLength), OSIPHeader.LENGTH_MESSAGE_LENGTH_FIELD, "0") + sender + receiver + padLeft(String.valueOf(sequenceNo), LENGTH_SEQUENCE_NO_FIELD, "0");
    }

    public static final class Builder {
        private String sync;
        private short messageLength;
        private String sender;
        private String receiver;
        private short sequenceNo;

        public Builder sync(String val) {
            sync = val;
            return this;
        }

        public Builder messageLength(short val) {
            messageLength = val;
            return this;
        }

        public Builder sender(String val) {
            sender = val;
            return this;
        }

        public Builder receiver(String val) {
            receiver = val;
            return this;
        }

        public Builder sequenceNo(short val) {
            sequenceNo = val;
            return this;
        }

        public OSIPHeader build() {
            return new OSIPHeader(this);
        }
    }
}
