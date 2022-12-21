package helloworldapp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;

public class MyVerticle2 extends AbstractVerticle {

    // Called when verticle is deployed
    public void start() {
        System.out.println("Verticle-2 starting!!");

        eventConsume();
        System.out.println("Verticle-2 end!!");
    }

    public void eventConsume() {
        EventBus eventBus = vertx.eventBus();

        MessageConsumer<String> consumer = eventBus.consumer("news.uk.sport");
        consumer.handler(message -> {
            System.out.println("Message received v-2: " + message.body());
            message.reply("how interesting v-2!");
        });

    }

    // Optional - called when verticle is undeployed
    public void stop() {
    }
}
