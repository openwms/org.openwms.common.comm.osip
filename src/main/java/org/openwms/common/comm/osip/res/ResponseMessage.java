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

import org.openwms.common.comm.CommConstants;
import org.openwms.common.comm.Payload;
import org.openwms.common.comm.osip.req.spi.RequestFieldLengthProvider;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * A OSIP ResponseMessage responds to an processed {@code RequestMessage}.
 *
 * See https://interface21-io.gitbook.io/osip/messaging-between-layer-n-and-layer-n-1#response-telegram-res_
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
public class ResponseMessage extends Payload implements Serializable {

    /** Message identifier {@value} . */
    public static final String IDENTIFIER = "RES_";

    private String barcode;
    private String actualLocation;
    private String targetLocation;
    private String targetLocationGroup;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageIdentifier() {
        return IDENTIFIER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWithoutReply() {
        return true;
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

    public String getTargetLocationGroup() {
        return targetLocationGroup;
    }

    public void setTargetLocationGroup(String targetLocationGroup) {
        this.targetLocationGroup = targetLocationGroup;
    }

    public static class Builder {

        private final ResponseMessage responseMessage;
        private final RequestFieldLengthProvider provider;

        /**
         * Create a new RequestMessage.Builder.
         */
        public Builder(RequestFieldLengthProvider provider) {
            this.provider = provider;
            this.responseMessage = new ResponseMessage();
        }

        /**
         * Add an {@code Barcode} to the message.
         *
         * @param barcode The barcode
         * @return The builder
         */
        public Builder withBarcode(String barcode) {
            responseMessage.barcode = barcode;
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
            responseMessage.actualLocation = String.join("/",
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
                responseMessage.targetLocation = String.join("/",
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
                responseMessage.setErrorCode(errorCode);
            }
            return this;
        }

        /**
         * Add the date of creation in an expected format as defined in {@link CommConstants#DATE_FORMAT_PATTERN}.
         *
         * @param createDate The creation date as String
         * @return The builder
         */
        public Builder withCreateDate(String createDate, String pattern) throws ParseException {
            responseMessage.setCreated(new SimpleDateFormat(pattern).parse(createDate));
            return this;
        }

        /**
         * Finally build the message.
         *
         * @return The completed message
         */
        public ResponseMessage build() {
            return responseMessage;
        }
    }
}
