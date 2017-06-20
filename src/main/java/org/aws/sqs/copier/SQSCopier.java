package org.aws.sqs.copier;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

import java.util.List;
import java.util.logging.Logger;

/**
 * Simple application that shows how to use Amazon SQS client to read, send and delete messages
 * between different queues.
 *
 * NOTICE: AWS credentials (AWS_ACCESS_KEY and AWS_SECRET_ACCESS_KEY) should be declared as Environment Variables.
 *
 * Created by arymar on 19.06.17.
 */
public class SQSCopier {
  private static final Logger logger = Logger.getLogger("SQSCopier");
  //Range 1 - 10
  private static final int MAX_MESSAGES_PER_REQUEST = 1;

  private final AmazonSQS amazonSQSClient;

  /**
   * Build SQSCopier instance constructor. Also build AmazonSQS client using Region from input
   * arguments.
   *
   * @param region AWS Region for SQS Client.
   */
  public SQSCopier(String region) {
    amazonSQSClient = buildClient(region);
  }

  /**
   * Start copying messages from one queue to another.
   *
   * @param fromQueue Source queue name.
   * @param toQueue   Destination queue name.
   * @param deleteSourceMessage remove message from source queue.
   */
  public void startCopying(String fromQueue, String toQueue, boolean deleteSourceMessage) {
    logger.info("Start copying data from - " + fromQueue + " to - " + toQueue);

    String fromQueueUrl = amazonSQSClient.getQueueUrl(fromQueue).getQueueUrl();
    String toQueueUrl = amazonSQSClient.getQueueUrl(toQueue).getQueueUrl();

    ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest()
        .withQueueUrl(fromQueueUrl)
        .withMaxNumberOfMessages(MAX_MESSAGES_PER_REQUEST);

    List<Message> messageList;

    while ((messageList = amazonSQSClient.receiveMessage(receiveMessageRequest).getMessages()).size() > 0) {

      copyMessages(messageList, fromQueueUrl, toQueueUrl, deleteSourceMessage);

    }
  }

  /**
   * Build Amazon SQS client based on Region.
   *
   * @param region AWS Region.
   * @return {@link AmazonSQS} instance.
   */
  protected AmazonSQS buildClient(String region){
    return AmazonSQSClientBuilder
        .standard()
        .withRegion(region)
        .build();
  }

  /**
   * Copy read messages to destination queue.
   *
   * @param messages read messages from source queue.
   * @param fromQueueUrl from queue URL.
   * @param toQueueUrl to queue URL.
   * @param deleteSourceMessage remove message from source queue.
   */
  private void copyMessages(List<Message> messages, String fromQueueUrl, String toQueueUrl,
                            boolean deleteSourceMessage) {
    for (Message message : messages) {
      logger.info("Copy message :" + message.getBody());

      amazonSQSClient.sendMessage(toQueueUrl, message.getBody());

      if (deleteSourceMessage) {
        amazonSQSClient.deleteMessage(fromQueueUrl, message.getReceiptHandle());
        logger.info("Message removed from source queue!");
      }

      logger.info("Message copied.");
    }
  }

  /**
   * Just for stand alone usage.
   *
   * @param args input arguments. Expected arguments :
   *             1 - Region Name
   *             2 - From Queue Name
   *             3 - To Queue Name
   */
  public static void main(String... args) {

    if (args.length != 3) {

      logger.warning("Wrong input arguments size!");
      logger.info("Expected : <region>, <from_queue_name>, <to_queue_name>");
      logger.info("Example : java -jar SQSCopier.jar eu-central-1 fromQueue toQueue");

      return;
    }

    String region = args[0];
    String fromQueueName = args[1];
    String toQueueName = args[2];

    new SQSCopier(region).startCopying(fromQueueName, toQueueName, false);
  }
}
