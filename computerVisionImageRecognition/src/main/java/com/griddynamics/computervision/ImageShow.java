package com.griddynamics.computervision;


import org.opencv.core.Mat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by npakhomova on 4/13/16.
 */
public class ImageShow {

    public static void imshow(Mat mat, String name) {
        displayImage(toBufferedImage(mat), null, null, name, null, false);
    }

    public static void imshow(Mat img, Mat img2, Mat explanation, String name, boolean isHorizontal) {
        displayImage(toBufferedImage(img), toBufferedImage(img2), toBufferedImage(explanation), name, null, isHorizontal);
    }

    public static void imshow(Mat mat, Mat secMat, String name) {
        displayImage(toBufferedImage(mat), null, toBufferedImage(secMat), name, null, false);
    }

    public static void imshow(Mat mat, String name, List<String> colorNames) {
        displayImage(toBufferedImage(mat), null, null, name, colorNames, false);
    }

    public static void imshow(Mat mat, Mat secMat, String name, List<String> colorNames) {
        displayImage(toBufferedImage(mat), null, toBufferedImage(secMat), name, colorNames, false);
    }

    private static void displayImage(Image img, Image img2, Image explanation, String name, List<String> colorNames, boolean isHorizontal) {
        //BufferedImage img=ImageIO.read(new File("/HelloOpenCV/lena.png"));
        Rectangle rect = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        ImageIcon icon = new ImageIcon(img);
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setLayout(new BorderLayout());
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        lbl.setBorder(BorderFactory.createLineBorder(Color.black));

        if (img2 != null) {
            ImageIcon icon2 = new ImageIcon(img2);
            JLabel lbl2 = new JLabel();
            lbl2.setIcon(icon2);
            lbl2.setBorder(BorderFactory.createLineBorder(Color.black));
            if (isHorizontal) {
                panel.add(lbl, BorderLayout.WEST);
                panel.add(lbl2, BorderLayout.CENTER);
            } else {
                panel.add(lbl, BorderLayout.NORTH);
                panel.add(lbl2, BorderLayout.CENTER);
            }
        } else {
            panel.add(lbl, BorderLayout.NORTH);
        }

        if (explanation != null) {
            ImageIcon icon2 = new ImageIcon(explanation);
            JLabel lbl2 = new JLabel();
            lbl2.setIcon(icon2);
            lbl2.setBorder(BorderFactory.createLineBorder(Color.black));
            panel.add(lbl2, BorderLayout.SOUTH);
        } else {
            frame.setSize(img.getWidth(null) + 2, img.getHeight(null) + 2);
        }
        if (colorNames != null) {
            for (String colorName : colorNames) {
                frame.add(new JLabel(colorName));
            }
        }
        frame.setTitle(name);
        frame.setVisible(true);
        frame.pack();
        frame.setLocation(205, 2);
    }

    public static Image toBufferedImage(Mat m) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }

//    public static void showExpl(Mat explanation, List<ColorAndPercents> colorAndPercents) {
//        Image img = toBufferedImage(explanation);
//        //BufferedImage img=ImageIO.read(new File("/HelloOpenCV/lena.png"));
//
//        ImageIcon icon = new ImageIcon(img);
//
//        JTextArea colorExp = new JTextArea(5, 34);
//
//        colorExp.setMargin(new Insets(5, 5, 5, 5));
//        colorExp.setEditable(false);
//        JScrollPane colorExplScrollPane = new JScrollPane(colorExp);
//        colorExplScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//
//        JFrame frame = new JFrame();
//        frame.setLayout(new FlowLayout());
//        JPanel panel = new JPanel(new BorderLayout());
//
//        JLabel lbl = new JLabel();
//        lbl.setIcon(icon);
//        lbl.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
//        panel.add(lbl, BorderLayout.NORTH);
//        panel.add(colorExplScrollPane, BorderLayout.CENTER);
//        frame.add(panel);
//        frame.pack();
//        frame.setTitle("Color explanation");
//        frame.setResizable(false);
//        frame.setLocation(205, 2);
//        frame.setVisible(true);
//    }
}
