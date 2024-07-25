// @@@SNIPSTART audiobook-project-java-FileActivities-implementation
package ttspackage;

import io.temporal.failure.ApplicationFailure;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class FileActivitiesImpl implements FileActivities {
    private static final Logger logger = Logger.getLogger(FileActivitiesImpl.class.getName());

    public ConversionStatus setupStatus(String pathString) {
        ConversionStatus status = new ConversionStatus(pathString);

        status.inputPath = FileUtility.validateInputFile(status.inputPathString).orElseThrow(() ->
            ApplicationFailure.newFailure(
                "Invalid path or missing/unreadable contents",
                "MALFORMED_INPUT"
            )
        );
        logger.info("Validated input file path: " + pathString);

        String content = FileUtility.fetchFileContent(status.inputPath).orElseThrow(() ->
             ApplicationFailure.newFailure(
                "Missing/unreadable contents",
                "MALFORMED_INPUT"
            )
        );

        status.chunks = DataUtility.splitText(content);
        status.chunkCount = status.chunks.size();
        status.count = 0;
        status.message = "Starting processing";
        logger.info("File content has " + status.chunkCount + " chunk(s) to process.");

        status.tempOutputPath = FileUtility.createTemporaryFile().orElseThrow(() ->
             ApplicationFailure.newFailure(
                "Unable to create temporary work file to process data",
                "FILE_ERROR"
            )
        );
        logger.info("Created temporary file for processing: " + status.tempOutputPath.toString());

        return status;
    }

    public ConversionStatus moveAudio(ConversionStatus status) {
        status.outputPath = FileUtility.findUniqueName(status.inputPath, ".mp3").orElseThrow(() ->
            ApplicationFailure.newFailure(
                "Unable to create output destination",
                "FILE_ERROR"
            )
        );

        try {
            logger.info("Moving audio into place: " + status.outputPath.toString());
            FileUtility.moveFile(status.tempOutputPath, status.outputPath);
        } catch (Exception e) {
            logger.severe("Could not move output audio into place.");
            throw ApplicationFailure.newFailure("Could not move output audio into place.", "FILE_SYSTEM_ERROR");
        }

        return status;
    }
}
// @@@SNIPEND
