// @@@SNIPSTART audiobook-project-java-Worker-app
package ttspackage;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import java.util.logging.Logger;

public class TTSWorkerApp {
    public static String sharedTaskQueue = "tts-task-queue";
    private static final Logger logger = Logger.getLogger(TTSWorkerApp.class.getName());

    public static void runWorker(String[] args) {
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);
        WorkerFactory factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker(sharedTaskQueue);
        worker.registerWorkflowImplementationTypes(TTSWorkflowImpl.class);
        worker.registerActivitiesImplementations(new TTSActivitiesImpl());
        factory.start();
    }

    public static void main(String[] args) {
        String bearerToken = System.getenv("OPEN_AI_BEARER_TOKEN");
        if (bearerToken == null || bearerToken.isEmpty()) {
            logger.severe("Environment variable OPEN_AI_BEARER_TOKEN not found");
            System.exit(1);
        }
        bearerToken = bearerToken.trim();
        bearerToken = bearerToken.replaceAll("[\\P{Print}]", "");
        TTSActivitiesImpl.bearerToken = bearerToken;

        runWorker(args);
    }
}
// @@@SNIPEND
