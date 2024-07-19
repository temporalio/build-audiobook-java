// @@@SNIPSTART audiobook-project-java-tts-implementation
package ttspackage;

import io.temporal.failure.ApplicationFailure;
import java.io.IOException;
import java.nio.file.Path;

public class TTSActivitiesImpl implements TTSActivities {
    public void process(String chunk, Path outputPath) {
        byte[] audio;

        try {
            audio = TTSUtility.textToSpeech(chunk);
            DataUtility.appendToFile(audio, outputPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
// @@@SNIPEND
