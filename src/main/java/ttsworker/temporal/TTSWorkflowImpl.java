package ttspackage;

import java.io.IOException;
import java.time.Duration;
import java.util.logging.Logger;

public class TTSWorkflowImpl implements TTSWorkflow {
    private ConversionStatus status;
    private static final Logger logger = Logger.getLogger(TTSWorkflowImpl.class.getName());

    // Allow up to two minutes for each chunk to process.
    private TTSActivities encodingStub = TemporalUtility.buildActivityStub(TTSActivities.class, 0, Duration.ofSeconds(120));

    // Local utility work requires very little time so it uses a shorter timeout
    private FileActivities fileStub = TemporalUtility.buildActivityStub(FileActivities.class, 0, Duration.ofSeconds(10));

    // Query method to return the current status message
    public String fetchMessage() {
        return status.message;
    }

    // Workflow entry point
    public String startWorkflow(InputPayload payload) {
        // Create the conversion elements
        ConversionStatus status = fileStub.setupStatus(payload.path);

        // Process them
        for (int index = 0; index < status.chunkCount; index += 1) {
            status.count = index;
            status.message = "Processing part " + (index + 1) + " of " + status.chunkCount; // for Queries
            logger.info(status.message);
            encodingStub.process(status.chunks.get(index), status.tempOutputPath);
        }

        // Move the results into place from the temporary folder
        status = fileStub.moveAudio(status);
        logger.info("Output file: " + status.outputPath.toString());
        return status.outputPath.toString();
    }
}
