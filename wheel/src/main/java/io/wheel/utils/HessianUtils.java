package io.wheel.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import io.wheel.transport.http.HttpGateway;

public class HessianUtils {

	private static Log log = LogFactory.getLog(HttpGateway.class);

	public static Object bytesToObject(byte[] bytes) {
		if (bytes == null) {
			log.warn("Input bytes is null!");
			return null;
		}
		ByteArrayInputStream bis = null;
		HessianInput hi = null;
		try {
			bis = new ByteArrayInputStream(bytes);
			hi = new HessianInput(bis);
			return hi.readObject();
		} catch (Exception e) {
			log.error("Bytes to object failed!", e);
			return null;
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (Exception e) {
					log.error("Close ByteArrayInputStream failed!", e);
				}
			}
			if (hi != null) {
				try {
					hi.close();
				} catch (Exception e) {
					log.error("Close HessianInput failed!", e);
				}
			}
		}
	}

	public static byte[] objectToBytes(Object object) {
		if (object == null) {
			log.warn("Input object is null! ");
			return null;
		}
		ByteArrayOutputStream bos = null;
		HessianOutput ho = null;
		try {
			bos = new ByteArrayOutputStream();
			ho = new HessianOutput(bos);
			ho.writeObject(object);
			return bos.toByteArray();
		} catch (Exception e) {
			log.error("Object to bytes failed!", e);
			return null;
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e) {
					log.error("Close ByteArrayOutputStream failed!", e);
				}
			}
			if (ho != null) {
				try {
					ho.close();
				} catch (Exception e) {
					log.error("Close HessianOutput failed!", e);
				}
			}
		}
	}
}
