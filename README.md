# SQS Copier

### Description

Is a simple project that shows how to copy messages from one AWS SQS Queue to another. Also it might be used as stand alone tool or library.

**Feel free to reuse this code in your projects**

### Example

```java
    String region = "eu-central-1";
    String fromQueueName = "queue1";
    String toQueueName = "queue2";

    new SQSCopier(region)
        .startCopying(fromQueueName, toQueueName, false);
```

### Build

- Build project as Stand Alone JAR file use Gradle command : `gradle clean fatJar`. After this just go into `build/libs` folder and execute in terminal:
`java -jar SQSCopier-1.0.jar eu-central-1 queue1 queue2`

- Build project as JAR library use gradle command : `gradle build`. After this you may find JAR file in folder `build/libs` that called `org.aws.sqs.copier-1.0.jar`.

### Notice

AWS SQS Client expect to find your **AWS_ACCESS_KEY** and **AWS_SECRET_ACCESS_KEY** inside environment or system variables.

## Enjoy
