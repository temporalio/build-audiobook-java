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
        .setMaximumAttempts(3)
        .build();

    private ActivityOptions singleRetryActivityOptions = ActivityOptions
        .newBuilder()
        .setRetryOptions(singleRetryOptions)
        .setScheduleToCloseTimeout(Duration.ofSeconds(5))
        .build();

    private ActivityOptions twoMinuteActivityOptions = ActivityOptions
        .newBuilder()
        .setScheduleToCloseTimeout(Duration.ofSeconds(120))
        .build();

    private FileActivities fileStub = Workflow.newActivityStub(FileActivities.class, singleRetryActivityOptions);
    private TTSActivities encodingStub = Workflow.newActivityStub(TTSActivities.class, twoMinuteActivityOptions);

    public String fetchMessage() {
        return status.message;
    }

    public String startWorkflow(InputPayload payload) {
        ConversionStatus status = fileStub.setupStatus(payload.path);
        for (int index = 0; index < status.chunkCount; index += 1) {
            status.count = index;
            status.message = "Processing part " + (index + 1) + " of " + status.chunkCount; 
            logger.info(status.message);
            encodingStub.process(status.chunks.get(index), status.tempOutputPath);
        }
        status = fileStub.moveAudio(status);
        logger.info("Output file: " + status.outputPath.toString());
        return status.outputPath.toString();
    }
}
// @@@SNIPEND
