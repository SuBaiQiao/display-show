package com.caozhaoyu;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Caozhaoyu
 * @date 2025年05月13日 9:10
 */
public class ScreenGeneratorGif {

    public static void main(String[] args) {
        // 录制时长（秒）
        int duration = 3;
        // 每帧间隔时间（毫秒），对应约10帧/秒
        int delay = 100;
        String fileName = "output.gif";

        try {
            List<BufferedImage> frames = captureScreen(duration, delay);
            saveFramesToGif(frames, fileName);
            System.out.println("GIF 已生成！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 屏幕录制为图像帧列表
     */
    private static List<BufferedImage> captureScreen(int duration, int delay) throws AWTException, InterruptedException {
        Robot robot = new Robot();
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        List<BufferedImage> frames = new ArrayList<>();

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < duration * 1000L) {
            BufferedImage screenCapture = robot.createScreenCapture(screenRect);
            frames.add(screenCapture);
            Thread.sleep(delay);
        }
        return frames;
    }

    /**
     * 将图像帧保存为 GIF 文件
     */
    private static void saveFramesToGif(List<BufferedImage> frames, String outputFilePath) throws IOException {
        File outputFile = new File(outputFilePath);

        // 获取 GIF writer
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("gif");
        if (!writers.hasNext()) {
            throw new IllegalStateException("No GIF ImageWriter found");
        }
        ImageWriter writer = writers.next();

        ImageOutputStream ios = ImageIO.createImageOutputStream(outputFile);
        writer.setOutput(ios);

        writer.prepareWriteSequence(null);

        ImageWriteParam param = writer.getDefaultWriteParam();
        for (BufferedImage frame : frames) {
            writer.writeToSequence(new javax.imageio.IIOImage(frame, null, null), param);
        }

        writer.endWriteSequence();
        ios.close();
        writer.dispose();
    }
}
