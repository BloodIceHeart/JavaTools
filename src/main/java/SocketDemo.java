import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

public class SocketDemo {

    public static void main(String[] args) throws Exception {
        SocketDemo demo = new SocketDemo();
        Server server = demo.new Server();
        Client client = demo.new Client();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    countDownLatch.countDown();
                    server.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        countDownLatch.await();
        client.sendMsg();
        client.sendMsg();
    }

    /**
     * 客户端
     */
    public class Client {
        public void sendMsg() throws Exception {

            String clientMessage;// 来自用户输入的的信息
            String serverMessage; // 服务器端的信息
            // 参数是本机地址和端口 客户端套接字，发起 TCP连接
            Socket ClientSocket = new Socket("9.7.26.168", 55557);
            // 字符读取流，获取从键盘输入的字符
            System.out.println("客户端获取从键盘输入的字符：");
            BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));
            // 获取从服务器端的流，建立套接字输入流
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(ClientSocket.getInputStream()));
            // 建立套接字输出流
            DataOutputStream toServer = new DataOutputStream(ClientSocket.getOutputStream());
            clientMessage = fromUser.readLine();// 读取从用户的输入
            System.out.println("客户端发送键盘输入的字符：" + clientMessage);
            toServer.writeUTF(clientMessage);// 写到服务器端

            serverMessage = fromServer.readLine();// 从服务器端读取
            System.out.println("客户端从服务器端读取的字符：" + serverMessage);
            ClientSocket.close();// 关闭套接字连接
        }
    }

    /**
     * 服务端
     */
    public class Server {
        public void start() throws Exception {
            String ClientMessage;
            String ServerMessage;
            ServerSocket serversocket = new ServerSocket(55557);// 端口要和客户端对应
            System.err.println("服务端正在监听 55557 端口");
            while (true) {
                Socket collection = serversocket.accept();// 调用 accept() 函数，建立 TCP 连接
                System.err.println("服务端成功建立 TCP 连接");
                DataInputStream fromClient = new DataInputStream(collection.getInputStream());
                DataOutputStream toClient = new DataOutputStream(collection.getOutputStream());
                ClientMessage = fromClient.readUTF();// 接收来自客户端的信息
                System.err.println("服务端读取数据：" + ClientMessage);
                ServerMessage = ClientMessage + "\n";
                System.err.println("服务端发送数据：" + ServerMessage);
                toClient.writeBytes(ServerMessage);// 写到服务器端
            }
        }
    }
}
