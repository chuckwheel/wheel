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

public class InetAddressHelper {

	public static void main(String[] args) throws SocketException {
		System.out.println(getAllLocalIPs());
	}

	private static final AtomicReference<LocalIpFilter> localIpFilter = new AtomicReference<LocalIpFilter>(
			new LocalIpFilter() {
				@Override
				public boolean use(NetworkInterface nif, InetAddress adr) throws SocketException {
					return (adr != null) && !adr.isLoopbackAddress()
							&& (nif.isPointToPoint() || !adr.isLinkLocalAddress());
				}
			});

	public static Collection<InetAddress> getAllLocalIPs() throws SocketException {
		List<InetAddress> listAdr = Lists.newArrayList();
		Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
		if (nifs == null)
			return listAdr;

		while (nifs.hasMoreElements()) {
			NetworkInterface nif = nifs.nextElement();
			// We ignore subinterfaces - as not yet needed.

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
}
