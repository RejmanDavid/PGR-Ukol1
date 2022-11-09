import Components.Painters.AbstractPainter;
import Components.Painters.SolidPainter;
import Components.Painters.StraightPainter;
import Components.ThreeD.Solid;
import transforms.Point3D;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Controller3D extends JFrame {
    BufferedImage img;
    JPanel mainPanel;
    int pixelSize = 20;
    AbstractPainter painter;

    public Controller3D(String title, int width, int height){
        super(title);
        setLayout(new BorderLayout());
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((dim.width-width)/2, (dim.height-height)/2);

        img = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        img = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        mainPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img,0,0,null);
            }
        };
        painter = new StraightPainter(pixelSize);

        mainPanel.setPreferredSize(new Dimension(width,height));
        add(mainPanel,BorderLayout.CENTER);

        mainPanel.requestFocus();
        mainPanel.requestFocusInWindow();

        Point3D[] vb = {
                new Point3D(2,8,1),
                new Point3D(8,8,1),
                new Point3D(8,2,1),
                new Point3D(2,2,1)
        };
        int[][] ib = {
                {0,1},
                {1,2},
                {2,3},
                {3,0}
        };
        Solid cube = new Solid(vb,ib);
        StraightPainter sp = new StraightPainter(pixelSize);
        sp.setImg(img);
        SolidPainter snake = new SolidPainter(sp);
        snake.Draw(cube,0x00CCCC);
        pack();
        setVisible(true);
    }
    void Rasterize(int x,int y){
        BufferedImage newImg = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_RGB);
        newImg.setData(img.getData());
        painter.setImg(newImg);

        img.setData(newImg.getData());
        repaint();
    }
}
