import Components.Painters.AbstractPainter;
import Components.Painters.DottedPainter;
import Components.Painters.WireRenderer;
import Components.Painters.StraightPainter;
import Components.ThreeD.Scene;
import Components.ThreeD.Solid;
import transforms.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
        Scene scene = new Scene(new ArrayList<>());

        scene.addObject(
                new Solid(
                        new Point3D[]{
                                new Point3D(-0.9,-0.6,1),
                                new Point3D(-0.6,-0.6,1),
                                new Point3D(-0.6,-0.9,1),
                                new Point3D(-0.9,-0.9,1)
                        },
                        new int[][]{
                            {0,1},
                            {1,2},
                            {2,3},
                            {3,0}
                        },0x00FFFF
                )
        );
        scene.addObject(
                new Solid(
                        new Point3D[]{
                                new Point3D(0,-0.8,1),
                                new Point3D(0.2,-0.6,1),
                                new Point3D(0.3,0,1),
                                new Point3D(0.5,0.2,1),
                                new Point3D(0.3,0,1)
                        },
                        new int[][]{
                                {0,1},
                                {1,2},
                                {2,3},
                                {3,4},
                                {4,0}
                        },0xFFFF00
                )
        );
        Mat4 mat = new Mat4Transl(new Vec3D(0.1,0.1,0));//move by screen halves
        Mat4Scale scale = new Mat4Scale(0.6);//scale from centre of screen
        //todo combine
        scene.changeModel(0,scale);
        AbstractPainter painter = new StraightPainter(pixelSize);
        painter.setImg(img);

        WireRenderer wireRenderer = new WireRenderer(painter);
        wireRenderer.Draw(scene.getSolids());

        pack();
        setVisible(true);
    }
}
