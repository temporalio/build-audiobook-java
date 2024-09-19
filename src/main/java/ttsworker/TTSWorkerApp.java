// @@@SNIPSTART audiobook-project-java-Worker-app
package ttspackage;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import java.lang.management.ManagementFactory;
import java.util.logging.Logger;

public class TTSWorkerApp {
    public static String sharedTaskQueue = "tts-task-queue";
    public static String sharedActivityTaskQueue;
    private static final Logger logger = Logger.getLogger(TTSWorkerApp.class.getName());

    public static void main(String[] args) {
        String bearerToken = System.getenv("OPEN_AI_BEARER_TOKEN");
        if (bearerToken == null || bearerToken.isEmpty()) {
            logger.severe("Environment variable OPEN_AI_BEARER_TOKEN not found");
            System.exit(1);
        }
        bearerToken = bearerToken.trim();
        bearerToken = bearerToken.replaceAll("[\\P{Print}]", "");

        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);
        WorkerFactory factory = WorkerFactory.newInstance(client);

        // This starter Worker will start a Workflow and create a unique Task Queue name
        Worker starterWorker = factory.newWorker(sharedTaskQueue);
        starterWorker.registerWorkflowImplementationTypes(TTSWorkflowImpl.class);
        sharedActivityTaskQueue = ManagementFactory.getRuntimeMXBean().getName();

        // This host-specific Worker restricts all activities to the unique
        // host-specific Task Queue
        Worker hostSpecificWorker = factory.newWorker(sharedActivityTaskQueue);
        hostSpecificWorker.registerActivitiesImplementations(new TTSActivitiesImpl(bearerToken));
        factory.start();
    }
}
// @@@SNIPEND
