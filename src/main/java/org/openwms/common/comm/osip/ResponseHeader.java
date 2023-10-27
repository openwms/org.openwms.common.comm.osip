/*
 * Copyright 2005-2023 the original author or authors.
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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * A ResponseHeader.
 *
 * @author Heiko Scherrer
 */
public class ResponseHeader implements Serializable {

    @JsonProperty(OSIPHeader.SENDER_FIELD_NAME)
    private String sender;
    @JsonProperty(OSIPHeader.RECEIVER_FIELD_NAME)
    private String receiver;
    @JsonProperty(OSIPHeader.SEQUENCE_FIELD_NAME)
    private short sequenceNo;
    @JsonProperty(OSIPHeader.TENANT_FIELD_NAME)
    private String tenant;

    /*~------------ Constructors ------------*/
    private ResponseHeader() {
    }

    private ResponseHeader(Builder builder) {
        setSender(builder.sender);
        setReceiver(builder.receiver);
        setSequenceNo(builder.sequenceNo);
        setTenant(builder.tenant);
    }

    /*~------------ Methods ------------*/
    public static ResponseHeader emptyHeader() {
        return new ResponseHeader();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Map<String, Object> getAll() {
        Map<String, Object> result = new HashMap<>(3);
        result.put(OSIPHeader.SENDER_FIELD_NAME, sender);
        result.put(OSIPHeader.RECEIVER_FIELD_NAME, receiver);
        result.put(OSIPHeader.SEQUENCE_FIELD_NAME, sequenceNo);
        result.put(OSIPHeader.TENANT_FIELD_NAME, tenant);
        return result;
    }

    /*~------------ Accessors ------------*/
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

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    /*~------------ Builder ------------*/
    public static final class Builder {
        private String sender;
        private String receiver;
        private short sequenceNo;
        private String tenant;

        private Builder() {
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

        public Builder tenant(String val) {
            tenant = val;
            return this;
        }

        public ResponseHeader build() {
            return new ResponseHeader(this);
        }
    }

    /*~------------ Overrides ------------*/
    @Override
    public String toString() {
        return new StringJoiner(", ", ResponseHeader.class.getSimpleName() + "[", "]")
                .add("sender='" + sender + "'")
                .add("receiver='" + receiver + "'")
                .add("sequenceNo=" + sequenceNo)
                .add("tenant='" + tenant + "'")
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResponseHeader)) return false;
        ResponseHeader that = (ResponseHeader) o;
        return sequenceNo == that.sequenceNo &&
                Objects.equals(sender, that.sender) &&
                Objects.equals(receiver, that.receiver) &&
                Objects.equals(tenant, that.tenant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver, sequenceNo, tenant);
    }
}
