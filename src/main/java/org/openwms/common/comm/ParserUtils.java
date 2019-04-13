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

    private ParserUtils() {
    }

    /**
     * Pad the given String {@code s} with a number {@code n} of characters {@code chr}.
     *
     * @param s The String to pad
     * @param n Number of digits in sum
     * @return A new padded String instance
     */
    public static String padRight(String s, int n, String chr) {
        return String.format("%1$-" + n + "s", s).replace(" ", chr);
    }

    /**
     * Trim the given String {@code s} about the character {@code chr}.
     *
     * @param s The String to analyse
     * @param chr The character to remove from the right end sid
     * @return A new trimmed String instance
     */
    public static String trimRight(String s, char chr) {
        return s.substring(0, s.indexOf(chr));
    }

    /**
     * Pad a String {@code s} with a number {@code n} of characters {@code chr}.
     *
     * @param s The String to pad
     * @param n Number of digits in sum
     * @return A new padded String instance
     */
    public static String padLeft(String s, int n, String chr) {
        return String.format("%1$" + n + "s", s).replace(" ", chr);
    }

    /**
     * Return the standard telegram representation (???..) if the {@code barcode} is {@literal null}.
     *
     * @param barcode The Barcode to translate
     * @return The barcode or ????????????????????
     */
    public static String nullableBarcode(String barcode) {
        if (barcode == null || barcode.isEmpty()) {
            return padLeft("", 20, "?");
        }
        return barcode;
    }

    /**
     * Return the standard telegram representation (***..) if the {@code locationId} is
     * {@literal null}.
     *
     * @param locationId The LocationId to translate
     * @return The normalized LocationId or ********************
     */
    public static String nullableLocation(String locationId) {
        if (locationId == null || locationId.isEmpty()) {
            return padLeft("", 20, "*");
        }
        return locationId.replace("/", "");
    }

    /**
     * Return the standard telegram representation (***..) if the {@code locationGroup} is
     * {@literal null}.
     *
     * @param locationGroup The name of the LocationGroup to analyse
     * @return The normalized name or ********************
     */
    public static String nullableLocationGroup(String locationGroup) {
        if (locationGroup == null || locationGroup.isEmpty()) {
            return padLeft("", 20, "*");
        }
        return locationGroup;
    }
}
