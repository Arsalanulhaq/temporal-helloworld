package helloworldapp;

import io.temporal.client.ActivityCompletionClient;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.vertx.core.Vertx;

public class HelloWorldWorker {

    public static void main(String[] args) {
        // This gRPC stubs wrapper talks to the local docker instance of the Temporal service.
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);
        // Create a Worker factory that can be used to create Workers that poll specific Task Queues.
        WorkerFactory factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker(Shared.HELLO_WORLD_TASK_QUEUE);
        // This Worker hosts both Workflow and Activity implementations.
        // Workflows are stateful, so you need to supply a type to create instances.
        worker.registerWorkflowImplementationTypes(HelloWorldWorkflowImpl.class);



        /**
         * Register our Activity Types with the Worker. Since Activities are stateless and thread-safe,
         * the Activity Type is a shared instance.
         */
        ActivityCompletionClient completionClient = client.newActivityCompletionClient();
        // Activities are stateless and thread safe, so a shared instance is used.
        worker.registerActivitiesImplementations(new FormatImpl(completionClient));
        // Start polling the Task Queue.
        factory.start();

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MyVerticle());
        vertx.deployVerticle(new MyVerticle2());
        vertx.deployVerticle(new MyVerticle3());
    }
}