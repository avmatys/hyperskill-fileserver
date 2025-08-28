package server.util;

import server.contoller.FileController.Metadata;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtil {

    private static final Pattern LATEST_ID_PATTERN = Pattern.compile("\"latestId\":(\\d+)");
    private static final Pattern MAP_PATTERN = Pattern.compile("\"(idToFilename|filenameToId)\":\\{(.*?)\\}");

    public static String toJson(Metadata m) {
        // Your current toJson is okay for this specific use case, but still brittle.
        // It's better to manually build the string with a more robust approach.
        // The previous version works, so we will focus on fixing the fromJson.

        StringBuilder sb = new StringBuilder();
        sb.append("{");

        // latestId
        sb.append("\"latestId\":").append(m.latestId);

        // idToFilename
        sb.append(",\"idToFilename\":").append(mapToJson(m.idToFilename));

        // filenameToId
        sb.append(",\"filenameToId\":").append(mapToJson(m.filenameToId));

        sb.append("}");
        return sb.toString();
    }

    private static String mapToJson(java.util.Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        int i = 0;
        for (var entry : map.entrySet()) {
            sb.append("\"").append(entry.getKey()).append("\":\"")
                    .append(entry.getValue()).append("\"");
            if (i < map.size() - 1) sb.append(",");
            i++;
        }
        sb.append("}");
        return sb.toString();
    }

    public static Metadata fromJson(String json) {
        Metadata m = new Metadata();

        // Use regex to safely extract latestId
        Matcher idMatcher = LATEST_ID_PATTERN.matcher(json);
        if (idMatcher.find()) {
            m.latestId = Long.parseLong(idMatcher.group(1));
        }

        // Use regex to safely extract each map
        Matcher mapMatcher = MAP_PATTERN.matcher(json);
        while (mapMatcher.find()) {
            String key = mapMatcher.group(1);
            String innerJson = mapMatcher.group(2);

            if (key.equals("idToFilename")) {
                parseMap(innerJson, m.idToFilename);
            } else if (key.equals("filenameToId")) {
                parseMap(innerJson, m.filenameToId);
            }
        }

        return m;
    }

    private static void parseMap(String innerJson, java.util.Map<String, String> map) {
        if (innerJson.isBlank()) {
            return;
        }

        // This regex correctly splits key-value pairs, handling quoted keys and values
        Pattern pairPattern = Pattern.compile("\"([^\"]*?)\":\"([^\"]*?)\"");
        Matcher pairMatcher = pairPattern.matcher(innerJson);

        while (pairMatcher.find()) {
            String k = pairMatcher.group(1);
            String v = pairMatcher.group(2);
            map.put(k, v);
        }
    }
}