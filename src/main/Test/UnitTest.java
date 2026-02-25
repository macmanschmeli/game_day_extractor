import java.util.ArrayList;

public class UnitTest {

    @org.junit.jupiter.api.Test
    public void testAPIEmulator() {
        APIEmulator emulator = new APIEmulator("https://panel.volleystation.com/website/150/de/phase-4089-no1llh/schedule/");
        ArrayList<Game> games = emulator.getGames("Perchtholdsdorf");
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