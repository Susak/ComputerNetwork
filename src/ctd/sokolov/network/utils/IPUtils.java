package ctd.sokolov.network.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * author: Ruslan Sokolov
 * date: 10/3/14
 */
public class IPUtils {
    public static InetAddress getAllIPs() {
        List<InetAddress> addresses = new ArrayList<>();
        Enumeration e;
        try {
            e = NetworkInterface.getNetworkInterfaces();

            while(e.hasMoreElements())
            {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements())
                {
                    InetAddress i = (InetAddress) ee.nextElement();
                    if (i.isSiteLocalAddress()) {
                        addresses.add(i);
                    }
                }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        assert addresses.size() != 1;
        return addresses.get(0);
    }
}
