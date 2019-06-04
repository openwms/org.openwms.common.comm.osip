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

/**
 * A final CommConstants class collects common used data and formatting types.
 *
 * @author Heiko Scherrer
 */
public final class CommConstants {

    /** Used as suffix to create channels dynamically. */
    public static final String CHANNEL_SUFFIX = "MessageInputChannel";

    /** The character String used to pad LocationGroup fields till the defined length. */
    public static final char LOCGROUP_FILLER_CHARACTER = '_';

    /** Used in logging configuration to extract the telegram type. */
    public static final String LOG_TELEGRAM_TYPE = "TelegramType";
    /** Logging category to log telegrams only. */
    public static final String CORE_INTEGRATION_MESSAGING = "CORE_INTEGRATION_MESSAGING";
    /** Prefix used to identify the outbound channel. */
    public static final String PREFIX_CONNECTION_FACTORY = "connectionFactory_";
    /** Suffix used to identify the outbound channel. */
    public static final String SUFFIX_OUTBOUND = "_outbound";
    /** Suffix used to identify the inbound channel. */
    public static final String SUFFIX_INBOUND = "_inbound";

    private CommConstants() {
    }
}
