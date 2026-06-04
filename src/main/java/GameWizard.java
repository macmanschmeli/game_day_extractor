import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GameWizard {
    private final Element element;
    private static final Logger logger = Logger.getLogger(GameWizard.class.getName());
    private final String filter;

    private static final String NUMBERREGEX = "[^\\d]*";
    private static final String MATCHNUMBERTAG = "match-number";
    private static final String NAMETAG = "name";
    private static final String TIMETAG = "span";
    private static final String HREFTAG = "href";
    private static final String LOCATIONTAG = "details date";
    private static final String GAMETAG="table-row";

    GameWizard(Element element, String filter) {
        this.element = element;
        this.filter = filter;
    }

    public Game extract() {
        Game game = new Game();

        insertMatchNumer(game);
        if (game.matchNumber < 0) return null;

        insertOpponentName(game);
        if (isNullOrEmpty(game.opponent)) return null;

        insertHomeAway(game);
        insertStartingTimeFromHeader(game);

        return game;
    }

    private void insertStartingTimeFromHeader(Game game) {
        // Walk up to find the nearest parent that contains an <h3> sibling

        Element h3 = element.selectFirst("h3");
        if (h3 == null) {
            logger.log(Level.WARNING, "No <h3> date header found for match {0}", game.matchNumber);
            game.startingTime = null;
            return;
        }

        String rawDate = h3.text().trim(); // e.g. "24 Januar 2026, Samstag"
        // Strip the weekday suffix (", Samstag") if present
        String datePart = rawDate.contains(",") ? rawDate.substring(0, rawDate.indexOf(",")).trim() : rawDate;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.GERMAN);
        try {
            // Date only — set time to midnight as a neutral default
            game.startingTime = LocalDateTime.parse(datePart + " 00:00",
                    DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm", Locale.GERMAN));
        } catch (DateTimeParseException e) {
            logger.log(Level.WARNING, "Unable to parse date from header string \"{0}\" for match {1}",
                    new Object[]{rawDate, game.matchNumber});
            game.startingTime = null;
        }
    }

    private void insertHomeAway(Game game) {
        Element homeEl = element.selectFirst(".rival.home .name");
        Element awayEl = element.selectFirst(".rival.away .name");

        if (homeEl == null || awayEl == null) {
            logger.log(Level.WARNING, "Could not find home/away name elements for match {0}", game.matchNumber);
            game.location = null;
            return;
        }

        String homeName = homeEl.text().trim();
        String awayName = awayEl.text().trim();

        // Reuse the filter to determine which side our club is on
        boolean weAreHome = homeName.toLowerCase().contains(filter.toLowerCase());
        boolean weAreAway = awayName.toLowerCase().contains(filter.toLowerCase());

        if (!weAreHome && !weAreAway) {
            logger.log(Level.INFO, "Neither home ({0}) nor away ({1}) matches filter \"{2}\" for match {3}",
                    new Object[]{homeName, awayName, filter, game.matchNumber});
            return;
        }

        // Store home/away context in location field as a readable label
        game.location = weAreHome ? "Heim" : "Auswärts";
    }

    private void insertDetailsFromSubPage(Game game) {
        Document doc = getMatchDetails(game.matchDetails);
        if (doc == null) {
            logger.log(Level.WARNING, "unable to fetch HTML for URL {0}", game.matchDetails);
            game.location = null;
            game.startingTime = null;
            return;
        }
        insertGameLocation(doc, game);

        insertGameStartingTime(doc, game);
    }

    private void insertGameStartingTime(Document doc, Game game) {
        Elements elements = doc.getElementsByClass(LOCATIONTAG);
        if (elements.isEmpty()) {
            logger.log(Level.WARNING, "tag {0} for details of match {1} with URL {2} was not found", new Object[]{LOCATIONTAG, game.matchNumber, game.matchDetails});
            game.startingTime = null;
            return;
        }
        elements = elements.first().getElementsByTag(TIMETAG);
        if (elements.isEmpty()) {
            logger.log(Level.WARNING, "tag {0} for details of match {1} with URL {2} was not found", new Object[]{TIMETAG, game.matchNumber, game.matchDetails});
            game.startingTime = null;
            return;
        }
        String dateTime = elements.first().text();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", Locale.GERMAN);
        try {
            game.startingTime = LocalDateTime.parse(dateTime, formatter);
        } catch (DateTimeParseException e) {
            logger.log(Level.WARNING, "Unable to parse DateTime for String {0}", dateTime);
            game.startingTime = null;
        }
    }

    private void insertGameLocation(Document doc, Game game) {
        Elements elements = doc.getElementsByClass(LOCATIONTAG);
        if (elements.isEmpty()) {
            logger.log(Level.WARNING, "tag {0} for details of match {1} with URL {2} was not found", new Object[]{LOCATIONTAG, game.matchNumber, game.matchDetails});
            game.location = null;
        }
        game.location = getMatchLocation(elements);
    }

    private void insertMatchDetails(Game game) {
        if (!element.hasAttr(HREFTAG)) {
            game.matchDetails = null;
            return;
        }
        try {
            game.matchDetails = new URI(element.attr(HREFTAG));
        } catch (URISyntaxException e) {
            logger.log(Level.WARNING, "URL {0} for details of match {1} has an invalid format", new Object[]{element.attr(HREFTAG), game.matchNumber});
            game.matchDetails = null;
        }
    }

    private void insertOpponentName(Game game) {
        Elements elements = element.getElementsByClass(NAMETAG);
        game.opponent = extractOpponentName(elements);
    }

    private void insertMatchNumer(Game game) {
        Elements elements = element.getElementsByClass(MATCHNUMBERTAG);
        game.matchNumber = extractMatchNumber(elements);
    }

    private String getMatchLocation(Elements elements) {
        String location = elements.getFirst().text();
        Pattern pattern = Pattern.compile("(?<=,)[^,]*(?=,)");
        Matcher matcher = pattern.matcher(location);
        if (!matcher.find()) {
            logger.log(Level.WARNING, "no location found in string {0}", location);
            return null;
        }
        return matcher.group();
    }


    private String extractOpponentName(Elements elements) {
        if (elements != null && elements.size() > 1) {
            Element clubOne = elements.get(0);
            Element clubTwo = elements.get(1);
            String clubOneName = clubOne.text();
            String clubTwoName = clubTwo.text();
            if (isNullOrEmpty(clubOneName) || isNullOrEmpty(clubTwoName)) {
                logger.log(Level.WARNING, "html node {0} with tag {1} has no value", new Object[]{element.text(), NAMETAG});
                return null;
            }
            if (clubOneName.toLowerCase().contains(filter.toLowerCase())) {
                return clubTwoName;
            }
            if (clubTwoName.toLowerCase().contains(filter.toLowerCase())) {
                return clubOneName;
            }
            logger.log(Level.INFO, "html node {0} does not contain a game regarding {1}", new Object[]{element.text(), filter});
            return null;


        } else {
            logger.log(Level.WARNING, "html node {0} has no tag named {1}", new Object[]{element.text(), NAMETAG});
            return null;
        }

    }

    private int extractMatchNumber(Elements elements) {
        if (elements != null && !elements.isEmpty()) {
            Element matchnr = elements.getFirst();
            String value = matchnr.text();
            if (value == null) {
                logger.log(Level.WARNING, "html node {0} with tag {1} has no value", new Object[]{element.text(), MATCHNUMBERTAG});
                return -1;
            }
            Pattern pattern = Pattern.compile(NUMBERREGEX);
            try {
                return Integer.parseInt(value.replaceAll(NUMBERREGEX, ""));
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "html node {0} with tag {1} has no integer in its value", new Object[]{element.text(), MATCHNUMBERTAG});
                return -1;
            }
        } else {
            logger.log(Level.WARNING, "html node {0} has no tag named {1}", new Object[]{element.text(), MATCHNUMBERTAG});
            return -1;
        }
    }

    /**
     * Checks if a string is null, empty, or contains only whitespace.
     * * @param str The string to evaluate.
     *
     * @return {@code true} if the string is null or blank; {@code false} otherwise.
     */
    private Boolean isNullOrEmpty(String str) {
        return str == null || str.isBlank();
    }

    private Document getMatchDetails(URI matchDetails) {
        Document doc;
        try {
            CloudflareHTMLExtractor extractor = new CloudflareHTMLExtractor();
            String html = extractor.getHTML(matchDetails.toString());
            doc = Jsoup.parse(html);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unable to get html for url {0}", matchDetails);
            return null;
        }


        return doc;
    }
}
