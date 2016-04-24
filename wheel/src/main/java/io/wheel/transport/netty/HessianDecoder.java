package io.wheel.transport.netty;

import com.caucho.hessian.io.HessianInput;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Hessian Decoder
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public class HessianDecoder extends LengthFieldBasedFrameDecoder {

	public HessianDecoder() {
		super(1048576, 0, 4, 0, 4);
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		ByteBuf frame = (ByteBuf) super.decode(ctx, in);
		if (frame == null) {
			return null;
		}
		ByteBufInputStream bin = null;
		HessianInput hin = null;
		try {
			bin = new ByteBufInputStream(frame);
			hin = new HessianInput(bin);
			return hin.readObject();
		} finally {
			bin.close();
			hin.close();
		}
	}

	@Override
	protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
		return buffer.slice(index, length);
	}
}