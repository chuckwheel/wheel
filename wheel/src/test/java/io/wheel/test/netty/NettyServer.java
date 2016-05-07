package io.wheel.test.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.wheel.engine.RpcRequest;
import io.wheel.engine.RpcResponse;
import io.wheel.transport.netty.HessianDecoder;
import io.wheel.transport.netty.HessianEncoder;

/**
 * 
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public class NettyServer {

	private static Logger logger = LoggerFactory.getLogger(NettyServer.class);

	private EventLoopGroup serverGroup;

	private ServerBootstrap serverBootstrap;

	public void start() throws Exception {
		serverGroup = new NioEventLoopGroup(1);
		serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(serverGroup,new NioEventLoopGroup(10));
		serverBootstrap.channel(NioServerSocketChannel.class);
		serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new HessianEncoder());
				ch.pipeline().addLast(new HessianDecoder());
				ch.pipeline().addLast(new ServerHandler());
			}
		});
		serverBootstrap.bind(9989).sync();
		logger.warn("Netty server started on prot : {}", 9989);
		serverBootstrap.bind(9988).sync();
		logger.warn("Netty server started on prot : {}", 9989);
	}

	private class ServerHandler extends ChannelInboundHandlerAdapter {

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
			if (message == null) {
				logger.warn("Received message is null!");
				return;
			}
			RpcRequest request = (RpcRequest) message;
			RpcResponse response = null;
			try {
				logger.warn("recv msg : {}",request);
				Thread.sleep(2000);
				response = new RpcResponse();
			} catch (Exception e) {
				logger.warn("Process client failed!invokeId={}", request.getInvokeId(), e);
				throw e;
			}
			if (response != null) {
				response.setInvokeId(request.getInvokeId());
				ctx.writeAndFlush(response);
			}
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			//cause.printStackTrace();
//			logger.warn("error----->");
		}
		
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			logger.info("" + ctx.channel());
			super.channelActive(ctx);
		}
	}

	
	public static void main(String[] args) throws Exception {
		NettyServer server = new NettyServer();
		server.start();
	}
}
