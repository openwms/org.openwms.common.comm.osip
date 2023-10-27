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
package org.openwms.common.comm.osip.err.tcp;

import org.openwms.common.comm.TimeProvider;
import org.openwms.common.comm.config.Osip;
import org.openwms.common.comm.osip.OSIPComponent;
import org.openwms.common.comm.osip.OSIPSerializer;
import org.openwms.common.comm.osip.err.ErrorMessage;

import java.text.SimpleDateFormat;

/**
 * A ErrorMessageSerializer.
 *
 * @author Heiko Scherrer
 */
@OSIPComponent
public class ErrorMessageSerializer extends OSIPSerializer<ErrorMessage> {

    private final TimeProvider timeProvider;

    public ErrorMessageSerializer(Osip driver, TimeProvider timeProvider) {
        super(driver);
        this.timeProvider = timeProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageIdentifier() {
        return ErrorMessage.IDENTIFIER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String convert(ErrorMessage message) {
        return getMessageIdentifier() +
                message.getLocationGroupName() +
                message.getErrorCode() +
                new SimpleDateFormat(getDriver().getDatePattern()).format(timeProvider.now());
    }
}
