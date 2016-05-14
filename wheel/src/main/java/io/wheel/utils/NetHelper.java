package io.wheel.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.curator.x.discovery.LocalIpFilter;

import com.google.common.collect.Lists;

/**
 * InetAddressHelper
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public class NetHelper {

	private static final AtomicReference<LocalIpFilter> localIpFilter = new AtomicReference<LocalIpFilter>(
			new LocalIpFilter() {
				@Override
				public boolean use(NetworkInterface nif, InetAddress adr) throws SocketException {
					return (adr != null) && !adr.isLoopbackAddress()
							&& (nif.isPointToPoint() || !adr.isLinkLocalAddress());
				}
			});

	public static Collection<InetAddress> getAllLocalIps() throws Exception {
		List<InetAddress> listAdr = Lists.newArrayList();
		Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
		if (nifs == null) {
			return listAdr;
		}
		while (nifs.hasMoreElements()) {
			NetworkInterface nif = nifs.nextElement();
			Enumeration<InetAddress> adrs = nif.getInetAddresses();
			while (adrs.hasMoreElements()) {
				InetAddress adr = adrs.nextElement();
				if (localIpFilter.get().use(nif, adr)) {
					listAdr.add(adr);
				}
			}
		}
		return listAdr;
	}

	public static InetAddress getLocalIp() throws Exception {
		return getAllLocalIps().iterator().next();
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(getAllLocalIps());
	}
}
