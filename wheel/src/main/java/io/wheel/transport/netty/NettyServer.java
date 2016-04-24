package io.wheel.transport.netty;

import org.apache.commons.lang.StringUtils;
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

	private NettyTransporter nettyTransport;

	public NettyServer(NettyTransporter nettyTransport) {
		this.nettyTransport = nettyTransport;
	}

	public void start() throws Exception {
		serverGroup = new NioEventLoopGroup(nettyTransport.getServerThreads());
		serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(serverGroup);
		serverBootstrap.channel(NioServerSocketChannel.class);
		serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new HessianEncoder());
				ch.pipeline().addLast(new HessianDecoder());
				ch.pipeline().addLast(new ServerHandler());
			}
		});
		if (StringUtils.isNotBlank(nettyTransport.getHost())) {
			serverBootstrap.bind(nettyTransport.getHost(), nettyTransport.getPort()).sync();
		} else {
			serverBootstrap.bind(nettyTransport.getPort()).sync();
		}
		logger.warn("Netty server started!prot={}", nettyTransport.getPort());
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
				response = nettyTransport.getServiceGateway().service(request);
			} catch (Exception e) {
				logger.warn("Process client failed! msgid={}", request.getInvokeId(), e);
				throw e;
			}
			if (response != null) {
				response.setInvokeId(request.getInvokeId());
				ctx.writeAndFlush(response);
			}
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			cause.printStackTrace();
		}
	}

}
