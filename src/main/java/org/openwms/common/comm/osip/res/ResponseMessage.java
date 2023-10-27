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
package org.openwms.common.comm.osip.res;

import org.openwms.common.comm.osip.Payload;
import org.openwms.common.comm.spi.FieldLengthProvider;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * A OSIP ResponseMessage responds to an processed {@code RequestMessage}.
 *
 * See https://interface21-io.gitbook.io/osip/messaging-between-layer-n-and-layer-n-1#response-telegram-res_
 *
 * @author Heiko Scherrer
 */
public class ResponseMessage extends Payload implements Serializable {

    /** Message identifier {@value} . */
    public static final String IDENTIFIER = "RES_";

    private String barcode;
    private String actualLocation;
    private String targetLocation;
    private String targetLocationGroup;

    /*~------------ Accessors ------------*/
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

    /*~------------ Overrides ------------*/
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

    /**
     * {@inheritDoc}
     *
     * Use all fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        ResponseMessage that = (ResponseMessage) o;
        return Objects.equals(barcode, that.barcode) && Objects.equals(actualLocation, that.actualLocation) && Objects.equals(targetLocation, that.targetLocation) && Objects.equals(targetLocationGroup, that.targetLocationGroup);
    }

    /**
     * {@inheritDoc}
     *
     * Use all fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), barcode, actualLocation, targetLocation, targetLocationGroup);
    }

    /**
     * {@inheritDoc}
     *
     * Use all fields.
     */
    @Override
    public String toString() {
        return new StringJoiner(", ", ResponseMessage.class.getSimpleName() + "[", "]").add("barcode='" + barcode + "'").add("actualLocation='" + actualLocation + "'").add("targetLocation='" + targetLocation + "'").add("targetLocationGroup='" + targetLocationGroup + "'").toString();
    }

    /*~------------ Builders ------------*/
    public static class Builder {

        private final ResponseMessage responseMessage;
        private final FieldLengthProvider provider;

        public Builder(FieldLengthProvider provider) {
            this.provider = provider;
            this.responseMessage = new ResponseMessage();
        }

        public Builder withBarcode(String barcode) {
            responseMessage.barcode = barcode;
            return this;
        }

        /**
         * Add an actual {@code Location} by the given unique {@code LocationPk} in an expected format like {@literal AAAAAAA/BBBBBB/...}.
         * Where the number of digits each coordinate has and the number of coordinates at all is defined by the {@code
         * FieldLengthProvider}.
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
         * FieldLengthProvider}.
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

        public Builder withTargetLocationGroup(String targetLocationGroup) {
            if (exists(targetLocationGroup)) {
                responseMessage.setTargetLocationGroup(targetLocationGroup);
            }
            return this;
        }

        public Builder withErrorCode(String errorCode) {
            if (exists(errorCode)) {
                responseMessage.setErrorCode(errorCode);
            }
            return this;
        }

        public Builder withCreateDate(String createDate, String pattern) throws ParseException {
            responseMessage.setCreated(new SimpleDateFormat(pattern).parse(createDate));
            return this;
        }

        public ResponseMessage build() {
            return responseMessage;
        }
    }
}
