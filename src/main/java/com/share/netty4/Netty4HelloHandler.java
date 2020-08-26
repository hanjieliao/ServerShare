package com.share.netty4;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
/**
 * 消息接受处理类
 * @author hanjie.l
 *
 */
public class Netty4HelloHandler extends SimpleChannelInboundHandler<String> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		System.out.println("收到数据:" + msg);
		//回写数据
		ctx.channel().writeAndFlush("server has receieve messsage!!!");
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("新连接-------------");
	}
}