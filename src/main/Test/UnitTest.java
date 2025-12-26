import java.util.ArrayList;

public class UnitTest {

    @org.junit.jupiter.api.Test
    public void testAPIEmulator() {
        APIEmulator emulator = new APIEmulator("https://panel.volleystation.com/website/150/de/phase-4089-no1llh/schedule/");
        ArrayList<Game> games = emulator.getGames("Perchtholdsdorf");
        System.out.println("test done");
    }
}