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
package org.openwms.common.comm.locu;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.openwms.common.comm.CommConstants;
import org.openwms.common.comm.Payload;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

import static org.openwms.common.comm.ParserUtils.asDate;

/**
 * A LocationUpdateMessage.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@AllArgsConstructor
@NoArgsConstructor
public class LocationUpdateMessage extends Payload implements Serializable {

    /** Message identifier {@value} . */
    public static final String IDENTIFIER = "LOCU";
    private String type;
    private String location;
    private String locationGroupName;

    private LocationUpdateMessage(Builder builder) {
        setErrorCode(builder.errorCode);
        setCreated(builder.created);
        type = builder.type;
        location = builder.location;
        locationGroupName = builder.locationGroupName;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String asString() {
        return null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationGroupName() {
        return locationGroupName;
    }

    public void setLocationGroupName(String locationGroupName) {
        this.locationGroupName = locationGroupName;
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
        LocationUpdateMessage that = (LocationUpdateMessage) o;
        return Objects.equals(type, that.type) && Objects.equals(location, that.location) && Objects.equals(locationGroupName, that.locationGroupName);
    }

    /**
     * {@inheritDoc}
     *
     * Use all fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type, location, locationGroupName);
    }

    public static final class Builder {
        private String errorCode;
        private Date created;
        private String type;
        private String location;
        private String locationGroupName;

        public Builder withErrorCode(String val) {
            errorCode = val;
            return this;
        }

        public Builder withCreated(String val) throws ParseException {
            created = asDate(val);
            return this;
        }

        public Builder withType(String val) {
            type = val;
            return this;
        }

        public Builder withLocation(String val) {
            location = String.join("/", val.split("(?<=\\G.{" + 4 + "})"));
            return this;
        }

        public Builder withLocationGroupName(String val) {
            locationGroupName = StringUtils.trimTrailingCharacter(val, CommConstants.LOCGROUP_FILLER_CHARACTER);
            return this;
        }

        public LocationUpdateMessage build() {
            return new LocationUpdateMessage(this);
        }
    }
}
