import ctd.sokolov.network.BroadcastReceiver;
import ctd.sokolov.network.BroadcastSender;
import ctd.sokolov.network.utils.IPUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * author: Ruslan Sokolov
 * date: 10/3/14
 */
public class Main {
    public static void main(String[] args) throws IOException {
        BroadcastSender.runServer();
        BroadcastReceiver.runReceiver();
    }
}
