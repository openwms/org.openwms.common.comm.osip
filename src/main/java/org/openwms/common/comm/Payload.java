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
package org.openwms.common.comm;

import org.openwms.common.comm.res.ResponseHeader;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * A Payload is the abstract superclass of all messages sent to subsystems like PLC or ERP.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
public abstract class Payload implements Serializable {

    private ResponseHeader header;
    private String errorCode;
    private Date created;

    public static final short ERROR_CODE_LENGTH = 8;
    public static final short DATE_LENGTH = 14;
    public static final int MESSAGE_IDENTIFIER_LENGTH = 4;

    public ResponseHeader getHeader() {
        if (header == null) {
            this.header = new ResponseHeader();
        }
        return header;
    }

    /**
     * Subclasses have to return an unique, case-sensitive message identifier.
     * 
     * @return The message TYPE field (see OSIP specification)
     */
    public abstract String getMessageIdentifier();

    /**
     * Does this type of message needs to be replied to?
     * 
     * @return {@literal true} no reply needed, otherwise {@literal false}
     */
    public abstract boolean isWithoutReply();

    /**
     * Get the errorCode.
     * 
     * @return the errorCode.
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Checks wether the {@code errorCode} is not {@literal null}.
     *
     * @return {@literal true} if errorCode is set, otherwise {@literal false}
     */
    public boolean hasErrorCode() {
        return errorCode != null && !errorCode.isEmpty();
    }

    /**
     * Set the errorCode.
     * 
     * @param errorCode
     *            The errorCode to set.
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Get the created.
     * 
     * @return the created.
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Set the created.
     * 
     * @param created
     *            The created to set.
     */
    protected void setCreated(Date created) {
        this.created = created;
    }

    /**
     * Checks if the given {@code str} starts with an '*'. An optional telegram value contains '*' only.
     *
     * @param str to check
     * @return true if set
     */
    public static boolean exists(String str) {
        return !str.startsWith("*");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payload payload = (Payload) o;
        return Objects.equals(errorCode, payload.errorCode) &&
                Objects.equals(created, payload.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(errorCode, created);
    }

    /**
     * todo: This needs to be extracted from the business object to an 'transformer'.
     * @return
     */
    public abstract String asString();
}
