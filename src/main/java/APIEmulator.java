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
    private String endpoint;
    private static final Logger logger= Logger.getLogger(APIEmulator.class.getName());
    private static final String GAMETAG="table-row";

    APIEmulator(String endpoint) {
        this.endpoint = endpoint;
    }

    public ArrayList<Game> getGames(String teamName){
        Document doc;
        CloudflareHTMLExtractor extractor = new CloudflareHTMLExtractor();
        doc = Jsoup.parse(extractor.getHTML(endpoint));

        Elements elements = doc.getElementsByClass(GAMETAG);
        if (elements.isEmpty()) {
            logger.log(Level.WARNING, "no games with tag {0} were found",GAMETAG );
            return null;
        }
        ArrayList<Game> list = new ArrayList<>();
        for (Element element : elements) {
            GameWizard wizard = new GameWizard(element, teamName);
            Game game = wizard.extract();
            if (game != null) list.add(game);
        }

        return list;
    }

}
