/*********************
HARE AND HOUND GAME
CLASS IMAGEPANE
DESIGNED BY DONG LI
VERSION 1.0
2014
**********************/	
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
/******** IMAGEPANE ********
Inherit JPanel class, overwrite paintComponent method.
This is a JPanel with the extra method: setBackground.
*/	
public class ImagePane extends JPanel {

    private Image image;
    
    public ImagePane() {

    }

    protected void paintComponent(Graphics g) {//overwrite 
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }
    
    public void setBackground(String src){//set background of the panel
    	
    	 try {
           image = ImageIO.read(new File(src));
       } catch (IOException ex) {
           ex.printStackTrace();
       }
    }
    

//    public static void main(String[] args) {
//        
//    }
}