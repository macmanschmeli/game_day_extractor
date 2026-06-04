import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GitPublisher {

    private static final Logger logger = Logger.getLogger(GitPublisher.class.getName());

    private final File workingDirectory;

    public GitPublisher(String workingDirectory) {
        this.workingDirectory = new File(workingDirectory);
    }

    public GitPublisher() {
        this.workingDirectory = new File(System.getProperty("user.dir"));
    }

    public void publish(String filePath, String commitMessage) throws IOException, InterruptedException {
        run("git", "add", filePath);
        run("git", "commit", "-m", commitMessage);
        run("git", "push");
    }

    public void publish(String filePath) throws IOException, InterruptedException {
        publish(filePath, "adapted dates");
    }

    private void run(String... command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(workingDirectory);
        pb.redirectErrorStream(true); // merge stderr into stdout

        Process process = pb.start();

        // Stream output to logger
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logger.log(Level.INFO, "[git] {0}", line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException(
                    "Git command failed with exit code " + exitCode + ": " + String.join(" ", command)
            );
        }
    }
}