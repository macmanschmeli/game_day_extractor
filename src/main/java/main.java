import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public static void main(String[] args) throws IOException {
    String file = "C:\\Users\\schme\\Desktop\\game date extracter\\game_day_extractor\\calendar-19\\appointments.json";
    String[][] endpoints = {
            {"https://panel.volleystation.com/website/150/de/schedule/", "H1", "Perchtoldsdorf"},
            {"https://panel.volleystation.com/website/151/de/schedule/", "H2", "Perchtoldsdorf"},
            {"https://panel.volleystation.com/website/296/de/schedule/", "U20M", "Perchtoldsdorf"},
            {"https://panel.volleystation.com/website/297/de/schedule/", "U18M", "Perchtoldsdorf"},
            {"https://panel.volleystation.com/website/162/de/schedule/", "D1", "Südstadt"},
            {"https://panel.volleystation.com/website/160/de/schedule/", "D2", "Südstadt"},
            {"https://panel.volleystation.com/website/1156/de/schedule/", "D3", "Südstadt"},
            {"https://panel.volleystation.com/website/304/de/schedule/", "U20W", "Südstadt"},
            {"https://panel.volleystation.com/website/303/de/schedule/", "U18W1", "\\bSüdstadt\\b(?!\\s*\\d)"},
            {"https://panel.volleystation.com/website/303/de/schedule/", "U18W2", "Südstadt 1"},
            {"https://panel.volleystation.com/website/303/de/schedule/", "U18W3", "Südstadt 2"},
            {"https://panel.volleystation.com/website/301/de/schedule/", "U16W", "Südstadt"},

            };
    List<Game> allGames = new ArrayList<>();
    CloudflareHTMLExtractor extractor = new CloudflareHTMLExtractor();
    List<String[]> htmls = extractor.getHTMLs(endpoints);
    for(String[] html : htmls){
        APIEmulator emulator = new APIEmulator();
        List<Game> games = emulator.getGames(html[2], html[0], html[1] );
        allGames.addAll(games);
    }
    CalendarJsonExporter exporter = new CalendarJsonExporter();
    exporter.export(allGames, file);


}