package helloworldapp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;

public class MyVerticle3 extends AbstractVerticle {
    // Called when verticle is deployed
    public void start() {
        System.out.println("Verticle-3 starting!!");

        eventConsume();
        System.out.println("Verticle-3 end!!");
    }

    public void eventConsume() {
        EventBus eventBus = vertx.eventBus();

        MessageConsumer<String> consumer = eventBus.consumer("news.uk.sport");
        consumer.handler(message -> {
            System.out.println("Message received v-3: " + message.body());
            message.reply("how interesting v-3!");
        });

    }

    // Optional - called when verticle is undeployed
    public void stop() {
    }
}
