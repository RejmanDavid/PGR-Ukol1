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
import java.util.Date;

public class Controller3D extends JFrame {
    BufferedImage img;
    JPanel mainPanel;
    int pixelSize = 5;
    Point mouse;
    float[] camMovement = new float[]{0,0,0};
    boolean isFocused = true;
    AbstractPainter painter;
    Scene scene = new Scene(new ArrayList<>());
    Camera camera = new Camera(
            new Vec3D(-0.5,-0.5,0),//Xforward,Yleft,Zup
            //new Vec3D(0,0,0),//for looking from the center
            Math.toRadians(45),//azimuth//left
            Math.toRadians(0),//zenith//updown
            1,true//distance for 3rd person and bool if 1st person
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
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                switch (e.getKeyChar()) {
                    case 'a' -> camMovement[0] = 0f;
                    case 's' -> camMovement[1] = 0f;
                    case 'd' -> camMovement[0] = 0f;
                    case 'w' -> camMovement[1] = 0f;
                    case ' ' -> camMovement[2] = 0f;
                    case 'c' -> camMovement[2] = 0f;
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                switch (e.getKeyChar()) {
                    case 'a' -> camMovement[0] = 0.02f;
                    case 's' -> camMovement[1] = -0.02f;
                    case 'd' -> camMovement[0] = -0.02f;
                    case 'w' -> camMovement[1] = 0.02f;
                    case ' ' -> camMovement[2] = -0.02f;
                    case 'c' -> camMovement[2] = 0.02f;
                    default -> System.out.println(e.getKeyChar());
                }
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
                        robot.mouseMove(mousePosition.x,getY()+height+35);
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
                if (mouse == null) {
                    if(getMousePosition() == null){return;}//alternative to try, because it can somehow fail
                    mouse = new Point(getMousePosition().x,getMousePosition().y);
                }
                int deX = e.getX() - mouse.x;
                int deY = e.getY() - mouse.y;
                camera = camera.addAzimuth(-Math.toRadians(deX/10d));
                camera = camera.addZenith(Math.toRadians(deY/10d));

                if(Math.toDegrees(camera.getAzimuth())>360){camera = camera.addAzimuth(-Math.toRadians(360));}
                else if(Math.toDegrees(camera.getAzimuth())<0){camera = camera.addAzimuth(Math.toRadians(360));}
                //System.out.println(camera.getAzimuth());
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
                                new Point3D(-0.2,-0.2,0),
                                new Point3D(0.2,-0.2,0)
                        },
                        new int[][]{
                                {0,1}
                        },0xFF0000
                )
        );*/
        pack();
        setVisible(true);
        javax.swing.Timer timer = new Timer(10, e -> Render());
        timer.start();
    }
    private void Render(){
        camera = camera.left(camMovement[0]);
        camera = camera.forward(camMovement[1]);
        camera = camera.up(camMovement[2]);

        //System.out.println("a:"+Math.toDegrees(camera.getAzimuth()));

        img = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_RGB);
        Mat4 mat = new Mat4Transl(new Vec3D(0,0,0));//move by screen halves //vlastnost objektu
        //Mat4Scale unstrech = new Mat4Scale((double)img.getHeight()/img.getWidth(),1,1);//remove stretch
        //mat = mat.mul(unstrech);//already unstretched
        Mat4Scale scale = new Mat4Scale(1,1,1);//scale from centre of screen //vlastnost objektu
        mat = mat.mul(scale);

        Mat4PerspRH proj = new Mat4PerspRH(//vlastnost kamery, lze predem nasobit
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
        mat = mat.mul(camera.getViewMatrix());//vlastnost kamery, lze predem nasobit
        mat = mat.mul(proj);//lze obratit nasobeni ale musely by se matice transponovat

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
