/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imageeditapplet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JOptionPane;

/**
 *
 * @author mich4570
 */
public class ImageEditApplet extends JApplet implements MouseListener, MouseMotionListener, KeyListener {
    protected String imageFile = "C:/Users/mich4570/Desktop/woody.jpg";
    protected Integer cropWidth = 327;
    protected Integer cropHeight = 477;
    protected Dimension dim;
    protected BufferedImage image;
    protected BufferedImage offscreen;
    protected BufferedImage croppedImage;
    protected BufferedImage scaledImage;
    protected Graphics2D bufferGraphics; 
    protected Rectangle cropRectangle;
    protected Double cropRectangleScale = 1.0;
    protected Boolean cropFreeze = false;
    
    @Override
    public void init() {
        /*
        imageFile = getParameter("image-file");
        cropWidth = Integer.parseInt(getParameter("crop-width"));
        cropHeight = Integer.parseInt(getParameter("crop-height"));
        */
        
        System.out.println("IMAGE:  " + imageFile);
        System.out.println("CROP-W: " + cropWidth);
        System.out.println("CROP-H: " + cropHeight);
        
        cropRectangle = new Rectangle(0, 0, cropWidth, cropHeight);
        
        try {
            image = ImageIO.read(new File(imageFile));
        } catch (IOException ex) {
            Logger.getLogger(ImageEditApplet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        resize(image.getWidth(), image.getHeight());
        
        dim = new Dimension(image.getWidth(), image.getHeight());
        scaledImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        offscreen = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        bufferGraphics = offscreen.createGraphics();
        
        addMouseListener( this );
        addMouseMotionListener( this );
        addKeyListener(this);
    }
    
    @Override
    public void paint(Graphics g) {
        // Draw the image we are editting to buffer
        bufferGraphics.drawImage(image, 0, 0, null);
        
        // Draw the cropping square to buffer
        bufferGraphics.setColor(Color.red);
        bufferGraphics.drawRect((int)(cropRectangle.x),  (int)(cropRectangle.y), (int)(cropRectangle.width * cropRectangleScale),  (int)(cropRectangle.height * cropRectangleScale));
        
        // Draw the buffer
        g.drawImage(offscreen, 0, 0, null);
    }
    
    // Receive outside data
    public void receiveData(String data) {
        if (data.equals("save")) {
            this.saveImage();
        } else if (data.equals("close")) {
        }
    }
    
    public void saveImage() {
        // Get cropped image
        croppedImage = image.getSubimage((int)(cropRectangle.x),  (int)(cropRectangle.y), (int)(cropRectangle.width * cropRectangleScale),  (int)(cropRectangle.height * cropRectangleScale));

        // Scale cropped image back down to original size.
        scaledImage.getGraphics().drawImage(croppedImage, 0, 0, cropWidth, cropHeight, null);

        // Write cropped image
        try {
            ImageIO.write(scaledImage, "png", new File("output.png"));
        } catch (IOException ex) {
            Logger.getLogger(ImageEditApplet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        cropRectangle.setLocation(e.getX(), e.getY());
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        cropFreeze = false;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        cropFreeze = true;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        cropRectangle.setLocation(e.getX(), e.getY());
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case 10:
                // Save image
                this.saveImage();
                JOptionPane.showMessageDialog(this, "Cropped image saved!");
                break;
            case 61:
                // Zoom in
                cropRectangleScale += 0.05;
                repaint();
                break;
            case 45:
                // Zoom out
                cropRectangleScale -= 0.05;
                repaint();
                break;
            default:
                System.out.println("CODE: " + e.getKeyCode());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
