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
 * A ParserUtils.
 *
 * @author <a href="mailto:hscherrer@interface21.io">Heiko Scherrer</a>
 */
public final class ParserUtils {

    /**
     * Pad a String {@code s} with a number {@code n} of characters {@code chr}.
     *
     * @param s The String to pad
     * @param n Number of digits in sum
     * @return The padded String
     */
    public static String padRight(String s, int n, String chr) {
        return String.format("%1$-" + n + "s", s).replace(" ", chr);
    }

    public static String trimRight(String s, char chr) {
        return s.substring(0, s.indexOf(chr));
    }

    /**
     * Pad a String {@code s} with a number {@code n} of characters {@code chr}.
     *
     * @param s The String to pad
     * @param n Number of digits in sum
     * @return The padded String
     */
    public static String padLeft(String s, int n, String chr) {
        return String.format("%1$" + n + "s", s).replace(" ", chr);
    }

    public static String nullableBarcode(String location) {
        if (location == null || location.isEmpty()) {
            return padLeft("", 20, "?");
        }
        return location.replace("/", "");
    }

    public static String nullableLocation(String location) {
        if (location == null || location.isEmpty()) {
            return padLeft("", 20, "*");
        }
        return location.replace("/", "");
    }

    public static String nullableLocationGroup(String locationGroup) {
        if (locationGroup == null || locationGroup.isEmpty()) {
            return padLeft("", 21, "*");
        }
        return locationGroup;
    }
}
