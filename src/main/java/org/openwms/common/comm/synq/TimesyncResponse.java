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
package org.openwms.common.comm.synq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.openwms.common.comm.ParserUtils;
import org.openwms.common.comm.Payload;

import java.io.Serializable;
import java.util.Date;

/**
 * A TimesyncResponse.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Data
@Builder
@Slf4j
@AllArgsConstructor
class TimesyncResponse extends Payload implements Serializable {

    /** Message identifier {@value} . */
    public static final String IDENTIFIER = "SYNC";
    private Date senderTimer;

    /**
     * Create a new CommonMessage.
     */
    TimesyncResponse() {
        super();
        senderTimer = new Date();
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

    @Override
    public String asString() {
        return IDENTIFIER + ParserUtils.asString(senderTimer);
    }

    @Override
    public String toString() {
        return IDENTIFIER + ParserUtils.asString(senderTimer);
    }
}
