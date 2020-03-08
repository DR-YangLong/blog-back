title: JAVA原生NIO示例        
date: 2017-05-09 12:00:56       
categories: [java]      
tags: [nio]
---

## 抽象类，抽象通用方法

```java
/**
 * functional describe:selector监听处理方法
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0    2018/7/10
 */
public abstract class AbstractNio {

    /**
     * 模板方法
     *
     * @param selector 选择器
     * @throws IOException
     */
    void listen(Selector selector) throws IOException {
        while (true) {
            //阻塞，直到有链接
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                handlerKey(key);
            }
        }
    }

    /**
     * 得到时间通知后具体的处理方法
     *
     * @param key
     * @throws IOException
     */
    abstract void handlerKey(SelectionKey key) throws IOException;
}
```

<!-- more -->
## 服务端

```java
/**
 * functional describe: nio服务端，接受客户端连接，读取客户端发送的消息打印到控制台，并返回消息。
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0    2018/4/3
 */
public class NioServer extends AbstractNio {
    private static final int BUFFER_SIZE = 2048;
    private ByteBuffer sendBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private ByteBuffer receiveBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private Selector selector;

    void openServer(int port) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(port));
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("start server on port:" + port);
    }

    @Autowired
    void handlerKey(SelectionKey key) throws IOException {
        ServerSocketChannel server;
        SocketChannel client;
        int count;
        if (key.isAcceptable()) {
            //1.服务端启动后，首先注册读事件，等待客户端输入
            server = (ServerSocketChannel) key.channel();
            client = server.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        } else if (key.isReadable()) {
            //2.当服务端可读时，读取客户端输入，然后注册写事件
            client = (SocketChannel) key.channel();
            receiveBuffer.clear();
            count = client.read(receiveBuffer);
            if (count > 0) {
                receiveBuffer.flip();
                String msg = new String(receiveBuffer.array(), 0, receiveBuffer.limit());
                System.out.println("server received msg:" + msg);
                client.register(selector, SelectionKey.OP_WRITE);
            }
        } else if (key.isWritable()) {
            //3.当服务端可写时，向客户端发送数据，然后注册读事件
            sendBuffer.clear();
            client = (SocketChannel) key.channel();
            String msg = "msg from server";
            sendBuffer.put(msg.getBytes());
            sendBuffer.flip();
            client.write(sendBuffer);
            client.register(selector, SelectionKey.OP_READ);
        }
    }


    public static void main(String[] args) throws IOException {
        int port = 9997;
        NioServer nioServer = new NioServer();
        nioServer.openServer(port);
        nioServer.listen(nioServer.getSelector());
    }

    public Selector getSelector() {
        return selector;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }
}
```

## 客户端

```java
/**
 * functional describe: nio客户端，连接服务端，向服务端发送消息，接收服务端消息。
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0    2018/4/3
 */
public class NioClient extends AbstractNio {
    private Selector selector;
    private static final int BUFFER_SIZE = 2048;
    private ByteBuffer sendBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private ByteBuffer receiveBuffer = ByteBuffer.allocate(BUFFER_SIZE);

    void connectedServer(final String ip, final int port) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(ip, port));
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        System.out.println("client connect to server!");
    }

    @Override
    void handlerKey(SelectionKey key) throws IOException {
        SocketChannel client;
        int count;
        if (key.isConnectable()) {
            //1.客户端连接上时，注册写事件
            client = (SocketChannel) key.channel();
            if (client.isConnectionPending()) {
                client.finishConnect();
            }
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_WRITE);
        } else if (key.isReadable()) {
            //2.客户端可读时，读取从服务端发送过来的数据，然后注册写事件
            client = (SocketChannel) key.channel();
            receiveBuffer.clear();
            count = client.read(receiveBuffer);
            if (count > 0) {
                receiveBuffer.flip();
                String msg = new String(receiveBuffer.array(), 0, receiveBuffer.limit());
                System.out.println("client received msg:" + msg);
                client.register(selector, SelectionKey.OP_WRITE);
            }
        } else if (key.isWritable()) {
            //3.客户端可写时，向服务端发送数据，并注册读事件
            sendBuffer.clear();
            client = (SocketChannel) key.channel();
            String msg = "msg from client";
            sendBuffer.put(msg.getBytes());
            sendBuffer.flip();
            client.write(sendBuffer);
            client.register(selector, SelectionKey.OP_READ);
        }
    }

    public static void main(String[] args) throws IOException {
        NioClient client = new NioClient();
        client.connectedServer("127.0.0.1", 9997);
        client.listen(client.getSelector());
    }

    public Selector getSelector() {
        return selector;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }
}
```