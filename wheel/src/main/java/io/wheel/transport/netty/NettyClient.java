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
import io.wheel.config.Protocol;
import io.wheel.engine.RpcErrorCodes;
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

	private Protocol protocol;

	private EventLoopGroup clientGroup;

	private Bootstrap clientBootstrap;

	private AtomicLong invokeIds = new AtomicLong();

	private Map<String, Channel> channels = new ConcurrentHashMap<String, Channel>();

	private AttributeKey<ServiceInstance<ServiceInfo>> targetKey = AttributeKey.valueOf("TARGET");

	private AttributeKey<Boolean> closeableKey = AttributeKey.valueOf("CLOSEABLE");

	private AttributeKey<Boolean> writableKey = AttributeKey.valueOf("WRITABLE");

	private Map<Long, InvokeFuture<RpcResponse>> futures = new ConcurrentHashMap<Long, InvokeFuture<RpcResponse>>();

	public NettyClient(Protocol protocol) {
		this.protocol = protocol;
	}

	public void start() throws Exception {
		int clientThreads = protocol.getParameterValue(NettyParameter.CLIENT_THREADS);
		clientGroup = new NioEventLoopGroup(clientThreads);
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
		String protocols = serviceInfo.getProtocol(protocol.getName());
		if (protocols == null) {
			logger.error("Undefined protocol,protocol={}", protocol);
			throw new RpcException(RpcErrorCodes.UNDEFINED_PROTOCOL);
		}
		String[] values = protocols.split(":");
		String address = values[0];
		int port = Integer.parseInt(values[1]);
		String channelCode = address + ":" + port;
		Channel channel = channels.get(channelCode);
		if (channel != null && channel.isActive()) {
			if (channel.attr(writableKey).get()) {
				return channel;
			} else {
				// 新连接
				Channel newChannel = clientBootstrap.connect(address, port).sync().channel();
				newChannel.attr(closeableKey).set(true);
				return newChannel;
			}
		}
		try {
			channel = clientBootstrap.connect(address, port).sync().channel();
			if (channels.containsKey(channelCode)) {
				// 新连接
				channel.attr(closeableKey).set(true);
				channel.attr(targetKey).set(target);
				return channel;
			} else {
				channel.attr(closeableKey).set(false);
				channel.attr(targetKey).set(target);
				channels.put(channelCode, channel);
				return channel;
			}
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
			channel.attr(writableKey).set(false);
			channelFuture.addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) throws Exception {
					if (!future.isSuccess()) {
						futures.remove(invokeId);
						invokeFuture.setCause(future.cause());
					}
				}
			});
			RpcResponse result = invokeFuture.getResult(request.getTimeout(), TimeUnit.SECONDS);
			channel.attr(writableKey).set(true);
			if (result == null) {
				provider.noteError(channel.attr(targetKey).get());
				throw new RpcException();
			}
			return result;
		} catch (Exception e) {
			provider.noteError(channel.attr(targetKey).get());
			logger.error("Send and receive failed! invokeId={}", invokeId, e);
			throw new RpcException("", e);
		} finally {
			boolean closeable = channel.attr(closeableKey).get();
			if (closeable) {
				if (logger.isDebugEnabled()) {
					logger.debug("Close channel,channel={}", channel);
				}
				channel.close();
			}
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
					logger.warn("InvokeFuture is null,invokeId={}", invokeId);
				}
			} else {
				logger.warn("Received invalid message type,message={}" + message);
			}
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			// cause.printStackTrace();
		}

	}

}
