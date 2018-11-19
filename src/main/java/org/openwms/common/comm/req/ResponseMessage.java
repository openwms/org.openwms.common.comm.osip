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
package org.openwms.common.comm.req;

import org.openwms.common.comm.ParserUtils;
import org.openwms.common.comm.Payload;

import java.io.Serializable;

/**
 * A ResponseMessage on {@link RequestMessage}s.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
public class ResponseMessage extends Payload implements Serializable {

    /** Message identifier {@value} . */
    public static final String IDENTIFIER = "RES_";

    private String barcode;
    private String actualLocation;
    private String targetLocation;
    private String targetLocationGroup;

    private ResponseMessage(Builder builder) {
        barcode = builder.barcode;
        actualLocation = builder.actualLocation;
        targetLocation = builder.targetLocation;
        targetLocationGroup = builder.targetLocationGroup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageIdentifier() {
        return IDENTIFIER;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getActualLocation() {
        return actualLocation;
    }

    public String getTargetLocation() {
        return targetLocation;
    }

    public String getTargetLocationGroup() {
        return targetLocationGroup;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString() + IDENTIFIER + getErrorCode() + ParserUtils.asString(super.getCreated());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWithoutReply() {
        return true;
    }

    @Override
    public String asString() {
        return IDENTIFIER + barcode + actualLocation + targetLocation + targetLocationGroup;
    }

    /**
     * {@code ResponseMessage} builder static inner class.
     */
    public static final class Builder {

        private String barcode;
        private String actualLocation;
        private String targetLocation;
        private String targetLocationGroup;

        public Builder() {
        }

        /**
         * Sets the {@code barcode} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code barcode} to set
         * @return a reference to this Builder
         */
        public Builder withBarcode(String val) {
            barcode = val;
            return this;
        }

        /**
         * Sets the {@code actualLocation} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code actualLocation} to set
         * @return a reference to this Builder
         */
        public Builder withActualLocation(String val) {
            actualLocation = val;
            return this;
        }

        /**
         * Sets the {@code targetLocation} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code targetLocation} to set
         * @return a reference to this Builder
         */
        public Builder withTargetLocation(String val) {
            targetLocation = val;
            return this;
        }

        /**
         * Sets the {@code targetLocationGroup} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code targetLocationGroup} to set
         * @return a reference to this Builder
         */
        public Builder withTargetLocationGroup(String val) {
            targetLocationGroup = val;
            return this;
        }

        /**
         * Returns a {@code ResponseMessage} built from the parameters previously set.
         *
         * @return a {@code ResponseMessage} built with parameters of this {@code ResponseMessage.Builder}
         */
        public ResponseMessage build() {
            return new ResponseMessage(this);
        }
    }
}
