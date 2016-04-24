package io.wheel.transport.netty;

import java.io.Serializable;

import com.caucho.hessian.io.HessianOutput;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Hessian Encoder
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public class HessianEncoder extends MessageToByteEncoder<Serializable> {

	private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

	@Override
	protected void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception {
		int startIdx = out.writerIndex();
		ByteBufOutputStream bout = null;
		HessianOutput hout = null;
		try {
			bout = new ByteBufOutputStream(out);
			bout.write(LENGTH_PLACEHOLDER);
			hout = new HessianOutput(bout);
			hout.writeObject(msg);
		} finally {
			hout.close();
			bout.close();
		}
		int endIdx = out.writerIndex();
		out.setInt(startIdx, endIdx - startIdx - 4);
	}

}
