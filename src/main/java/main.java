import java.io.IOException;
import java.util.List;

public static void main(String[] args) throws IOException {
    String file = "C:\\Users\\schme\\Desktop\\game date extracter\\game_day_extractor\\calendar-19\\appointments.json";
    CloudflareHTMLExtractor extractor = new CloudflareHTMLExtractor();
    String html = extractor.getHTML("https://panel.volleystation.com/website/150/de/results/");
    APIEmulator emulator = new APIEmulator();
    List<Game> games = emulator.getGames("Perchtoldsdorf", html);
    CalendarJsonExporter exporter = new CalendarJsonExporter();
    exporter.export(games, file);


}