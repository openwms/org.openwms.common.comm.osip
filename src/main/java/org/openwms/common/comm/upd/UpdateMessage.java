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
package org.openwms.common.comm.upd;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.openwms.common.comm.CommConstants;
import org.openwms.common.comm.ParserUtils;
import org.openwms.common.comm.Payload;
import org.openwms.common.comm.upd.spi.UpdateFieldLengthProvider;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import static org.openwms.common.comm.ParserUtils.asDate;

/**
 * A UpdateMessage reflects the OSIP UPD telegram type and is used to change the state of a {@code LocationGroup}.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMessage extends Payload implements Serializable {

    /** Message identifier {@value} . */
    public static final String IDENTIFIER = "UPD_";
    private String barcode;
    private String actualLocation;

    private UpdateMessage(Builder builder) {
        barcode = builder.barcode;
        actualLocation = builder.actualLocation;
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
        return false;
    }


    /**
     * {@code UpdateMessage} builder static inner class.
     */
    public static final class Builder {

        private String barcode;
        private String actualLocation;
        private String errorCode;
        private Date created;
        private final UpdateFieldLengthProvider provider;

        /**
         * Create a new RequestMessage.Builder.
         */
        public Builder(UpdateFieldLengthProvider provider) {
            this.provider = provider;
        }

        /**
         * Add an {@code Barcode} to the message.
         *
         * @param barcode The barcode
         * @return The builder
         */
        public Builder withBarcode(String barcode) {
            this.barcode = barcode;
            return this;
        }

        public Builder withActualLocation(String actualLocation) {
            this.actualLocation = String.join("/",
                    actualLocation.split("(?<=\\G.{" + provider.locationIdLength() / provider.noLocationIdFields() + "})"));
            return this;
        }

        /**
         * Add an error code.
         *
         * @param errorCode
         *            The error code
         * @return The builder
         */
        public Builder withErrorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        /**
         * Add the date of creation in an expected format as defined in {@link CommConstants#DATE_FORMAT_PATTERN}.
         *
         * @param createDate
         *            The creation date
         * @return The builder
         */
        public Builder withCreateDate(String createDate) throws ParseException {
            this.created = asDate(createDate);
            return this;
        }

        public UpdateMessage build() {
            UpdateMessage res = new UpdateMessage(this);
            res.setBarcode(this.barcode);
            res.setActualLocation(this.actualLocation);
            res.setErrorCode(this.errorCode);
            res.setCreated(this.created);
            return res;
        }
    }

    @Override
    public String asString() {
        return IDENTIFIER + barcode +
                ParserUtils.nullableLocation(actualLocation) +
                getErrorCode() +
                ParserUtils.asString(super.getCreated());
    }

    @Override
    public String toString() {
        return "UpdateMessage{" + "barcode='" + barcode + '\'' + ", actualLocation='" + actualLocation + '\'' + "} " + super.toString();
    }
}
