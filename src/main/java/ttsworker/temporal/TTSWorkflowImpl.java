// @@@SNIPSTART audiobook-project-java-Workflow-implementation
package ttspackage;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.workflow.Workflow;
import java.io.IOException;
import java.time.Duration;
import java.util.logging.Logger;

public class TTSWorkflowImpl implements TTSWorkflow {
    private ConversionStatus status;
    private static final Logger logger = Logger.getLogger(TTSWorkflowImpl.class.getName());

    private RetryOptions singleRetryOptions = RetryOptions
        .newBuilder()
        .setMaximumAttempts(1)
        .build();

    private ActivityOptions singleRetryActivityOptions = ActivityOptions
        .newBuilder()
        .setRetryOptions(singleRetryOptions)
        .build();

    private ActivityOptions twoMinuteActivityOptions = ActivityOptions
        .newBuilder()
        .setScheduleToCloseTimeout(Duration.ofSeconds(120))
        .build();

    private FileActivities fileStub = Workflow.newActivityStub(FileActivities.class, singleRetryActivityOptions);
    private TTSActivities encodingStub = Workflow.newActivityStub(TTSActivities.class, twoMinuteActivityOptions);

    // Fetch the current encoding status
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
// @@@SNIPEND
