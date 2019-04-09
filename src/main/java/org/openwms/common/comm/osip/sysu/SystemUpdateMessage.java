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
package org.openwms.common.comm.osip.sysu;

import org.openwms.common.comm.CommConstants;
import org.openwms.common.comm.Payload;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A SystemUpdateMessage reflects the OSIP SYSU telegram type and is used to change the state of a {@code LocationGroup}.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
//@Data
public class SystemUpdateMessage extends Payload implements Serializable {

    /** Message identifier {@value} . */
    public static final String IDENTIFIER = "SYSU";
    private String locationGroupName;

    private SystemUpdateMessage(Builder builder) {
        locationGroupName = builder.locationGroupName;
    }

    public String getType() {
        return IDENTIFIER;
    }

    public String getLocationGroupName() {
        return locationGroupName;
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
        return true;
    }

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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "SystemUpdateMessage{" +
                "identifier='" + IDENTIFIER + '\'' +
                ", locationGroup=" + locationGroupName +
                "} with " + super.toString();
    }
}
