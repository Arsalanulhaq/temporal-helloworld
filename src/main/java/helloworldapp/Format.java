package helloworldapp;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface Format {

    @ActivityMethod
    String composeGreeting(String name);

    @ActivityMethod
    String getHttpRequest();

    @ActivityMethod
    String createHttpRequest();
}