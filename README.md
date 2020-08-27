### 一、前言
Netty 是一个基于NIO的客户、服务器端编程框架，使用Netty 可以确保你快速和简单的开发出一个网络应用，Netty的主要应用领域在 游戏服务器，物联网开发， RPC通讯

* 用Netty做服务端的知名游戏商有
网易部分手游工作室，如 率土之滨
盛和游戏， 是兄弟就来砍我....... 大家好，我是渣渣辉........
三七玩
第七大道
等......

* 用netty做通讯组件的框架hadoop  dubbo  redisson  vert.x....

### 一、OIO与NIO   同步阻塞IO与同步非阻塞IO

因为Netty是基于NIO的一个框架， 学习Netty源码不可避免的要学习下NIO， 所以我们第一个话题是通过一些DEMO代码来学习NIO， 并了解NIO与IO的区别
###### OIO 代码示例
```

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 传统socket服务端(单线程版)
 * @author hanjie.l
 *
 */
public class OioServer {

	public static void main(String[] args) throws Exception {
		//创建socket服务,监听10101端口
		ServerSocket server=new ServerSocket(10101);
		System.out.println("服务器启动！");
		while(true){
			//获取一个套接字（阻塞）
			final Socket socket = server.accept();
			System.out.println("来个一个新客户端！");
			handler(socket);
		}
	}
	
	/**
	 * 读取数据
	 * @param socket
	 * @throws Exception
	 */
	public static void handler(Socket socket){
		try {
			byte[] bytes = new byte[1024];
			InputStream inputStream = socket.getInputStream();

			while(true){
				//读取数据（阻塞）
				int read = inputStream.read(bytes);
				if(read != -1){
					System.out.println(new String(bytes, 0, read));
				}else{
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				System.out.println("socket关闭");
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
```
NIO 代码示例
```

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
/**
 * NIO服务端
 * 
 * @author hanjie.l
 */
public class NIOServer {
	// 通道管理器
	private Selector selector;

	/**
	 * 获得一个ServerSocket通道，并对该通道做一些初始化的工作
	 * 
	 * @param port
	 *            绑定的端口号
	 * @throws IOException
	 */
	public void initServer(int port) throws IOException {
		// 获得一个ServerSocket通道
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		// 设置通道为非阻塞
		serverChannel.configureBlocking(false);
		// 将该通道对应的ServerSocket绑定到port端口
		serverChannel.socket().bind(new InetSocketAddress(port));
		// 获得一个通道管理器(多路复用器)
		this.selector = Selector.open();
		// 将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件,注册该事件后，
		// 当该事件到达时，selector.select()会返回，如果该事件没到达selector.select()会一直阻塞。
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
	}

	/**
	 * 采用轮询的方式监听selector上是否有需要处理的事件，如果有，则进行处理
	 * 
	 * @throws IOException
	 */
	public void listen() throws IOException {
		System.out.println("服务端启动！");
		// 轮询访问selector
		while (true) {
			try {
				// 当注册的事件到达时，方法返回；否则,该方法会一直阻塞
				selector.select();
				// 获得selector中选中的项的迭代器，选中的项为注册的事件
				Iterator<?> ite = this.selector.selectedKeys().iterator();
				while (ite.hasNext()) {
					SelectionKey key = (SelectionKey) ite.next();
					// 删除已选的key,以防重复处理
					ite.remove();

					handler(key);
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * 处理请求
	 * 
	 * @param key
	 * @throws IOException
	 */
	public void handler(SelectionKey key) throws IOException {
		// 客户端请求连接事件
		if (key.isAcceptable()) {
			handlerAccept(key);
			// 获得了可读的事件
		} else if (key.isReadable()) {
			handelerRead(key);
		}
	}

	/**
	 * 处理连接请求
	 * 
	 * @param key
	 * @throws IOException
	 */
	public void handlerAccept(SelectionKey key) throws IOException {
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		// 获得和客户端连接的通道
		SocketChannel channel = server.accept();
		// 设置成非阻塞
		channel.configureBlocking(false);

		System.out.println("新的客户端连接");
		// 将通道管理器和该通道绑定，监听读事件
		channel.register(this.selector, SelectionKey.OP_READ);
	}

	/**
	 * 处理读的事件
	 * 
	 * @param key
	 * @throws IOException
	 */
	public void handelerRead(SelectionKey key) throws IOException {
		// 服务器可读取消息:得到事件发生的会话通道
		SocketChannel channel = (SocketChannel) key.channel();
		// 创建buffer
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		int read = channel.read(buffer);
		if(read > 0){
			byte[] data = buffer.array();
			String msg = new String(data).trim();
			System.out.println("服务端收到信息：" + msg);
			
			//回写数据
			ByteBuffer outBuffer = ByteBuffer.wrap("好的".getBytes());
			channel.write(outBuffer);// 将消息回送给客户端
		}else{
			System.out.println("客户端关闭");
			key.cancel();
		}
	}

	/**
	 * 启动服务端测试
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		NIOServer server = new NIOServer();
		server.initServer(10102);
		server.listen();
	}

}

```

---

### 上2张图说明下OIO和NIO区别
OIO系统
![image.png](https://upload-images.jianshu.io/upload_images/3876371-b98378a14ab68c11.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

NIO系统
![image.png](https://upload-images.jianshu.io/upload_images/3876371-7fb8d3733a3d1ab7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


---
### NIO API总结
* Selector  在nio中最核心的类，用于监控通道状态， 核心方法
```
// 监听通道状态变化，变化时候返回，否则阻塞
selector.select();
//返回变化的通道
selector.selectedKeys();
//用于唤醒select阻塞状态
selector.wakeup();
```
* ServerSocketChannel  socket监听通道
 ```
 // 监听端口
serverChannel.socket().bind(new InetSocketAddress(port));
// 将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件
serverChannel.register(selector, SelectionKey.OP_ACCEPT);
```
* SocketChannel  socket会话通道
```
 //  将通道管理器和该通道绑定，监听读事件
 channel.register(this.selector, SelectionKey.OP_READ);
```

### Netty线程源码分析

用嘴说吧.......打字太麻烦


### Netty线程模型图

![image.png](https://upload-images.jianshu.io/upload_images/3876371-e152f10606b63a86.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


分享代码地址
https://github.com/hanjieliao/ServerShare.git
