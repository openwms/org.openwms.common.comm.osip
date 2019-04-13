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
package org.openwms.common.comm.osip.upd;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * A UpdateVO.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
class UpdateVO implements Serializable {

    @JsonProperty
    String type, actualLocation, barcode;
    @JsonProperty
    UpdateHeaderVO header;
    @JsonProperty
    String errorCode;
    @JsonProperty
    Date created;

    /*~------------ Constructors ------------*/
    private UpdateVO(Builder builder) {
        setType(builder.type);
        setActualLocation(builder.actualLocation);
        setBarcode(builder.barcode);
        setHeader(builder.header);
        setErrorCode(builder.errorCode);
        setCreated(builder.created);
    }

    /*~------------ Accessors ------------*/
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActualLocation() {
        return actualLocation;
    }

    public void setActualLocation(String actualLocation) {
        this.actualLocation = actualLocation;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public UpdateHeaderVO getHeader() {
        return header;
    }

    public void setHeader(UpdateHeaderVO header) {
        this.header = header;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    /*~------------ Inner Classes ------------*/
    static class UpdateHeaderVO {
        @JsonProperty
        String sender;
        @JsonProperty
        String receiver;
        @JsonProperty
        String sequenceNo;

        private UpdateHeaderVO(Builder builder) {
            setSender(builder.sender);
            setReceiver(builder.receiver);
            setSequenceNo(builder.sequenceNo);
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

        public String getSequenceNo() {
            return sequenceNo;
        }

        public void setSequenceNo(String sequenceNo) {
            this.sequenceNo = sequenceNo;
        }

        public static final class Builder {
            private String sender;
            private String receiver;
            private String sequenceNo;

            public Builder sender(String val) {
                sender = val;
                return this;
            }

            public Builder receiver(String val) {
                receiver = val;
                return this;
            }

            public Builder sequenceNo(String val) {
                sequenceNo = val;
                return this;
            }

            public UpdateHeaderVO build() {
                return new UpdateHeaderVO(this);
            }
        }
    }

    /*~------------ Builders ------------*/
    public static final class Builder {
        private String type;
        private String actualLocation;
        private String barcode;
        private UpdateHeaderVO header;
        private String errorCode;
        private Date created;

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Builder actualLocation(String val) {
            actualLocation = val;
            return this;
        }

        public Builder barcode(String val) {
            barcode = val;
            return this;
        }

        public Builder header(UpdateHeaderVO val) {
            header = val;
            return this;
        }

        public Builder errorCode(String val) {
            errorCode = val;
            return this;
        }

        public Builder created(Date val) {
            created = val;
            return this;
        }

        public UpdateVO build() {
            return new UpdateVO(this);
        }
    }

    /*~------------ Overrides ------------*/
    /**
     * {@inheritDoc}
     *
     * Include all fields.
     */
    @Override
    public String toString() {
        return new StringJoiner(", ", UpdateVO.class.getSimpleName() + "[", "]").add("type='" + type + "'").add("actualLocation='" + actualLocation + "'").add("barcode='" + barcode + "'").add("header=" + header).add("errorCode='" + errorCode + "'").add("created=" + created).toString();
    }

    /**
     * {@inheritDoc}
     *
     * Include all fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UpdateVO updateVO = (UpdateVO) o;
        return Objects.equals(type, updateVO.type) && Objects.equals(actualLocation, updateVO.actualLocation) && Objects.equals(barcode, updateVO.barcode) && Objects.equals(header, updateVO.header) && Objects.equals(errorCode, updateVO.errorCode) && Objects.equals(created, updateVO.created);
    }

    /**
     * {@inheritDoc}
     *
     * Include all fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, actualLocation, barcode, header, errorCode, created);
    }
}
