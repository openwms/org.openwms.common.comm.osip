/*
 * Copyright 2005-2020 the original author or authors.
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
package org.openwms.common.comm.osip.sysu;

import org.openwms.common.comm.CommConstants;
import org.openwms.common.comm.osip.Payload;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * A SystemUpdateMessage reflects the OSIP SYSU telegram type and is used to change the state of a {@code LocationGroup}.
 *
 * @author Heiko Scherrer
 */
public class SystemUpdateMessage extends Payload implements Serializable {

    /** Message identifier {@value} . */
    public static final String IDENTIFIER = "SYSU";
    private String locationGroupName;

    /*~------------ Constructors ------------*/
    private SystemUpdateMessage(Builder builder) {
        locationGroupName = builder.locationGroupName;
    }

    /*~------------ Accessors ------------*/
    public String getType() {
        return IDENTIFIER;
    }

    String getLocationGroupName() {
        return locationGroupName;
    }

    /*~------------ Builders ------------*/
    public static final class Builder {

        private String locationGroupName;
        private String errorCode;
        private Date created;

        public Builder withLocationGroupName(String locationGroupName) {
            this.locationGroupName = StringUtils.trimTrailingCharacter(locationGroupName, CommConstants.LOCGROUP_FILLER_CHARACTER);
            return this;
        }

        public Builder withErrorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder withCreateDate(String createDate, String pattern) throws ParseException {
            this.created = new SimpleDateFormat(pattern).parse(createDate);
            return this;
        }

        public SystemUpdateMessage build() {
            SystemUpdateMessage res = new SystemUpdateMessage(this);
            res.setErrorCode(this.errorCode);
            res.setCreated(this.created);
            return res;
        }
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
     * Include all fields.
     */
    @Override
    public String toString() {
        return new StringJoiner(", ", SystemUpdateMessage.class.getSimpleName() + "[", "]").add("locationGroupName='" + locationGroupName + "'").toString();
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
        if (!super.equals(o))
            return false;
        SystemUpdateMessage that = (SystemUpdateMessage) o;
        return Objects.equals(locationGroupName, that.locationGroupName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), locationGroupName);
    }
}
