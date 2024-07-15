package ttspackage;

import io.temporal.failure.ApplicationFailure;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class FileActivitiesImpl implements FileActivities {
    private static final Logger logger = Logger.getLogger(FileActivitiesImpl.class.getName());

    // Set up the status for a text-to-speech conversion.
    // This method will never be retried as every error is non-retryable.
    public ConversionStatus setupStatus(String pathString) {
        ConversionStatus status = new ConversionStatus(pathString);

        // Validate the input file path and ensure it points to a readable text file.
        status.inputPath = FileUtility.validateInputFile(status.inputPathString).orElseThrow(() ->
            ApplicationFailure.newNonRetryableFailure(
                "Invalid path or missing/unreadable contents",
                "MALFORMED_INPUT"
            )
        );
        logger.info("Validated input file path: " + pathString);

        // Create a temporary file for intermediate processing.
        status.tempOutputPath = FileUtility.createTemporaryFile().orElseThrow(() ->
             ApplicationFailure.newNonRetryableFailure(
                "Unable to create temporary work file to process data",
                "FILE_ERROR"
            )
        );
        logger.info("Created temporary file for processing: " + status.tempOutputPath.toString());

        // Read the content of the validated input file.
        String content = FileUtility.fetchFileContent(status.inputPath).orElseThrow(() ->
             ApplicationFailure.newNonRetryableFailure(
                "Missing/unreadable contents",
                "MALFORMED_INPUT"
            )
        );

        // Split the content into manageable chunks for processing.
        // This won't fail.
        status.chunks = DataUtility.splitText(content);
        status.chunkCount = status.chunks.size();
        status.count = 0;
        status.message = "Starting processing";
        logger.info("File content has " + status.chunkCount + " chunk(s) to process.");

        return status;
    }

    // Moves the audio file from the temporary output path to the final output path.
    // This method should never be retried as every error is non-retryable.
    public ConversionStatus moveAudio(ConversionStatus status) {
        status.outputPath = FileUtility.findUniqueName(status.inputPath, ".mp3").orElseThrow(() ->
            ApplicationFailure.newNonRetryableFailure(
                "Unable to create output destination",
                "FILE_ERROR"
            )
        );

        try {
            logger.info("Moving audio into place: " + status.outputPath.toString());
            FileUtility.moveFile(status.tempOutputPath, status.outputPath);
        } catch (Exception e) {
            logger.severe("Could not move output audio into place.");
            throw ApplicationFailure.newNonRetryableFailure("Could not move output audio into place.", "FILE_SYSTEM_ERROR");
        }

        return status;
    }
}
