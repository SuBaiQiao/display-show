package com.caozhaoyu;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Caozhaoyu
 */
public class ScreenCaptureServer extends WebSocketServer {
    private final Robot robot;
    private final List<WebSocket> clients;

    public ScreenCaptureServer(int port) throws AWTException {
        super(new InetSocketAddress(port));
        robot = new Robot();
        clients = new ArrayList<>();
    }

    @Override
    public void onStart() {
        // 实现空的 onStart() 方法
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
        clients.add(conn);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
        clients.remove(conn);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {

    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        // 不需要处理二进制消息
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    public void startScreenCapture(int interval) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                Rectangle screenRectangle = new Rectangle(screenSize);
                BufferedImage screenImage = robot.createScreenCapture(screenRectangle);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                try {
                    javax.imageio.ImageIO.write(screenImage, "png", outputStream);
                    broadcast(outputStream.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, interval);
    }

    @Override
    public void broadcast(byte[] data) {
        for (WebSocket conn : clients) {
            conn.send(data);
        }
    }

    public static void main(String[] args) throws AWTException {
        int port = 8887;
        ScreenCaptureServer server = new ScreenCaptureServer(port);
        // 设置捕捉频率，单位：毫秒
        server.startScreenCapture(1);
        server.start();
        System.out.println("WebSocket server started on port " + port);
    }
}
