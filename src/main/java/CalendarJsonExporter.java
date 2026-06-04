import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Exports a list of Game objects to a FullCalendar-compatible data.json file.
 *
 * Dependencies (add to pom.xml or build.gradle):
 *   Jackson: com.fasterxml.jackson.core:jackson-databind:2.15.2
 */
public class CalendarJsonExporter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Converts a list of Game objects to a FullCalendar-compatible JSON file.
     *
     * @param games    the list of games to export
     * @param filePath the output path, e.g. "data.json" or "docs/data.json"
     * @throws IOException if the file cannot be written
     */
    public void export(List<Game> games, String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode array = mapper.createArrayNode();

        for (Game game : games) {
            ObjectNode node = mapper.createObjectNode();

            node.put("title", buildTitle(game));
            node.put("start", formatStart(game.startingTime));

            if (game.matchDetails != null) {
                node.put("details", game.matchDetails.toString());
            }

            array.add(node);
        }

        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), array);
    }

    private String buildTitle(Game game) {
        String time = game.startingTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        return String.format("%s vs %s %s", game.location, game.opponent, time);
    }

    private String formatStart(LocalDateTime dt) {
        // Use date-only format if time is midnight, full datetime otherwise
        if (dt == null)
            throw new NullPointerException("datetime was null");
        return dt.format(DATE_FORMATTER);
    }
}
