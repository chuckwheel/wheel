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
import io.wheel.config.Protocol;
import io.wheel.engine.RpcRequest;
import io.wheel.engine.RpcResponse;
import io.wheel.engine.ServiceGateway;

/**
 * 
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public class NettyServer {

	private static Logger logger = LoggerFactory.getLogger(NettyServer.class);

	private Protocol protocol;

	private EventLoopGroup serverGroup;

	private ServerBootstrap serverBootstrap;

	private ServiceGateway serviceGateway;

	public NettyServer(Protocol protocol, ServiceGateway serviceGateway) {
		this.protocol = protocol;
		this.serviceGateway = serviceGateway;
	}

	public void start() throws Exception {
		int serverThreads = protocol.getParameterValue(NettyParameter.SERVER_THREADS);
		serverGroup = new NioEventLoopGroup(serverThreads);
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
		if (StringUtils.isNotBlank(protocol.getHost())) {
			serverBootstrap.bind(protocol.getHost(), protocol.getPort()).sync();
		} else {
			serverBootstrap.bind(protocol.getPort()).sync();
		}
		logger.warn("Netty server started on prot : {}", protocol.getPort());
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
				response = serviceGateway.service(request);
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
			cause.printStackTrace();
		}
	}

}
