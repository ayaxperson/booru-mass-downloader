package io.github.ayaxperson.boorumassdownloader;

import java.util.LinkedHashMap;
import java.util.Map;

public class Utils {

    public static Map<String, String> parsePair(final String input) {
        final Map<String, String> result = new LinkedHashMap<>();

        if (input == null || input.isEmpty())
            return result;

        final StringBuilder key = new StringBuilder();
        final StringBuilder value = new StringBuilder();

        boolean inValue = false;
        boolean escaped = false;

        for (int i = 0; i < input.length(); i++) {
            final char c = input.charAt(i);

            if (escaped) {
                (inValue ? value : key).append(c);
                escaped = false;
                continue;
            }

            if (c == '\\') {
                escaped = true;
                continue;
            }

            if (c == '=' && !inValue) {
                inValue = true;
                continue;
            }

            if (c == ';') {
                if (inValue) {
                    result.put(key.toString(), value.toString());
                }
                key.setLength(0);
                value.setLength(0);
                inValue = false;
                continue;
            }

            (inValue ? value : key).append(c);
        }

        // Handle a trailing backslash as a literal character
        if (escaped) {
            (inValue ? value : key).append('\\');
        }

        // Handle the final entry
        if (inValue) {
            result.put(key.toString(), value.toString());
        }

        return result;
    }

}
