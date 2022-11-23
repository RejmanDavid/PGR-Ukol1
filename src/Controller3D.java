import Components.Painters.AbstractPainter;
import Components.Painters.DottedPainter;
import Components.Painters.WireRenderer;
import Components.Painters.StraightPainter;
import Components.ThreeD.Scene;
import Components.ThreeD.Solid;
import transforms.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Controller3D extends JFrame {
    BufferedImage img;
    JPanel mainPanel;
    int pixelSize = 5;
    AbstractPainter painter;
    Scene scene = new Scene(new ArrayList<>());
    Camera camera = new Camera(
            new Vec3D(-0.38,-0.5,0),//Xforward,Yleft,Zup
            Math.toRadians(45),//azimuth//left
            Math.toRadians(0),//zenith//updown
            1,true
    );

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

        mainPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                System.out.println("b");//todo DOESNT REACT
                switch (e.getKeyChar()){
                    case'a':
                        Move( new int[]{-1/img.getWidth(),1});
                        break;
                    case's':
                        Move( new int[]{1,1/img.getWidth()});
                        System.out.println("s");
                        break;
                    case'd':
                        Move( new int[]{1/img.getWidth(),1});
                        break;
                    case'w':
                        Move( new int[]{1,-1/img.getWidth()});
                        break;
                }
            }
        });

        scene.addObject(
                new Solid(
                        new Point3D[]{
                                new Point3D(-0.2,-0.2,0.2),
                                new Point3D(0.2,-0.2,0.2),
                                new Point3D(0.2,0.2,0.2),
                                new Point3D(-0.2,0.2,0.2),
                                new Point3D(-0.2,-0.2,-0.2),
                                new Point3D(0.2,-0.2,-0.2),
                                new Point3D(0.2,0.2,-0.2),
                                new Point3D(-0.2,0.2,-0.2)
                        },
                        new int[][]{
                                {0,1},
                                {1,2},
                                {2,3},
                                {3,0},
                                {4,5},
                                {5,6},
                                {6,7},
                                {7,4},
                                {0,4},
                                {1,5},
                                {2,6},
                                {3,7}
                        },0x00FFFF
                )
        );
        /*scene.addObject(
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
        );*/
        Mat4 mat = new Mat4Transl(new Vec3D(0,0,0));//move by screen halves
        Mat4Scale unstrech = new Mat4Scale((double)img.getHeight()/img.getWidth(),1,1);//remove stretch
        //mat = mat.mul(unstrech);//already unstretched
        Mat4Scale scale = new Mat4Scale(1,1,1);//scale from centre of screen
        mat = mat.mul(scale);

        Mat4PerspRH proj = new Mat4PerspRH(
                Math.toRadians(60),//also for rotation?
                img.getHeight()/(double)img.getWidth(),
                0.1,
                100
        );
        mat = mat.mul(camera.getViewMatrix());
        mat = mat.mul(proj);

        for (int i = 0; i < scene.getSolids().size();i++){
            scene.changeModel(i,mat);
        }
        AbstractPainter painter = new StraightPainter(pixelSize);
        painter.setImg(img);

        WireRenderer wireRenderer = new WireRenderer(painter);
        wireRenderer.Draw(scene.getSolids());

        pack();
        setVisible(true);
    }
    private void Move(int[] movement){
        for (int i = 0; i < scene.getSolids().size();i++){
            Mat4 mat = scene.getSolids().get(i).getModel();//todo not finished
            new Mat4Transl(new Vec3D(movement[0],movement[1],0));
            scene.changeModel(i,mat);
        }
    }
}
