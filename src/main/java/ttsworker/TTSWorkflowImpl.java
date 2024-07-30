// @@@SNIPSTART audiobook-project-java-Workflow-implementation
package ttspackage;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.workflow.*;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.logging.Logger;

public class TTSWorkflowImpl implements TTSWorkflow {
    public TTSWorkflowImpl() { }

    private final Logger logger = Logger.getLogger(TTSWorkflowImpl.class.getName());
    public String message = "Conversion request received";

    private ActivityOptions activityOptions = ActivityOptions.newBuilder().setScheduleToCloseTimeout(Duration.ofSeconds(120)).build();
    private TTSActivities encodingStub = Workflow.newActivityStub(TTSActivities.class, activityOptions);

    public String fetchMessage() {
        return message;
    }

    public String startWorkflow(String fileInputPath) {
        List<String> chunks = encodingStub.readFile(fileInputPath);
        int chunkCount = chunks.size();
        logger.info("File content has " + chunkCount + " chunk(s) to process.");

        Path tempOutputPath = encodingStub.createTemporaryFile();
        logger.info("Created temporary file for processing: " + tempOutputPath.toString());

        for (int index = 0; index < chunkCount; index += 1) {
            message = "Processing part " + (index + 1) + " of " + chunkCount;
            logger.info(message);
            encodingStub.process(chunks.get(index), tempOutputPath);
        }
        String outputPath = encodingStub.moveOutputFileToPlace(tempOutputPath);
        message = "Processing of file is done " + outputPath;
        logger.info("Output file: " + outputPath);
        return outputPath;
    }
}
// @@@SNIPEND
