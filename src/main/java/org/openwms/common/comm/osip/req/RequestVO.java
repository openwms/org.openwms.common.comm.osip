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
package org.openwms.common.comm.osip.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * A RequestVO.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
public class RequestVO implements Serializable {

    @JsonProperty
    String type, actualLocation, targetLocation, targetLocationGroup, barcode;
    @JsonProperty
    RequestHeaderVO header;
    @JsonProperty
    String errorCode;
    @JsonProperty
    Date created;

    private RequestVO(Builder builder) {
        setType(builder.type);
        setActualLocation(builder.actualLocation);
        setTargetLocation(builder.targetLocation);
        setTargetLocationGroup(builder.targetLocationGroup);
        setBarcode(builder.barcode);
        setHeader(builder.header);
        setErrorCode(builder.errorCode);
        setCreated(builder.created);
    }

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

    public String getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(String targetLocation) {
        this.targetLocation = targetLocation;
    }

    public String getTargetLocationGroup() {
        return targetLocationGroup;
    }

    public void setTargetLocationGroup(String targetLocationGroup) {
        this.targetLocationGroup = targetLocationGroup;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public RequestHeaderVO getHeader() {
        return header;
    }

    public void setHeader(RequestHeaderVO header) {
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

    public static class RequestHeaderVO implements Serializable {
        @JsonProperty
        String sender, receiver, sequenceNo;

        private RequestHeaderVO(Builder builder) {
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

            public Builder() {
            }

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

            public RequestHeaderVO build() {
                return new RequestHeaderVO(this);
            }
        }
    }

    public static final class Builder {
        private String type;
        private String actualLocation;
        private String targetLocation;
        private String targetLocationGroup;
        private String barcode;
        private RequestHeaderVO header;
        private String errorCode;
        private Date created;

        public Builder() {
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Builder actualLocation(String val) {
            actualLocation = val;
            return this;
        }

        public Builder targetLocation(String val) {
            targetLocation = val;
            return this;
        }

        public Builder targetLocationGroup(String val) {
            targetLocationGroup = val;
            return this;
        }

        public Builder barcode(String val) {
            barcode = val;
            return this;
        }

        public Builder header(RequestHeaderVO val) {
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

        public RequestVO build() {
            return new RequestVO(this);
        }
    }
}
