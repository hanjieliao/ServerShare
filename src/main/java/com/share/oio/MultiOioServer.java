package com.share.oio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 传统socket服务端(多线程版)
 * @author hanjie.l
 *
 */
public class MultiOioServer {

    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {

        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        //创建socket服务,监听10101端口
        ServerSocket server=new ServerSocket(10101);
        System.out.println("服务器启动！");
        while(true){
            //获取一个套接字（阻塞）
            final Socket socket = server.accept();
            System.out.println("来个一个新客户端！");
            //新启一个线程来读取数据
            newCachedThreadPool.execute(() -> {
                handler(socket);
            });
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
