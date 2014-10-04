package ctd.sokolov.network;

import ctd.sokolov.network.model.TableString;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;

/**
 * author: Ruslan Sokolov
 * date: 10/3/14
 */
public class BroadcastReceiver {
    private static volatile boolean IS_RUN = false;
    private static byte[] receiveData = new byte[1024];
    private static Map<TableString, Deque<Long>> table = new HashMap<>();
    private static JFrame frame;
    private static JTextArea textArea;
    private static Set<TableString> sorted = new TreeSet<>();

    static {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Udp Example");
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        textArea = new JTextArea();
        frame.add(textArea);
        Font font = new Font("Verdana", Font.PLAIN, 18);
        textArea.setFont(font);
        textArea.setEditable(false);
    }

    public static void runReceiver() throws IOException {
        if (IS_RUN) {
            return;
        }
        IS_RUN = true;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                    DatagramSocket clientSocket = BroadcastSender.getSocket();
                    while (IS_RUN) {
                        try {
                            drawTable();
                            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                            clientSocket.receive(receivePacket);
                            byte[] data = new byte[receivePacket.getLength()];
                            System.arraycopy(receivePacket.getData(), 0, data, 0, receivePacket.getLength());
                            if (!Arrays.equals(data, BroadcastSender.getMsg())) {
                                long time = System.currentTimeMillis();
                                TableString tb = new TableString(data);
                                if (!table.containsKey(tb)) {
                                    table.put(tb, new ArrayDeque<>(Arrays.asList(time)));
                                } else {
                                    table.get(tb).add(time);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            }
        });
        t.start();

    }

    private static void drawTable() throws IOException {
        for (Iterator<Map.Entry<TableString, Deque<Long>>> it = table.entrySet().iterator();
             it.hasNext();) {
            Map.Entry<TableString, Deque<Long>> entry = it.next();
            Deque<Long> currentDeque = entry.getValue();
            TableString tb = entry.getKey();
            if (currentDeque.size() == 0) {
                continue;
            }
            long time = System.currentTimeMillis();
            if (currentDeque.size() > 10) {
                currentDeque.removeFirst();
            }
            long count = (time - currentDeque.getFirst()) / 2000;
            int lost = currentDeque.size() >= count ? 0 : (int) count - currentDeque.size();
            tb.setLost(lost);
//                System.out.println(tb.getName() + " " + tb.getIpAddress() + " " + tb.getMacAddress() + " lost: " +
//                        lost + " listTime: " + (time - tb.getLastTime()));
//            textArea.append(tb.getName() + " " + tb.getIpAddress() + " " + tb.getMacAddress() + " lost: " +
//                    tb.getLost() + " listTime: " + (tb.getTime()) + "\n");
            if (currentDeque.size() > 0) {
                tb.setTime(time - tb.getLastTime());
                tb.setLastTime(currentDeque.getLast());
            }
            sorted.add(tb);
            if (lost >= 10) {
                it.remove();
            }
        }
        for (TableString tb : sorted) {
            textArea.append(tb.getName() + " " + tb.getIpAddress() + " " + tb.getMacAddress() + " lost: " +
                    tb.getLost() + " listTime: " + (tb.getTime()) + "\n");
        }

        sorted.clear();
        try {
            Thread.currentThread().sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        textArea.setText("");
    }

    public static void stopReceiver() {
        IS_RUN = false;
    }
}