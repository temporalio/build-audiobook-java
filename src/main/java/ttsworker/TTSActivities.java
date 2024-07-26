// @@@SNIPSTART audiobook-project-java-tts-interface
package ttspackage;

import io.temporal.activity.ActivityInterface;
import java.nio.file.Path;
import java.util.List;

@ActivityInterface
public interface TTSActivities {
    public List<String> readFile(String inputPath);
    public Path createTemporaryFile();
    public void process(String chunk, Path outputPath);
    public String moveOutputFileToPlace(Path tempPath);
}
// @@@SNIPEND
