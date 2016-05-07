package io.wheel.test.netty;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

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
import io.wheel.ErrorCodeException;
import io.wheel.engine.RpcRequest;
import io.wheel.engine.RpcResponse;
import io.wheel.transport.netty.HessianDecoder;
import io.wheel.transport.netty.HessianEncoder;
import io.wheel.transport.netty.InvokeFuture;

/**
 * 
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public class NettyClient {

	private static Logger logger = LoggerFactory.getLogger(NettyClient.class);

	private EventLoopGroup clientGroup;

	private Bootstrap clientBootstrap;

	private AtomicLong invokeIds = new AtomicLong();

	private Map<Long, InvokeFuture<RpcResponse>> futures = new ConcurrentHashMap<Long, InvokeFuture<RpcResponse>>();

	private Channel channel = null;
	
	public void start() throws Exception {
		clientGroup = new NioEventLoopGroup(10);
		clientBootstrap = new Bootstrap();
		clientBootstrap.group(clientGroup);
		clientBootstrap.channel(NioSocketChannel.class);
		clientBootstrap.option(ChannelOption.TCP_NODELAY, true);
		clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new HessianEncoder());
				ch.pipeline().addLast(new HessianDecoder());
				ch.pipeline().addLast(new ClientHandler(futures));
			}
		});
//		channel = clientBootstrap.connect("127.0.0.1", 9989).sync().channel();
	}
	
	public RpcResponse invoke(RpcRequest request) {
		final long invokeId = invokeIds.incrementAndGet();
		request.setInvokeId(invokeId);
		final InvokeFuture<RpcResponse> invokeFuture = new InvokeFuture<RpcResponse>();
		futures.put(invokeId, invokeFuture);
		try {
			Channel channel = clientBootstrap.connect("127.0.0.1", 9989).sync().channel();
			ChannelFuture channelFuture = channel.writeAndFlush(request);
			channelFuture.addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) throws Exception {
					if (!future.isSuccess()) {
						futures.remove(invokeId);
						invokeFuture.setCause(future.cause());
					}
					if(logger.isInfoEnabled()){
						logger.info("Send request, invokeId={}", invokeId);
					}
				}
			});
//			RpcResponse result = invokeFuture.getResult(10, TimeUnit.SECONDS);
//			if (result == null) {
//				throw new RpcException();
//			}
//			return result;
			return null;
		} catch (Exception e) {
			logger.error("Send and receive failed! invokeId={}", invokeId, e);
			throw new ErrorCodeException("", e);
		}
	}

	public static void main(String[] args) throws Exception {
		
		
		final NettyClient client = new NettyClient();
		try {
			client.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0;i<10000;i++){
			new Thread(){
				public void run() {
					for(int j=0;j<1;j++){
						
						RpcRequest request1 = new RpcRequest();
						client.invoke(request1);
					}
				};
			}.start();
		}
	}
}
