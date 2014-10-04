package ctd.sokolov.network;

import ctd.sokolov.network.utils.ByteHelper;
import ctd.sokolov.network.utils.IPUtils;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

/**
 * author: Ruslan Sokolov
 * date: 10/3/14
 */
public class BroadcastSender {
    public static final int PORT = 7777;
    private static volatile boolean IS_RUN = false;
    private static DatagramSocket serverSocket;
    private static byte[] msg;

    static {
        try {
            byte[] ip = IPUtils.getAllIPs().getAddress();
            byte[] mac = getMac();
            byte[] secondName = "Sokolov".getBytes();
            byte[] zero = new byte[1];
            zero[0] = 0;
            msg = ByteHelper.concatenateByteArrays(ip, mac, secondName, zero);
            serverSocket = new DatagramSocket(PORT);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }
    private BroadcastSender() {}

    public static byte[] getMsg() {
        return msg;
    }

    public static DatagramSocket getSocket() {
        return serverSocket;
    }
    private static byte[] getMac() throws SocketException, UnknownHostException {
        Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
        byte[] mac = null;
        while(networks.hasMoreElements()) {
            NetworkInterface network = networks.nextElement();
            mac = network.getHardwareAddress();
            if (mac != null) {
                break;
            }
        }
        return mac;
    }

    public static void runServer() {
        if (!IS_RUN) {
            IS_RUN = true;
            Timer t = new Timer();
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        InetAddress IPAddress = InetAddress.getByName("255.255.255.255");
//                        synchronized (System.out) {
//                            System.out.println("SEND MESSAGE");
//                        }
                        DatagramPacket sendPacket = new DatagramPacket(msg, msg.length,
                                IPAddress, PORT);
                        serverSocket.send(sendPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 0l, 2000l);
        }
    }

    public static void stopServer() {
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    @Override
    protected void finalize() {
        if (serverSocket != null) {
            serverSocket.close();
        }
    }
}
