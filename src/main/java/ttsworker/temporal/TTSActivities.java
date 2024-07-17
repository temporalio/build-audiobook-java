// @@@SNIPSTART audiobook-project-java-tts-interface
package ttspackage;

import io.temporal.activity.ActivityInterface;
import java.nio.file.Path;

@ActivityInterface
public interface TTSActivities {
    public void process(String chunk, Path outputPath);
}
// @@@SNIPEND
