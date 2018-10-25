/*
 * openwms.org, the Open Warehouse Management System.
 * Copyright (C) 2014 Heiko Scherrer
 *
 * This file is part of openwms.org.
 *
 * openwms.org is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * openwms.org is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openwms.common.comm.res;

import org.openwms.common.comm.CommConstants;
import org.openwms.common.comm.ParserUtils;
import org.openwms.common.comm.Payload;
import org.openwms.common.comm.req.spi.RequestFieldLengthProvider;

import java.io.Serializable;
import java.text.ParseException;

import static org.openwms.common.comm.ParserUtils.asDate;

/**
 * A OSIP ResponseMessage responds to an processed {@code RequestMessage}.
 *
 * See https://interface21-io.gitbook.io/osip/messaging-between-layer-n-and-layer-n-1#response-telegram-res_
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
        return IDENTIFIER + barcode +
                ParserUtils.nullableLocation(actualLocation) +
                ParserUtils.nullableLocation(targetLocation) +
                ParserUtils.nullableLocationGroup(targetLocationGroup) +
                getErrorCode() +
                ParserUtils.asString(super.getCreated());
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
        public Builder withCreateDate(String createDate) throws ParseException {
            responseMessage.setCreated(asDate(createDate));
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
