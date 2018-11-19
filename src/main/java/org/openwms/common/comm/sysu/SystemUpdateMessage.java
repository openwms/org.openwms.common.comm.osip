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
package org.openwms.common.comm.sysu;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.openwms.common.comm.CommConstants;
import org.openwms.common.comm.Payload;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import static org.openwms.common.comm.ParserUtils.asDate;

/**
 * A SystemUpdateMessage reflects the OSIP SYSU telegram type and is used to change the state of a {@code LocationGroup}.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
//@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemUpdateMessage extends Payload implements Serializable {

    /** Message identifier {@value} . */
    public static final String IDENTIFIER = "SYSU";
    private String locationGroupName;

    private SystemUpdateMessage(Builder builder) {
        locationGroupName = builder.locationGroupName;
    }

    /**
     * Get the name of the LocationGroup.
     *
     * @return The name
     */
    public String getLocationGroupName() {
        return locationGroupName;
    }

    public void setLocationGroupName(String locationGroupName) {
        this.locationGroupName = locationGroupName;
    }

    /**
     * Subclasses have to return an unique, case-sensitive message identifier.
     *
     * @return The message TYPE field (see OSIP specification)
     */
    @Override
    public String getMessageIdentifier() {
        return IDENTIFIER;
    }

    /**
     * Does this type of message needs to be replied to?
     *
     * @return {@literal true} no reply needed, otherwise {@literal false}
     */
    @Override
    public boolean isWithoutReply() {
        return false;
    }


    /**
     * {@code SystemUpdateMessage} builder static inner class.
     */
    public static final class Builder {

        private String locationGroupName;
        private String errorCode;
        private Date created;

        /**
         * Sets the {@code locationGroupName} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param locationGroupName the {@code locationGroupName} to set
         * @return a reference to this Builder
         */
        public Builder withLocationGroupName(String locationGroupName) {
            this.locationGroupName = StringUtils.trimTrailingCharacter(locationGroupName, CommConstants.LOCGROUP_FILLER_CHARACTER);
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


        /**
         * Returns a {@code SystemUpdateMessage} built from the parameters previously set.
         *
         * @return a {@code SystemUpdateMessage} built with parameters of this {@code SystemUpdateMessage.Builder}
         */
        public SystemUpdateMessage build() {
            SystemUpdateMessage res = new SystemUpdateMessage(this);
            res.setErrorCode(this.errorCode);
            res.setCreated(this.created);
            return res;
        }
    }

    @Override
    public String asString() {
        return IDENTIFIER + locationGroupName;
    }

    @Override
    public String toString() {
        return "SystemUpdateMessage{" +
                "identifier='" + IDENTIFIER + '\'' +
                ", locationGroup=" + locationGroupName +
                "} with " + super.toString();
    }
}
