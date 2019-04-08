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
 * A final CommConstants class aggregates common used data and formatting types and provides useful conversation methods.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
public final class CommConstants {

    /** The date format used in messages. */
    public static final String DATE_FORMAT_PATTERN = "yyyyMMddHHmmss";
    /** Defined telegram length. Validated in Serializer! */
    @Deprecated
    public static final int TELEGRAM_LENGTH = 160;
    /** Used as suffix to create channels dynamically. */
    public static final String CHANNEL_SUFFIX = "MessageInputChannel";

    /** The character String used to pad LocationGroup fields till the defined length. */
    public static final char LOCGROUP_FILLER_CHARACTER = '_';
    /** The character String used to pad till the defined telegram length. */
    public static final String TELEGRAM_FILLER_CHARACTER = "*";

    /** Used in logging configuration to extract the telegram type. */
    public static final String LOG_TELEGRAM_TYPE = "TelegramType";
    /** Used in logging configuration to log the current configured tenant. */
    public static final String LOG_TENANT = "Tenant";
    /** Logging category to log telegrams only. */
    public static final String CORE_INTEGRATION_MESSAGING = "CORE_INTEGRATION_MESSAGING";

    private CommConstants() {
    }
}
