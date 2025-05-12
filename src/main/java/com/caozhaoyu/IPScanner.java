package com.caozhaoyu;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author Caozhaoyu
 */
public class IPScanner {
    public static void main(String[] args) {
        String network = "192.168.0";
        for (int i = 0; i <= 254; i++) {
            String ipToScan = network + "." + i;
            new Thread(() -> validIp(ipToScan)).start();
        }
    }

    public static void validIp(String ipToScan) {
        try {
            InetAddress address = InetAddress.getByName(ipToScan);
            // 1秒超时
            if (address.isReachable(1000)) {
                System.out.println(ipToScan + " is up.");
                for (int port = 1; port <= 65535; port++) {
                    if (isPortOpen(ipToScan, port)) {
                        System.out.println("IP:\t" + ipToScan + "\tPort " + port + " is open.");
                    }
                }
            }
        } catch (UnknownHostException e) {
            // Handle exception
        } catch (Exception e) {
            // Handle exception
        }
    }

    public static boolean isPortOpen(String ip, int port) {
        try (Socket socket = new Socket(ip, port)) {
            // 设置超时时间为1秒
            socket.setSoTimeout(1000);
            return true;
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + ip);
        } catch (Exception e) {
            // 端口未开放或者连接超时都会抛出IOException
        }
        return false;
    }
}