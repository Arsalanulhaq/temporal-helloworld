package helloworldapp;

import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import io.temporal.client.ActivityCompletionClient;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.client.WebClient;
import io.vertx.core.Vertx;

public class FormatImpl implements Format {

    /**
     * ActivityCompletionClient is used to asynchronously complete activities. In this example we
     * will use this client alongside with {@link
     * io.temporal.activity.ActivityExecutionContext#doNotCompleteOnReturn()} which means our
     * activity method will not complete when it returns, however is expected to be completed
     * asynchronously using the client.
     */
    private final ActivityCompletionClient completionClient;

    FormatImpl(ActivityCompletionClient completionClient) {
        this.completionClient = completionClient;
    }

    @Override
    public String composeGreeting(String name) {
        return "Hello " + name + "!";
    }

    @Override
    public String getHttpRequest() {
        ActivityExecutionContext context = Activity.getExecutionContext();
        // Used to correlate reply
        byte[] taskToken = context.getInfo().getTaskToken();

        getHttpRequestAsync(taskToken);

        context.doNotCompleteOnReturn();

        // Since we have set doNotCompleteOnReturn(), the workflow action method return value is
        // ignored.
        return "ignored";

//        return "Http GET request works!!";
    }

    // Method that will complete action execution using the defined ActivityCompletionClient
    private void getHttpRequestAsync(byte[] taskToken) {
        Vertx vertx = Vertx.vertx();
        WebClient client = WebClient.create(vertx);

        String result = "Async activity function for http GET--";

        // Complete our workflow activity using ActivityCompletionClient
//        completionClient.complete(taskToken, result);

        // Submit the form as a multipart form body
        // Send a GET request
        client
                .get(8088, "localhost", "/api/members/1")
//                .get("vertx.io", "/docs")
                .send()
                .onSuccess(res -> {
//                    body = res.body().toJson();
                    System.out.println("Received GET response with status code " + res.statusCode() + " with body " + res.body());
                    completionClient.complete(taskToken, result);
                })
                .onFailure(err ->
                        System.out.println("Something went wrong.. " + err.getMessage()));
//        completionClient.complete(taskToken, result);

    }

    @Override
    public String createHttpRequest() {

        ActivityExecutionContext context = Activity.getExecutionContext();
        // Used to correlate reply
        byte[] taskToken = context.getInfo().getTaskToken();

        createHttpRequestAsync(taskToken);

        context.doNotCompleteOnReturn();

        // Since we have set doNotCompleteOnReturn(), the workflow action method return value is
        // ignored.
        return "ignored";
//        return "Http POST request works!!";
    }

    // Method that will complete action execution using the defined ActivityCompletionClient
    private void createHttpRequestAsync(byte[] taskToken) {
        Vertx vertx = Vertx.vertx();
        WebClient client = WebClient.create(vertx);

        MultiMap form = MultiMap.caseInsensitiveMultiMap();
        form.set("membership_id", "MEM-10");
        form.set("name", "John Doe 10");

        String result = "Async activity function for http post--";

        // Submit the form as a multipart form body
        client
                .post(8088, "localhost", "/api/members")
                .putHeader("content-type", "multipart/form-data")
                .sendForm(form)
                .onSuccess(res -> {
                    // OK
                    System.out.println("Received POST response with status code " + res.statusCode() + " with body " + res.body());
                    completionClient.complete(taskToken, result);
                });
//        completionClient.complete(taskToken, result);

    }
}