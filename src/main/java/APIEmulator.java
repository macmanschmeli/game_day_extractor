import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class APIEmulator {
    private static final Logger logger= Logger.getLogger(APIEmulator.class.getName());
    private static final String GAMEGROUPTAG="table list matches";
    private static final String GAMETAG="table-row";

    public ArrayList<Game> getGames(String teamName, String html, String prefix){
        Document doc;
        doc = Jsoup.parse(html);

        Elements elements = doc.getElementsByClass(GAMEGROUPTAG);
        if (elements.isEmpty()) {
            logger.log(Level.WARNING, "no games with tag {0} were found",GAMETAG );
            return null;
        }
        ArrayList<Game> list = new ArrayList<>();
        for (Element element : elements) {
            Element h3 = element.selectFirst("h3");
            if (h3 == null) {
                logger.log(Level.WARNING, "No <h3> date header found for match ");
                continue;
            }
            String rawDate = h3.text().trim(); // e.g. "24 Januar 2026, Samstag"
            Elements gameElements = element.getElementsByClass(GAMETAG);

            for (Element gameElement : gameElements){

                GameWizard wizard = new GameWizard(gameElement, teamName, rawDate, prefix);
                Game game = wizard.extract();
                if (game != null) list.add(game);
            }
        }

        return list;
    }

}
