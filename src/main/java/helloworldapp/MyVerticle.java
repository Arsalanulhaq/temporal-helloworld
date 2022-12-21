package helloworldapp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.kafka.client.common.TopicPartition;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.consumer.KafkaConsumerRecord;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class MyVerticle extends AbstractVerticle {

    private HttpServer server;

    // Called when verticle is deployed
    public void start(Promise<Void> startPromise) {
        System.out.println("Verticle starting!!");

        kafkaConsumer();
//        kafkaConsumer();

        System.out.println("Verticle end!!");
    }

    public void eventPublish(String key, String value) {
        EventBus eventBus = vertx.eventBus();

        DeliveryOptions options = new DeliveryOptions();
        options.addHeader("some-header", "some-value");
//        eventBus.send("news.uk.sport", "Yay! Someone kicked a ball", options);
        eventBus.request("news.uk.sport", value, ar -> {
            if (ar.succeeded()) {
                System.out.println("Received reply v-1: " + ar.result().body());
            }
        });
    }

    public void kafkaConsumer() {
        Map<String, String> config = new HashMap<>();
//        config.put("metadata.broker.list", "locahost:9092");
        config.put("bootstrap.servers", "localhost:9092");
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("group.id", "my_group");
        config.put("auto.offset.reset", "earliest");
        config.put("enable.auto.commit", "false");

// use consumer for interacting with Apache Kafka
        KafkaConsumer<String, String> consumer = KafkaConsumer.create(vertx, config);
// subscribe to several topics with list
        Set<String> topics = new HashSet<>();
        topics.add("quickstart");
//        topics.add("topic2");
//        consumer.subscribe(topics);
        consumer
                .subscribe(topics)
                .onSuccess(v ->
                        System.out.println("subscribed")
                ).onFailure(cause ->
                        System.out.println("Could not subscribe " + cause.getMessage())
                );
        consumer.handler(record -> {
            eventPublish(record.key(), record.value());
//            System.out.println("Processing key=" + record.key() + ",value=" + record.value() +
//                    ",partition=" + record.partition() + ",offset=" + record.offset());
        });
    }

    // Optional - called when verticle is undeployed
    public void stop() {
    }

}
