package cdb.dataset.gen.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * 
 * @author Chao Chen
 * @version $Id: ImagePanel.java, v 0.1 Oct 15, 2015 1:04:25 PM chench Exp $
 */
public class ImagePanel extends JPanel {
    /**  */
    private static final long serialVersionUID = 1L;
    private BufferedImage     image;

    public ImagePanel() {

    }

    public ImagePanel(String fileImg, int row, int col) {
        try {
            image = ImageIO.read(new FileInputStream(fileImg));
            Graphics2D g = image.createGraphics();
            g.setColor(Color.BLUE);
            g.drawOval(col - 25, row - 25, 50, 50);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }

    public void setImage(String fileImg, int row, int col) {
        try {
            image = ImageIO.read(new FileInputStream(fileImg));
            Graphics2D g = image.createGraphics();
            g.setColor(Color.BLUE);
            g.drawOval(col - 25, row - 25, 50, 50);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
