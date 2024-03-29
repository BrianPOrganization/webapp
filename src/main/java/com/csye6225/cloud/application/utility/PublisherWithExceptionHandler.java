package com.csye6225.cloud.application.utility;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class PublisherWithExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(Publisher.class);
    private static String PROJECT_ID;
    private static String TOPIC_ID;
    @Value("${gcp.project_id}")
    public void setProjectId(String projectId) {
        PROJECT_ID = projectId;
    }

    @Value("${gcp.topic_name}")
    public void setTopicId(String topicId) {
        TOPIC_ID = topicId;
    }

    public static void publishWithErrorHandlerExample(String message)
            throws IOException, InterruptedException {
        TopicName topicName = TopicName.of(PROJECT_ID, TOPIC_ID);
        Publisher publisher = null;

        try {
            // Create a publisher instance with default settings bound to the topic
            publisher = Publisher.newBuilder(topicName).build();


            ByteString data = ByteString.copyFromUtf8(message);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

            // Once published, returns a server-assigned message id (unique within the topic)
            ApiFuture<String> future = publisher.publish(pubsubMessage);

            // Add an asynchronous callback to handle success / failure
            ApiFutures.addCallback(
                    future,
                    new ApiFutureCallback<String>() {

                        @Override
                        public void onFailure(Throwable throwable) {
                            if (throwable instanceof ApiException) {
                                ApiException apiException = ((ApiException) throwable);
                                // details on the API exception
                                logger.error("ApiException while publishing message with stataus code " + apiException.getStatusCode().getCode());
                                logger.error("ApiException is retryable " + String.valueOf(apiException.isRetryable()));
                            }
                            logger.error("Error publishing message : " + message);
                        }

                        @Override
                        public void onSuccess(String messageId) {
                            // Once published, returns server-assigned message ids (unique within the topic)
                            logger.info("Message published. Message ID: " + messageId);
                        }
                    },
                    MoreExecutors.directExecutor());

        } finally {
            if (publisher != null) {
                // When finished with the publisher, shutdown to free up resources.
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
        }
    }



}
