// @@@SNIPSTART audiobook-project-java-FileActivities-interface
package ttspackage;

import io.temporal.activity.ActivityInterface;
import java.nio.file.Path;

@ActivityInterface
public interface FileActivities {
    public ConversionStatus setupStatus(String pathString);
    public ConversionStatus moveAudio(ConversionStatus status);
}
// @@@SNIPEND
