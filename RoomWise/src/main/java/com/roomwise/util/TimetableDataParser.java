package com.roomwise.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads the supplied college timetable JSON files without adding a JSON-library
 * dependency. The source files contain one JSON array of flat objects.
 *
 * <p>This class is intentionally limited to the known timetable export format;
 * it is not a general-purpose JSON parser.</p>
 */
public final class TimetableDataParser {

    private TimetableDataParser() {
    }

    /**
     * Parses a timetable JSON array into records whose keys match the source
     * file's field names.
     *
     * @param source path to a theory or lab timetable JSON file
     * @return records in their source-file order
     * @throws IOException if the file cannot be read or is not a JSON array
     */
    public static List<Map<String, String>> parse(Path source) throws IOException {
        String json = Files.readString(source).trim();
        if (!json.startsWith("[") || !json.endsWith("]")) {
            throw new IOException("Timetable file must contain one JSON array: " + source);
        }

        List<Map<String, String>> records = new ArrayList<>();
        for (String object : splitObjects(json)) {
            records.add(parseFlatObject(object));
        }
        return records;
    }

    private static List<String> splitObjects(String json) throws IOException {
        List<String> objects = new ArrayList<>();
        int depth = 0;
        int start = -1;
        boolean inString = false;
        boolean escaped = false;

        for (int index = 0; index < json.length(); index++) {
            char current = json.charAt(index);
            if (inString) {
                if (escaped) {
                    escaped = false;
                } else if (current == '\\') {
                    escaped = true;
                } else if (current == '"') {
                    inString = false;
                }
                continue;
            }
            if (current == '"') {
                inString = true;
            } else if (current == '{') {
                if (depth++ == 0) {
                    start = index;
                }
            } else if (current == '}' && --depth == 0) {
                objects.add(json.substring(start, index + 1));
            }
        }
        if (depth != 0 || inString) {
            throw new IOException("Malformed JSON timetable array");
        }
        return objects;
    }

    private static Map<String, String> parseFlatObject(String object) {
        Map<String, String> values = new LinkedHashMap<>();
        Pattern field = Pattern.compile("\\\"([^\\\"]+)\\\"\\s*:\\s*(\\\"((?:\\\\.|[^\\\"])*)\\\"|null|true|false|-?\\d+(?:\\.\\d+)?)");
        Matcher matcher = field.matcher(object);
        while (matcher.find()) {
            String raw = matcher.group(2);
            String value;
            if ("null".equals(raw)) {
                value = null;
            } else if (matcher.group(3) != null) {
                value = unescape(matcher.group(3));
            } else {
                value = raw;
            }
            values.put(matcher.group(1), value);
        }
        return values;
    }

    private static String unescape(String value) {
        return value.replace("\\\\\"", "\"")
                .replace("\\\\\\\\", "\\\\")
                .replace("\\\\n", "\n")
                .replace("\\\\t", "\t");
    }
}
