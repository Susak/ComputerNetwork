package ctd.sokolov.network.model;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * author: Ruslan Sokolov
 * date: 10/3/14
 */
public class TableString implements Comparable<TableString> {
    private String name;
    private String ipAddress;
    private String macAddress;
    private long lastTime = System.currentTimeMillis();
    private long time;
    private int lost;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TableString)) return false;

        TableString that = (TableString) o;

        return ipAddress.equals(that.ipAddress) && macAddress.equals(that.macAddress) && name.equals(that.name);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + ipAddress.hashCode();
        result = 31 * result + macAddress.hashCode();
        return result;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public TableString(byte[] array) {
        decode(array, array.length);
    }

    private void decode(byte[] array, int len) {
        byte[] ip = new byte[4];
        byte[] mac = new byte[6];
        System.arraycopy(array, 0, ip, 0, 4);
        System.arraycopy(array, 4, mac, 0, 6);
        this.name = new String(array, 10, len - 11);
        try {
            this.ipAddress = InetAddress.getByAddress(ip).toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }
        this.macAddress = sb.toString();
    }

    public int getLost() {
        return lost;
    }

    public void setLost(int lost) {
        this.lost = lost;
    }

    @Override
    public int compareTo(TableString o) {
        int i = new Integer(lost).compareTo(o.getLost());
        return i == 0 ? name.compareTo(o.getName()) : i;
    }
}
