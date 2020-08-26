package com.share.netty3;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
/**
 * 消息接受处理类
 * @author hanjie.l
 *
 */
public class Netty3HelloHandler extends SimpleChannelHandler {

	/**
	 * 接收消息
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		String s = (String) e.getMessage();
		System.out.println("收到数据:" + s);
		
		//回写数据
		ctx.getChannel().write("server has receieve messsage!!!");
		super.messageReceived(ctx, e);
	}

	/**
	 * 新连接
	 */
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		System.out.println("新连接-------------");
		super.channelConnected(ctx, e);
	}


}