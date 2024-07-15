package ttspackage;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.workflow.Workflow;
import java.time.Duration;

class TemporalUtility {
    public static <T> T buildActivityStub(Class<T> classReference, int maxAttempts, Duration duration) {
        // Uses default if maxAttempts is 0
        RetryOptions retryOptions = RetryOptions
            .newBuilder()
            .setMaximumAttempts(maxAttempts)
            .build();

        // This approach uses schedule-to-close to provide an upper limit on the
        // activity execution including retries. ScheduleToStartTimeout is always
        // non-retryable.
        ActivityOptions activityOptions = ActivityOptions
            .newBuilder()
            .setRetryOptions(retryOptions)
            .setScheduleToCloseTimeout(duration)
            .build();

        return Workflow.newActivityStub(classReference, activityOptions);
    }
}
