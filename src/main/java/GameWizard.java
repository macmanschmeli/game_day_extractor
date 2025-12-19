import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class GameWizard {
    private final Node node;
    private static final Logger logger = Logger.getLogger(GameWizard.class.getName());
    private final String filter;

    private static final String NUMBERREGEX = "[\\d]";
    private static final String MATCHNUMBERTAG = "match-number";
    private static final String NAMETAG = "name";
    private static final String TIMETAG = "status upcoming";

    GameWizard(Node node, String filter) {
        this.node = node;
        this.filter=filter;
    }

    public Game format() {
        Game game = new Game();
        NodeList elements = findElementsByName(node, MATCHNUMBERTAG);
        game.matchNumber= extractMatchNumber(elements);
        elements = findElementsByName(node, NAMETAG);
        String opponentName = extractOpponentName(elements);
        if (isNullOrEmpty(opponentName))
            return null;
        game.opponent=opponentName;

        return game;
    }


    /**
     * Extracts the opponent's name from a pair of nodes.
     * <p>
     * This method expects a {@link NodeList} with at least two nodes. It compares
     * the text values of both nodes against a member variable {@code filter}.
     * If one node contains the filter string (case-insensitive), the name of
     * the *other* node is returned as the opponent.
     * </p>
     * * @param elements The {@link NodeList} containing the club/team nodes.
     * @return The name of the opponent team if one matches the filter;
     * {@code null} if nodes are missing, empty, or neither match the filter.
     */
    private String extractOpponentName(NodeList elements){
        if (elements != null && elements.getLength() > 1) {
            Node clubOne= elements.item(0);
            Node clubTwo= elements.item(1);
            String clubOneName = clubOne.getNodeValue();
            String clubTwoName = clubTwo.getNodeValue();
            if (isNullOrEmpty(clubOneName) || isNullOrEmpty(clubTwoName)) {
                logger.log(Level.WARNING, "html node {0} with tag {1} has no value", new Object[]{node.getNodeValue(), NAMETAG});
                return null;
            }
            if (clubOneName.toLowerCase().contains(filter.toLowerCase())){
                return clubTwoName;
            }
            if (clubTwoName.toLowerCase().contains(filter.toLowerCase())){
                return clubOneName;
            }
            logger.log(Level.INFO, "html node {0} does not contain a game regarding {1}", new Object[]{node.getNodeValue(), filter});
            return null;


        } else {
            logger.log(Level.WARNING, "html node {0} has no tag named {1}", new Object[]{node.getNodeValue(), NAMETAG});
            return null;
        }

    }

    /**
     * Extracts and parses a numeric match identifier from a {@link NodeList}.
     * <p>
     * This method retrieves the first node, strips characters matching
     * {@code NUMBERREGEX}, and attempts to parse the remaining string into an integer.
     * </p>
     * * @param elements The {@link NodeList} containing the match number data.
     * @return The parsed match number as an {@code int};
     * {@code -1} if the list is empty, the value is null, or parsing fails.
     */
    private int extractMatchNumber(NodeList elements){
        if (elements != null && elements.getLength() > 0) {
            Node matchnr = elements.item(0);
            String value = matchnr.getNodeValue();
            if (value==null) {
                logger.log(Level.WARNING, "html node {0} with tag {1} has no value", new Object[]{node.getNodeValue(), MATCHNUMBERTAG});
                return -1;
            }
            Pattern pattern = Pattern.compile(NUMBERREGEX);
            try {
                return Integer.parseInt(value.replaceAll(NUMBERREGEX, ""));
            }catch (NumberFormatException e){
                logger.log(Level.WARNING, "html node {0} with tag {1} has no integer in its value", new Object[]{node.getNodeValue(), MATCHNUMBERTAG});
                return -1;
            }
        } else {
            logger.log(Level.WARNING, "html node {0} has no tag named {1}", new Object[]{node.getNodeValue(), MATCHNUMBERTAG});
            return -1;
        }
    }

    /**
     * Retrieves a list of descendant elements with the specified tag name.
     * * @param node The parent {@link Node} to search (must be a {@link Document} or {@link Element}).
     * @param tagName The name of the tag to locate.
     * @return A {@link NodeList} of matching elements, or {@code null} if the
     * provided node is not a searchable type.
     */
    private static NodeList findElementsByName(Node node, String tagName) {
        if (node instanceof Document) {
            return ((Document) node).getElementsByTagName(tagName);
        } else if (node instanceof Element) {
            return ((Element) node).getElementsByTagName(tagName);
        }
        return null; // Or return an empty NodeList implementation
    }

    /**
     * Checks if a string is null, empty, or contains only whitespace.
     * * @param str The string to evaluate.
     * @return {@code true} if the string is null or blank; {@code false} otherwise.
     */
    private Boolean isNullOrEmpty(String str){
        return str == null || str.isBlank();
    }
}
