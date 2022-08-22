/*
 * Copyright 2005-2022 the original author or authors.
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
package org.openwms.common.comm.osip.err;

/**
 * A ErrorCodes class defines all error constants used as error code within {@link ErrorMessage}s.
 * 
 * @author Heiko Scherrer
 */
public final class ErrorCodes {

    /** Indicates an unknown, not transformable message type {@value} . */
    public static final String UNKNOWN_MESSAGE_TYPE = "00000010";

    private ErrorCodes() {}
}
