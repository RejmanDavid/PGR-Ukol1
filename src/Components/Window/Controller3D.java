package Components.Window;

import Components.Painters.AbstractPainter;
import Components.Painters.WireRenderer;
import Components.Painters.StraightPainter;
import Components.ThreeD.Scene;
import Components.ThreeD.Solid;
import transforms.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Controller3D extends JFrame {
    BufferedImage img;
    JPanel mainPanel;
    int pixelSize = 5;
    Point mouse;
    boolean isFocused = true;
    AbstractPainter painter;
    Scene scene = new Scene(new ArrayList<>());
    Camera camera = new Camera(
            new Vec3D(-0.5,-0.5,0),//Xforward,Yleft,Zup
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

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                switch (e.getKeyChar()) {
                    case 'a' -> camera = camera.left(0.1);
                    case 's' -> camera = camera.forward(-0.1);
                    case 'd' -> camera = camera.left(-0.1);
                    case 'w' -> camera = camera.forward(0.1);
                }
                Render();
                //System.out.println(camera.getPosition());
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                isFocused = !isFocused;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                if (!isFocused){return;}
                try {
                    Robot robot = new Robot();
                    Point mousePosition = MouseInfo.getPointerInfo().getLocation();
                    if (mousePosition.x-10<getX()){
                        robot.mouseMove(getX()+width-1,mousePosition.y);
                    }
                    else if (mousePosition.x+10>getX()+width){
                        robot.mouseMove(getX()+1,mousePosition.y);
                    }//X
                    if (mousePosition.y-50<getY()){
                        robot.mouseMove(mousePosition.x,getY()+height+25);
                    }
                    else if (mousePosition.y+10>getY()+height){
                        robot.mouseMove(mousePosition.x,getY()+35);
                    }//Y
                    mouse = null;
                } catch (AWTException ex) {
                    ex.printStackTrace();
                }
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                if (!isFocused){return;}
                if (mouse == null) {mouse = new Point(getMousePosition().x,getMousePosition().y);}
                int deX = e.getX() - mouse.x;
                int deY = e.getY() - mouse.y;
                camera = camera.addAzimuth(-Math.toRadians(deX/10d));
                camera = camera.addZenith(Math.toRadians(deY/10d));
                //System.out.println(camera.getAzimuth());
                Render();
                mouse = new Point(e.getX(),e.getY());
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
        Render();
        pack();
        setVisible(true);
    }
    private void Render(){
        img = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_RGB);
        Mat4 mat = new Mat4Transl(new Vec3D(0,0,0));//move by screen halves
        //Mat4Scale unstrech = new Mat4Scale((double)img.getHeight()/img.getWidth(),1,1);//remove stretch
        //mat = mat.mul(unstrech);//already unstretched
        Mat4Scale scale = new Mat4Scale(1,1,1);//scale from centre of screen
        mat = mat.mul(scale);

        Mat4PerspRH proj = new Mat4PerspRH(
                Math.toRadians(60),//also for rotation?
                img.getHeight()/(double)img.getWidth(),
                0.1,
                100
        );
        /*Mat4OrthoRH proj = new Mat4OrthoRH(
                Math.toRadians(60),//also for rotation?
                img.getHeight()/(double)img.getWidth(),
                0.1,
                100
        );*/
        mat = mat.mul(camera.getViewMatrix());
        mat = mat.mul(proj);

        for (int i = 0; i < scene.getSolids().size();i++){
            scene.changeModel(i,mat);
        }
        AbstractPainter painter = new StraightPainter(pixelSize);
        painter.setImg(img);

        WireRenderer wireRenderer = new WireRenderer(painter);
        wireRenderer.Draw(scene.getSolids());
        repaint();
    }
}
