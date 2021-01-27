import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMq样例
 * 
 * <dependency>
 *     <groupId>com.rabbitmq</groupId>
 *     <artifactId>amqp-client</artifactId>
 *     <version>4.1.0</version>
 * </dependency>
 */
public class RabbitMqDemo {
    private volatile static Connection connection;

    public static void main(String[] args) throws Exception {
        RabbitMqDemo.startConsumer();
        //RabbitMqDemo.sendMsg("C:\\Users\\Administrator\\Desktop\\1.txt");
        //RabbitMqDemo.sendMsg();
    }

    public static void startConsumer() {
        new ConsumerThread("OLD_POLICY_37_MESSAGE_QUEUE").start();
        new ConsumerThread("POLICY_GENERATE_GZTrafficPlatform_FANOUT_MESSAGE_QUEUE").start();
        new ConsumerThread("POLICY_GENERATE_GZElectronTrafficPlatform_FANOUT_MESSAGE_QUEUE").start();
    }
    
    /**
     * 双重校验锁方式
     * @author: wangpeng
     * @date: 2020/1/14 20:36
     */
    public static Connection getSingleton() throws Exception {
        if (connection == null) {
            synchronized (RabbitMqDemo.class) {
                if (connection == null) {
                    //String ipAddress = "9.23.28.35:5672";
//                    String ipAddress = "9.23.27.142:5672";
                    String ipAddress = "boss.iok.la:5672";
//                    String ipAddress = "9.7.22.21:5672";
//                    String ipAddress = "9.0.9.74:5672";
//                    String ipAddress = "9.20.134.157:5672";//157 158 159
                    String strIp = ipAddress.split(":")[0];
                    int intPort = Integer.parseInt(ipAddress.split(":")[1]);
//                    String username = "clpcadmin";
//                    String password = "clpcpasswd";
                    String username = "test";
                    String password = "test";
//                    String password = "test#123";
                    ConnectionFactory factory = new ConnectionFactory();
                    factory.setAutomaticRecoveryEnabled(true);
                    factory.setHost(strIp);
                    factory.setPort(intPort);
                    factory.setUsername(username);
                    factory.setPassword(password);
                    connection = factory.newConnection();
                }
            }
        }
        return connection;
    }

    /**
     * MQ 消息生产发送
     */
    public static void send(String exchange, String routingKey, long messageId, byte[] bytes) throws Exception {
        Channel channel = null;
        try {
            channel = RabbitMqDemo.getSingleton().createChannel();
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
                    .messageId(String.valueOf(messageId))
                    .build();
            channel.basicPublish(exchange, routingKey, properties, bytes);
            System.out.println("RabbitMQ发送成功");
        } catch (Exception e) {
            System.out.println(exchange + "" + routingKey + "RabbitMQ:" + e);
            throw e;
        } finally {
            try {
                if (channel != null) {
                    channel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void sendMsg(String path) throws Exception {
        InputStream in = null;
        try {
            File file = new File(path);
            System.out.println("按行读取文件内容");
            in = new FileInputStream(file);
            Reader reader2 = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(reader2);
            String line;
            while((line=bufferedReader.readLine())!=null){
                System.out.println(line);
                RabbitMqDemo.sendMsg(line.split("-----")[0], line.split("-----")[1]);
                Thread.sleep(500);
            }
            bufferedReader.close();
        } catch (Exception e) {
        }
    }
    
    public static void sendMsg(String id, String content) throws Exception {
        long messageId = Long.valueOf(id);
        String json = "{\"content\":{" +
                "\"body\":\"" + content.replace("\"", "\\\"") + "\"," +
                "\"type\":\"Headline\"},\"messageId\":" + messageId + "}";
        System.out.println(json);

        String exchange = "DEX_PolicyGenerateMessage";
        String routingKey = "OLD_POLICY_37_MESSAGE_EXCHANGE_DEFAULT_KEY";
        RabbitMqDemo.send(exchange, routingKey, messageId, json.getBytes());
    }

    public static void sendMsg() throws Exception {
        String exchange = "DEX_PolicyGenerateMessage";
        String routingKey = "OLD_POLICY_37_MESSAGE_EXCHANGE_DEFAULT_KEY";
        long messageId = 1235963881;
        String json = "{\"content\":{" +
                "\"body\":\"{" +
                "\\\"comCode\\\":\\\"3701973321\\\"," +
                "\\\"inputDate\\\":\\\"2019-12-03\\\"," +
                "\\\"mainPolicyNo\\\":\\\"\\\"," +
                "\\\"messageId\\\":\\\"3651265125\\\"," +
                "\\\"mainProposalNo\\\":\\\"\\\"," +
                "\\\"policyNo\\\":\\\"805072019371300000001\\\"," +
                "\\\"policySort\\\":\\\"EM2\\\"," +
                "\\\"riskCode\\\":\\\"0507\\\"" +
                "}\"," +
                "\"type\":\"Headline\"},\"messageId\":" + messageId + "}";
        System.out.println(json);
        for (int i = 0; i < 1; i++) {
            //Thread.currentThread().sleep(0.01*1000);
            RabbitMqDemo.send(exchange, routingKey, messageId, json.getBytes());
        }
    }

    /**
     * MQ 消费者
     */
    static class ConsumerThread extends Thread {
        //队列名称
        private String QUEUE_NAME = "";
        public ConsumerThread(String QUEUE_NAME) {
            this.QUEUE_NAME = QUEUE_NAME;
        }
        @Override
        public void run() {
            try {
                Channel channel = RabbitMqDemo.getSingleton().createChannel();
                //channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                QueueingConsumer consumer = new QueueingConsumer(channel);
                channel.basicConsume(QUEUE_NAME, false, consumer);
                QueueingConsumer.Delivery delivery = null;
                while (true) {
                    try {
                        System.out.println("waiting " + QUEUE_NAME + " message...");
                        delivery = consumer.nextDelivery();
                        String message = new String(delivery.getBody());
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                        System.out.println("received " + QUEUE_NAME + " message:" + message);
                    } catch (Exception e) {
                        if (delivery != null) {
                            //重新放入队列
                            channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
                            //抛弃此条消息
                            //channel.basicNack(envelope.getDeliveryTag(), false, false);
                        }
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
    }
}
