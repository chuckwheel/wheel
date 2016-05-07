package io.wheel.test.netty;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.wheel.engine.RpcResponse;
import io.wheel.transport.netty.InvokeFuture;

public class ClientHandler extends ChannelInboundHandlerAdapter {

	private static Logger logger = LoggerFactory.getLogger(NettyClient.class);
	
	private Map<Long, InvokeFuture<RpcResponse>> futures;
	
	public ClientHandler(Map<Long, InvokeFuture<RpcResponse>> futures) {
		this.futures = futures;
	}
	
	public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {

		if (message == null) {
			logger.warn("Received message is null!");
			return;
		}
		if (message instanceof RpcResponse) {
			RpcResponse response = ((RpcResponse) message);
			long invokeId = response.getInvokeId();
			InvokeFuture<RpcResponse> future = futures.remove(invokeId);
			logger.info("Receive response, invokeId={}", invokeId);
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
		//cause.printStackTrace();
	}

}
