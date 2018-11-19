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

import org.openwms.common.comm.CommConstants;
import org.openwms.common.comm.ParserUtils;
import org.openwms.common.comm.Payload;
import org.openwms.common.comm.req.spi.RequestFieldLengthProvider;

import java.io.Serializable;
import java.text.ParseException;

import static org.openwms.common.comm.ParserUtils.asDate;

/**
 * A RequestMessage requests an order for a TransportUnit with id <tt>Barcode</tt> on a particular location <tt>actualLocation</tt>.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
public class RequestMessage extends Payload implements Serializable {

    /** Message identifier {@value} . */
    public static final String IDENTIFIER = "REQ_";
    private String barcode;
    private String actualLocation;
    private String targetLocation;


    public RequestMessage() {
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

    public void setBarcode(String barcode) {
        this.barcode = barcode;
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

    public static class Builder {

        private final RequestMessage requestMessage;
        private final RequestFieldLengthProvider provider;

        /**
         * Create a new RequestMessage.Builder.
         */
        public Builder(RequestFieldLengthProvider provider) {
            this.provider = provider;
            this.requestMessage = new RequestMessage();
        }

        /**
         * Add an {@code Barcode} to the message.
         *
         * @param barcode The barcode
         * @return The builder
         */
        public Builder withBarcode(String barcode) {
            requestMessage.barcode = barcode;
            return this;
        }

        /**
         * Add an actual {@code Location} by the given unique {@code LocationPk} in an expected format like {@literal AAAAAAA/BBBBBB/...}.
         * Where the number of digits each coordinate has and the number of coordinates at all is defined by the {@code
         * RequestFieldLengthProvider}.
         *
         * @param actualLocation The String representation of {@code LocationPK} of the actual location
         * @return The builder
         */
        public Builder withActualLocation(String actualLocation) {
            requestMessage.actualLocation = String.join("/",
                    actualLocation.split("(?<=\\G.{" + provider.locationIdLength() / provider.noLocationIdFields() + "})"));
            return this;
        }

        /**
         * Add an target {@code Location} by the given unique {@code LocationPk} in an expected format like {@literal AAAAAAA/BBBBBB/...}.
         * Where the number of digits each coordinate has and the number of coordinates at all is defined by the {@code
         * RequestFieldLengthProvider}.
         *
         * @param targetLocation The String representation of {@code LocationPK} of the target location
         * @return The builder
         */
        public Builder withTargetLocation(String targetLocation) {
            if (exists(targetLocation)) {
                requestMessage.targetLocation = String.join("/",
                        targetLocation.split("(?<=\\G.{" + provider.locationIdLength() / provider.noLocationIdFields() + "})"));
            }
            return this;
        }

        /**
         * Add an error code.
         *
         * @param errorCode The error code
         * @return The builder
         */
        public Builder withErrorCode(String errorCode) {
            if (exists(errorCode)) {
                requestMessage.setErrorCode(errorCode);
            }
            return this;
        }

        /**
         * Add the date of creation in an expected format as defined in {@link CommConstants#DATE_FORMAT_PATTERN}.
         *
         * @param createDate The creation date as String
         * @return The builder
         */
        public Builder withCreateDate(String createDate) throws ParseException {
            requestMessage.setCreated(asDate(createDate));
            return this;
        }

        /**
         * Finally build the message.
         *
         * @return The completed message
         */
        public RequestMessage build() {
            return requestMessage;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString() + IDENTIFIER + this.barcode + this.actualLocation + this.targetLocation +
                getErrorCode() + ParserUtils.asString(super.getCreated());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWithoutReply() {
        return false;
    }

    /**
     * todo: This needs to be extracted from the business object to an 'transformer'.
     *
     * @return
     */
    @Override
    public String asString() {
        return IDENTIFIER + actualLocation + targetLocation;
    }
}
