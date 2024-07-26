// @@@SNIPSTART audiobook-project-java-Workflow-interface
package ttspackage;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface TTSWorkflow {
    @WorkflowMethod
    public String startWorkflow(String filePathString);

    @QueryMethod
    public String fetchMessage();
}
// @@@SNIPEND
