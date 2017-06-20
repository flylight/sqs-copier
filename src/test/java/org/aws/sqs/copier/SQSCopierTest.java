package org.aws.sqs.copier;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Test cases for {@link SQSCopier} class.
 *
 * Created by arymar on 20.06.17.
 */
public class SQSCopierTest {

  @Test
  public void testCopyingOfMessagesWithoutDeleting() {
    //GIVEN
    String region = "testRegion";
    String fromQueueName = "test1";
    String toQueueName = "test2";

    String fromQueueURL = "fromQueueURL";
    String toQueueURL = "toQueueURL";

    AmazonSQS amazonSQS = mock(AmazonSQS.class);

    SQSCopier sqsCopier = new SQSCopier(region){
      @Override
      protected AmazonSQS buildClient(String region) {
        return amazonSQS;
      }
    };

    ReceiveMessageResult receiveMessageResult = mock(ReceiveMessageResult.class);

    Message message = mock(Message.class);
    Message message2 = mock(Message.class);

    String messageBody1 = "Test body 1";
    String messageBody2 = "Test body 2";

    List<Message> messages = Arrays.asList(message, message2);

    GetQueueUrlResult fromQueueUrlResult = mock(GetQueueUrlResult.class);
    GetQueueUrlResult toQueueUrlResult = mock(GetQueueUrlResult.class);

    //WHEN
    when(amazonSQS.getQueueUrl(eq(fromQueueName))).thenReturn(fromQueueUrlResult);
    when(amazonSQS.getQueueUrl(eq(toQueueName))).thenReturn(toQueueUrlResult);

    when(fromQueueUrlResult.getQueueUrl()).thenReturn(fromQueueURL);
    when(toQueueUrlResult.getQueueUrl()).thenReturn(toQueueURL);

    when(amazonSQS.receiveMessage(Mockito.<ReceiveMessageRequest>any())).thenReturn(receiveMessageResult);
    when(receiveMessageResult.getMessages()).thenReturn(messages, Collections.emptyList());

    when(message.getBody()).thenReturn(messageBody1);
    when(message2.getBody()).thenReturn(messageBody2);

    //THEN
    sqsCopier.startCopying(fromQueueName, toQueueName, false);

    verify(amazonSQS, times(1)).sendMessage(eq(toQueueURL), eq(messageBody1));
    verify(amazonSQS, times(1)).sendMessage(eq(toQueueURL), eq(messageBody2));
  }

}
