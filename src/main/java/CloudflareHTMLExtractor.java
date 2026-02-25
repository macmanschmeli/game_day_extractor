import java.util.Random;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;

public class CloudflareHTMLExtractor {
    private static final Playwright playwright = Playwright.create();
    private static String UserAgent ="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    private static String InitScript = "() => {" +
            "  delete Object.getPrototypeOf(navigator).webdriver;" +
            "  window.chrome = { runtime: {} };" +
            "  Object.defineProperty(navigator, 'languages', { get: () => ['de-DE', 'de', 'en-US', 'en'] });" +
            "  Object.defineProperty(navigator, 'plugins', { get: () => [1, 2, 3, 4, 5] });" +
            "}";

    private static final Random random = new Random();

    String getHTML(String endpoint) {
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)); // Keep visible for testing

        // 1. Create a context with a realistic User-Agent
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setUserAgent(UserAgent));

        // 2. Add Init Script to mask WebDriver
        context.addInitScript(InitScript);

        Page page = context.newPage();

        // 3. Navigate with human-like randomized behavior

        //humanDelay(2000, 5000); // Wait before navigating
        System.out.println("Navigating to Volleystation...");
        page.navigate(endpoint);
        //simulateHumanActivity(page);

        // Wait for the specific data table to appear
        page.waitForLoadState(LoadState.NETWORKIDLE);
        //humanDelay(3000, 6000); // Simulate "reading" time

        // Extract content
        String html = page.content();
        System.out.println("Page content retrieved successfully.");

        browser.close();
        return html;
    }

    /**
     * Simulates human pausing behavior
     *
     * @param min Minimum milliseconds
     * @param max Maximum milliseconds
     */
    private static void humanDelay(int min, int max) {
        try {
            int delay = random.nextInt((max - min) + 1) + min;
            System.out.println("Human pause: " + delay + "ms");
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void simulateHumanActivity(Page page) {
        Random random = new Random();

        // 1. Random "Wiggles"
        for (int i = 0; i < 3; i++) {
            int x = random.nextInt(800);
            int y = random.nextInt(600);
            page.mouse().move(x, y, new Mouse.MoveOptions().setSteps(15));

            // Random pause between moves
            try {
                Thread.sleep(random.nextInt(500) + 200);
            } catch (Exception ignored) {
            }
        }

        // 2. Natural Scrolling
        // Scroll down in small "notches" like a mouse wheel
        for (int i = 0; i < 5; i++) {
            int scrollAmount = random.nextInt(200) + 100;
            page.mouse().wheel(0, scrollAmount);
            try {
                Thread.sleep(random.nextInt(800) + 400);
            } catch (Exception ignored) {
            }
        }
    }
}
