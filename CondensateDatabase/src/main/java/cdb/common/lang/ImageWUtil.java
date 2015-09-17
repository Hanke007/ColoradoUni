package cdb.common.lang;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import cdb.dal.vo.DenseMatrix;

/**
 * The Image utility convert data matrix to different kind of image,
 * such as png, jpg, etc.
 * 
 * @author Chao Chen
 * @version $Id: ImageUtil.java, v 0.1 Jul 22, 2015 7:59:01 PM chench Exp $
 */
public final class ImageWUtil {
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
        BufferedImage grayImage = new BufferedImage(height, width, BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                double temperature = matrix.getVal(x, y);
                //the greater temperature is, the darker the pixel is
                int rgbVal = 255 - (int) (temperature / 6500.0 * 255.0);
                //with setting r = g = b = rgbVal, then follow the rule:
                //  RGBVal = 0.21 * r + 0.71 * g + 0.07 * b
                //the greater RGBVal, the lighter the pixel is
                //e.g., White is 255, Black is 0
                grayImage.setRGB(x, y, (new Color(rgbVal, rgbVal, rgbVal)).getRGB());
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
    public static void plotImageForMEASURE(DenseMatrix matrix, String outputFile, String formatName) {
        //convert to GrayImage
        int height = matrix.getRowNum();
        int width = matrix.getColNum();
        BufferedImage grayImage = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                double iceCon = matrix.getVal(x, y);

                int rgbVal = Integer.MAX_VALUE;
                if (iceCon == -99 | iceCon >= 90 | iceCon == Double.NaN) {
                    rgbVal = Color.WHITE.getRGB();
                } else if (iceCon == 50) {
                    rgbVal = Color.RED.getRGB();
                } else if (iceCon == 51) {
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
}
