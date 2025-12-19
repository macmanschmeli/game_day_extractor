import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import Loggers.LoggerPrintWriter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import org.w3c.dom.Document;

public class APIEmulator {
    private String endpoint;
    private static final Logger logger= Logger.getLogger(APIEmulator.class.getName());

    APIEmulator(String endpoint) {
        this.endpoint = endpoint;
    }

    public ArrayList<Game> getGames(){
        InputStream inputStream;
        try {
            inputStream = new URL(endpoint).openStream();
        }catch (MalformedURLException e){
            logger.log(Level.SEVERE, "Given endpoint url {0} is has an invalid format", endpoint);
            return null;
        }catch (IOException e){
            logger.log(Level.SEVERE, "Unable to open stream for url {0}", endpoint);
            return null;
        }

        Document document = createTidy().parseDOM(inputStream, new ByteArrayOutputStream());
        NodeList gameElements = document.getElementsByTagName("table list matches");

        return null;
    }

    private static Tidy createTidy(){
        Tidy tidy = new Tidy();
        PrintWriter writer = new PrintWriter(new LoggerPrintWriter(logger));
        tidy.setErrout(writer);
        return tidy;
    }

}
