package com.share.netty4;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
/**
 * netty4服务端入门
 * @author hanjie.l
 *
 */
public class Netty4Server {

	public static void main(String[] args) throws Exception{

		//服务类
		ServerBootstrap bootstrap = new ServerBootstrap();

		//boss线程监听端口，worker线程负责数据读写
		NioEventLoopGroup boss = new NioEventLoopGroup(1);
		NioEventLoopGroup worker = new NioEventLoopGroup(4);

		bootstrap.group(boss, worker);
		//设置channel工厂
		bootstrap.channel(NioServerSocketChannel.class);

		//设置管道的工厂
		bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
			@Override
			public void initChannel(NioSocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast("decoder",new StringDecoder());
				pipeline.addLast("encoder",new StringEncoder());
				pipeline.addLast("helloHandler", new Netty4HelloHandler());
			}
		});

		bootstrap.bind(10101).sync();

		System.out.println("netty4 服务器启动成功");
	}

}