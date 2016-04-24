package io.wheel.transport.netty;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import io.wheel.engine.RpcException;
import io.wheel.engine.RpcRequest;
import io.wheel.engine.RpcResponse;
import io.wheel.registry.ServiceInfo;

/**
 * 
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public class NettyClient {

	private static Logger logger = LoggerFactory.getLogger(NettyClient.class);

	private Map<Long, InvokeFuture<RpcResponse>> futures = new ConcurrentHashMap<Long, InvokeFuture<RpcResponse>>();

	private Map<String, Channel> channels = new ConcurrentHashMap<String, Channel>();

	private EventLoopGroup clientGroup;

	private Bootstrap clientBootstrap;

	private AtomicLong invokeIds = new AtomicLong();

	private NettyTransporter nettyTransport;

	private AttributeKey<ServiceInstance<ServiceInfo>> targetKey = AttributeKey.valueOf("TARGET");

	public NettyClient(NettyTransporter nettyTransport) {
		this.nettyTransport = nettyTransport;
	}

	public void start() throws Exception {
		clientGroup = new NioEventLoopGroup(nettyTransport.getClientThreads());
		clientBootstrap = new Bootstrap();
		clientBootstrap.group(clientGroup);
		clientBootstrap.channel(NioSocketChannel.class);
		clientBootstrap.option(ChannelOption.TCP_NODELAY, true);
		clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new HessianEncoder());
				ch.pipeline().addLast(new HessianDecoder());
				ch.pipeline().addLast(new ClientHandler());
			}
		});
	}

	private Channel getChannel(ServiceProvider<ServiceInfo> provider) throws Exception {
		ServiceInstance<ServiceInfo> target = provider.getInstance();
		ServiceInfo serviceInfo = target.getPayload();
		String protocol = serviceInfo.getProtocol(nettyTransport.getName());
		if (protocol == null) {
			logger.error("");
			throw new RpcException("");
		}
		String[] values = protocol.split(":");
		String address = values[0];
		int port = Integer.parseInt(values[1]);
		String channelCode = address + ":" + port;
		Channel channel = channels.get(channelCode);
		if (channel != null && channel.isActive()) {
			return channel;
		}
		try {
			channel = clientBootstrap.connect(address, port).sync().channel();
			channels.put(channelCode, channel);
			channel.attr(targetKey).set(target);
			return channel;
		} catch (Exception e) {
			// 标记为不可用
			provider.noteError(target);
			logger.error("Init client connector failed! serverId:", e);
			throw new RpcException("", e);
		}
	}

	public RpcResponse invoke(ServiceProvider<ServiceInfo> provider, RpcRequest request) {
		Channel channel = null;
		final long invokeId = invokeIds.incrementAndGet();
		request.setInvokeId(invokeId);
		final InvokeFuture<RpcResponse> invokeFuture = new InvokeFuture<RpcResponse>();
		futures.put(invokeId, invokeFuture);
		try {
			channel = this.getChannel(provider);
			ChannelFuture channelFuture = channel.writeAndFlush(request);
			channelFuture.addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) throws Exception {
					if (!future.isSuccess()) {
						futures.remove(invokeId);
						invokeFuture.setCause(future.cause());
					}
				}
			});
			RpcResponse result = invokeFuture.getResult(request.getTimeout(), TimeUnit.SECONDS);
			if (result == null) {
				provider.noteError(channel.attr(targetKey).get());
				throw new RpcException();
			}
			return result;
		} catch (Exception e) {
			provider.noteError(channel.attr(targetKey).get());
			logger.error("Send and receive failed! ServerId:", e);
			throw new RpcException("", e);
		}
	}

	private class ClientHandler extends ChannelInboundHandlerAdapter {

		public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
			if (message == null) {
				logger.warn("Received message is null!");
				return;
			}
			if (message instanceof RpcResponse) {
				RpcResponse response = ((RpcResponse) message);
				long invokeId = response.getInvokeId();
				InvokeFuture<RpcResponse> future = futures.remove(invokeId);
				if (future != null) {
					future.setResult(response);
				} else {
					logger.warn("InvokeFuture is null,invokeId=" + invokeId);
				}
			} else {
				logger.warn("Received message type is !" + message);
			}
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			cause.printStackTrace();
		}

	}

}
