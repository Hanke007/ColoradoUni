package cdb.common.lang;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import cdb.dal.vo.DenseMatrix;
import cdb.dal.vo.Location;
import cdb.dal.vo.SparseMatrix;
import cdb.ml.clustering.Cluster;
import cdb.ml.clustering.Point;

/**
 * The Image utility convert data matrix to different kind of image,
 * such as png, jpg, etc.
 * 
 * @author Chao Chen
 * @version $Id: ImageUtil.java, v 0.1 Jul 22, 2015 7:59:01 PM chench Exp $
 */
public final class ImageWUtil {
    /** Image format: BMP*/
    public final static String BMP_FORMAT  = "bmp";
    /** Image format: PNG*/
    public final static String PNG_FORMMAT = "png";
    /** Image format: JPG*/
    public final static String JPG_FORMMAT = "jpg";

    /**
     * forbidden construction
     */
    private ImageWUtil() {

    }

    /**
     * plot the gray image w.r.t the given data matrix.
     * 
     * @param matrix        given data matrix
     * @param outputFile    the file to show the image
     * @param formatName    the image format, i.e., PNG, JPG, and so on
     */
    public static void plotGrayImage(DenseMatrix matrix, String outputFile, String formatName) {
        //convert to GrayImage
        int height = matrix.getRowNum();
        int width = matrix.getColNum();
        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                double temperature = matrix.getVal(x, y);
                //the greater temperature is, the darker the pixel is
                int rgbVal = (Double.isNaN(temperature) | temperature > 500) ? 0
                    : (int) (temperature / 500.0 * 255.0);
                //with setting r = g = b = rgbVal, then follow the rule:
                //  RGBVal = 0.21 * r + 0.71 * g + 0.07 * b
                //the greater RGBVal, the lighter the pixel is
                //e.g., White is 255, Black is 0
                grayImage.setRGB(y, x, (new Color(rgbVal, rgbVal, rgbVal)).getRGB());
            }
        }

        //save in the disk
        File file = new File(outputFile);
        try {
            ImageIO.write(grayImage, formatName, file);
        } catch (IOException e) {
            ExceptionUtil.caught(e, outputFile + " failed in ImageConvert! ");
        }
    }

    /**
     * plot the gray image w.r.t the given data matrix.
     * 
     * @param matrix        given data matrix
     * @param outputFile    the file to show the image
     * @param formatName    the image format, i.e., PNG, JPG, and so on
     */
    public static void plotGrayImage(SparseMatrix matrix, String outputFile, String formatName) {
        //convert to GrayImage
        int height = matrix.length()[0];
        int width = matrix.length()[1];
        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < height; x++) {
            int[] ys = matrix.getRow(x).indexList();
            if (ys == null) {
                continue;
            }

            for (int y : ys) {
                double temperature = matrix.getValue(x, y);
                //the greater temperature is, the darker the pixel is
                int rgbVal = Double.isNaN(temperature) ? 0 : (int) (temperature / 300.0 * 255.0);
                //with setting r = g = b = rgbVal, then follow the rule:
                //  RGBVal = 0.21 * r + 0.71 * g + 0.07 * b
                //the greater RGBVal, the lighter the pixel is
                //e.g., White is 255, Black is 0
                grayImage.setRGB(y, x, (new Color(rgbVal, rgbVal, rgbVal)).getRGB());
            }
        }

        //save in the disk
        File file = new File(outputFile);
        try {
            ImageIO.write(grayImage, formatName, file);
        } catch (IOException e) {
            ExceptionUtil.caught(e, outputFile + " failed in ImageConvert! ");
        }
    }

    /**
     * plot the gray image w.r.t the given data matrix.
     * 
     * @param matrix        given data matrix
     * @param outputFile    the file to show the image
     * @param formatName    the image format, i.e., PNG, JPG, and so on
     */
    public static void plotRGBImageWithMask(DenseMatrix dmatrix, String outputFile,
                                            SparseMatrix smatrix, String formatName) {
        //  convert to GrayImage
        int height = dmatrix.getRowNum();
        int width = dmatrix.getColNum();
        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                double temperature = dmatrix.getVal(x, y);
                //the greater temperature is, the darker the pixel is
                int rgbVal = (Double.isNaN(temperature) | temperature > 500) ? 0
                    : (int) (temperature / 500.0 * 255.0);
                //with setting r = g = b = rgbVal, then follow the rule:
                //  RGBVal = 0.21 * r + 0.71 * g + 0.07 * b
                //the greater RGBVal, the lighter the pixel is
                //e.g., White is 255, Black is 0
                grayImage.setRGB(y, x, (new Color(rgbVal, rgbVal, rgbVal)).getRGB());
            }
        }

        // anomaly layers
        for (int x = 0; x < height; x++) {
            int[] ys = smatrix.getRow(x).indexList();
            if (ys == null) {
                continue;
            }

            for (int y : ys) {
                //                double temperature = dmatrix.getVal(x, y);
                //the greater temperature is, the darker the pixel is
                //                int rgbVal = Double.isNaN(temperature) | temperature > 300 ? 0
                //                    : (int) (temperature / 300.0 * 255.0);
                //with setting r = g = b = rgbVal, then follow the rule:
                //  RGBVal = 0.21 * r + 0.71 * g + 0.07 * b
                //the greater RGBVal, the lighter the pixel is
                //e.g., White is 255, Black is 0
                grayImage.setRGB(y, x, (new Color(255, 0, 0)).getRGB());
            }
        }

        //save in the disk
        File file = new File(outputFile);
        try {
            ImageIO.write(grayImage, formatName, file);
        } catch (IOException e) {
            ExceptionUtil.caught(e, outputFile + " failed in ImageConvert! ");
        }
    }

    /**
     * plot the gray image w.r.t the given data matrix.
     * 
     * @param matrix        given data matrix
     * @param outputFile    the file to show the image
     * @param formatName    the image format, i.e., PNG, JPG, and so on
     */
    public static void plotImageForMEASURE(DenseMatrix matrix, String outputFile,
                                           String formatName) {
        //convert to GrayImage
        int height = matrix.getRowNum();
        int width = matrix.getColNum();
        BufferedImage grayImage = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                double iceCon = matrix.getVal(x, y);

                int rgbVal = Integer.MAX_VALUE;
                if (Double.isNaN(iceCon)) {
                    rgbVal = Color.WHITE.getRGB();
                } else if (iceCon == 5000) {
                    rgbVal = Color.RED.getRGB();
                } else if (iceCon == 10000) {
                    rgbVal = Color.BLUE.getRGB();
                }

                grayImage.setRGB(x, y, rgbVal);
            }
        }

        //save in the disk
        File file = new File(outputFile);
        try {
            ImageIO.write(grayImage, formatName, file);
        } catch (IOException e) {
            ExceptionUtil.caught(e, outputFile + " failed in ImageConvert! ");
        }
    }

    /**
     * plot the gray image w.r.t the given data matrix.
     * 
     * @param matrix        given data matrix
     * @param outputFile    the file to show the image
     * @param formatName    the image format, i.e., PNG, JPG, and so on
     */
    public static void plotGrayImageWithCenter(SparseMatrix matrix, String outputFile,
                                               String formatName, Cluster[] clusters) {
        //convert to GrayImage
        int height = matrix.length()[0];
        int width = matrix.length()[1];
        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < height; x++) {
            int[] ys = matrix.getRow(x).indexList();
            if (ys == null) {
                continue;
            }

            for (int y : ys) {
                double temperature = matrix.getValue(x, y);
                //the greater temperature is, the darker the pixel is
                int rgbVal = Double.isNaN(temperature) ? 0 : (int) (temperature / 300.0 * 255.0);
                //with setting r = g = b = rgbVal, then follow the rule:
                //  RGBVal = 0.21 * r + 0.71 * g + 0.07 * b
                //the greater RGBVal, the lighter the pixel is
                //e.g., White is 255, Black is 0
                grayImage.setRGB(y, x, (new Color(rgbVal, rgbVal, rgbVal)).getRGB());
            }
        }

        // draw circles
        Graphics2D g = grayImage.createGraphics();
        g.setColor(Color.WHITE);
        for (Cluster cluster : clusters) {
            Point center = cluster.getCenter();
            int radius = (int) cluster.getRadius();
            int row = (int) (center.getValue(1) - radius);
            int col = (int) (center.getValue(0) - radius);
            g.drawOval(row, col, 2 * radius, 2 * radius);
        }

        //save in the disk
        File file = new File(outputFile);
        try {
            ImageIO.write(grayImage, formatName, file);
        } catch (IOException e) {
            ExceptionUtil.caught(e, outputFile + " failed in ImageConvert! ");
        }
    }

    public static void drawRects(String orgnImag, String targtImag, Location[] rects, int width,
                                 int height, String formatName) {
        try {
            BufferedImage grayImage = ImageIO.read(new File(orgnImag));

            // draw rects
            Graphics2D g = grayImage.createGraphics();
            for (Location loc : rects) {
                g.drawRect(loc.y(), loc.x(), width, height);
            }

            ImageIO.write(grayImage, formatName, new File(targtImag));
        } catch (IOException e) {
            ExceptionUtil.caught(e, "File not found: " + orgnImag);
        }
    }

}
