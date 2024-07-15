package ttspackage;

import io.temporal.failure.ApplicationFailure;
import java.io.IOException;
import java.nio.file.Path;

public class TTSActivitiesImpl implements TTSActivities {
    public void process(String chunk, Path outputPath) {
        byte[] audio;

        // Convert the audio. This is a retryable process
        try {
            audio = TTSUtility.textToSpeech(chunk);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Store the audio. This is not a retryable process.
        try {
            DataUtility.appendToFile(audio, outputPath);
        } catch (IOException e) {
            throw ApplicationFailure.newNonRetryableFailure(
                "Unable to write to output destination",
                "FILE_ERROR");
        }
    }
}
