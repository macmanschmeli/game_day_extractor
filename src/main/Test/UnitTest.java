import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class UnitTest {

    @org.junit.jupiter.api.Test
    public void testAPIEmulator() throws IOException {
        APIEmulator emulator = new APIEmulator("https://panel.volleystation.com/website/150/de/phase-4089-no1llh/schedule/");
        Path filepath = Paths.get("C:\\Users\\schme\\Downloads\\table.html");
        String html = Files.readString(filepath);
        ArrayList<Game> games = emulator.getGames("Perchtoldsdorf",html);
        System.out.println("test done");
    }
    @org.junit.jupiter.api.Test
    public void testCFHTMLExtractor(){
        CloudflareHTMLExtractor extractor = new CloudflareHTMLExtractor();
        String html = extractor.getHTML("https://panel.volleystation.com/website/150/de/phase-4089-no1llh/schedule/");
        System.out.println("test done");
        System.out.println(html);


    }
}