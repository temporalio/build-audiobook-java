// @@@SNIPSTART audiobook-project-java-Worker-app
package ttspackage;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import java.util.logging.Logger;

public class TTSWorkerApp {
    public static String sharedTaskQueue = "TTS_TASK_QUEUE";
    private static final Logger logger = Logger.getLogger(TTSWorkerApp.class.getName());

    public static void runWorker(String[] args) {
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);
        WorkerFactory factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker(TTSWorkerApp.sharedTaskQueue);
        worker.registerWorkflowImplementationTypes(TTSWorkflowImpl.class);
        worker.registerActivitiesImplementations(new FileActivitiesImpl());
        worker.registerActivitiesImplementations(new TTSActivitiesImpl());
        factory.start();
    }

    public static void main(String[] args) {
        // Validate that there's a bearer token before moving forward
        String bearerToken = System.getenv("OPEN_AI_BEARER_TOKEN");
        if (bearerToken == null || bearerToken.isEmpty()) {
            logger.severe("Bearer Token is not set as an Environment Variable.");
            logger.severe("Set OPEN_AI_BEARER_TOKEN in the Worker's shell before running.");
            logger.severe("Worker cannot run. Exiting.");
            System.exit(1);
        }

        runWorker(args);
    }
}
// @@@SNIPEND
