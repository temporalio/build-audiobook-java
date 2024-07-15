package ttspackage;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface TTSWorkflow {
    @WorkflowMethod
    String startWorkflow(InputPayload payload);

    @QueryMethod
    String fetchMessage();
}
