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
        try {
            doc =  Jsoup.connect(endpoint)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Connection", "keep-alive")
                    .header("Upgrade-Insecure-Requests", "1")
                    .followRedirects(true)
                    .get();
        }catch (MalformedURLException e){
            logger.log(Level.SEVERE, "Given endpoint url {0} is has an invalid format", endpoint);
            return null;
        }catch (IOException e){
            logger.log(Level.SEVERE, "Unable to get html for url {0}", endpoint);
            return null;
        }


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
