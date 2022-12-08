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
    Camera camera1st = new Camera(
            new Vec3D(-0.5,-0.5,0),//Xforward,Yleft,Zup
            Math.toRadians(45),//azimuth//left
            Math.toRadians(0),//zenith//updown
            1,true//distance for 3rd person and bool if 1st person
    );Camera camera3rd = new Camera(
            new Vec3D(0,0,0),//for looking from the center
            Math.toRadians(0),//azimuth//left
            Math.toRadians(0),//zenith//updown
            1,false//distance for 3rd person and bool if 1st person
    );
    Camera camera = camera1st;

    public Controller3D(String title, int width, int height){//todo add controls view
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
                    case '1' -> camera = camera1st;
                    case '3' -> camera = camera3rd;
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
        scene.addObject(//scaled cube
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
        scene.addObject(//movedpyramid
                new Solid(
                        new Point3D[]{
                                new Point3D(0,0,-0.2),//top
                                new Point3D(0.2,0.2,0.2),
                                new Point3D(0.2,-0.2,0.2),
                                new Point3D(-0.2,-0.2,0.2),
                                new Point3D(-0.2,0.2,0.2),
                        },
                        new int[][]{
                                {1,2},
                                {2,3},
                                {3,4},
                                {4,1},
                                {0,1},
                                {0,2},
                                {0,3},
                                {0,4}
                        },0xFFFF00
                )
        );
        java.util.List<Point3D> sinP = new ArrayList<>();
        java.util.List<int[]> sinI = new ArrayList<>();
        sinP.add(new Point3D(0,0,-0.02));
        for (int i = 1; i < 1000; i ++){
            sinP.add(new Point3D(i/1000d,Math.sin(i/10d)*0.02d,Math.sin((i/10d)-Math.PI/2)*0.02d));
            sinI.add(new int[]{i-1,i});
        }
        Point3D[] sinPA = sinP.toArray(new Point3D[sinP.size()]);
        int[][] sinIA = sinI.toArray(new int[sinI.size()][]);
        scene.addObject(//rotated and moved coil
                new Solid(
                        sinPA,
                        sinIA,
                        0xAAFF00
                )
        );
        scene.addObject(//axis-X
                new Solid(
                        new Point3D[]{
                                new Point3D(0,0,0),
                                new Point3D(0.2,0,0),
                                new Point3D(0.2,0.02,0),
                                new Point3D(0.2,-0.02,0),
                                new Point3D(0.24,0,0),
                                new Point3D(0.2,0,0.02),
                                new Point3D(0.2,0,-0.02)
                        },
                        new int[][]{
                                {0,1},
                                {2,3},
                                {3,4},
                                {4,2},
                                {5,6},
                                {6,4},
                                {4,5}
                        },0xFF0000
                )
        );
        scene.addObject(//axis-Y
                new Solid(
                        new Point3D[]{
                                new Point3D(0,0,0),
                                new Point3D(0,0.2,0),
                                new Point3D(0.02,0.2,0),
                                new Point3D(-0.02,0.2,0),
                                new Point3D(0,0.24,0),
                                new Point3D(0,0.2,0.02),
                                new Point3D(0,0.2,-0.02)
                        },
                        new int[][]{
                                {0,1},
                                {2,3},
                                {3,4},
                                {4,2},
                                {5,6},
                                {6,4},
                                {4,5}
                        },0x00FF00
                )
        );
        scene.addObject(//axis-Z
                new Solid(
                        new Point3D[]{
                                new Point3D(0,0,0),
                                new Point3D(0,0,0.2),
                                new Point3D(0.02,0,0.2),
                                new Point3D(-0.02,0,0.2),
                                new Point3D(0,0,0.24),
                                new Point3D(0,0.02,0.2),
                                new Point3D(0,-0.02,0.2)
                        },
                        new int[][]{
                                {0,1},
                                {2,3},
                                {3,4},
                                {4,2},
                                {5,6},
                                {6,4},
                                {4,5}
                        },0x0000FF
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
        Mat4 defaultmat = new Mat4Transl(new Vec3D(0,0,0));//move by screen halves //vlastnost objektu
        Mat4 pyramidmat = new Mat4Transl(new Vec3D(1,0,0));
        Mat4 coilmat = new Mat4RotXYZ(0,Math.toRadians(45),-Math.toRadians(45));
        coilmat = coilmat.mul(new Mat4Transl(new Vec3D(0,1,0)));
        //Mat4Scale scale = new Mat4Scale(1,1,1);//scale from centre of screen //vlastnost objektu
        //defaultmat = defaultmat.mul(scale);
        Mat4 cubemat = defaultmat.mul(new Mat4Scale(0.3));
        //pyramidmat = pyramidmat.mul(scale);
        //coilmat = coilmat.mul(new Mat4Scale(1d));

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
        defaultmat = defaultmat.mul(camera.getViewMatrix());//vlastnost kamery, lze predem nasobit
        defaultmat = defaultmat.mul(proj);//lze obratit nasobeni ale musely by se matice transponovat
        pyramidmat = pyramidmat.mul(camera.getViewMatrix());
        pyramidmat = pyramidmat.mul(proj);
        coilmat = coilmat.mul(camera.getViewMatrix());
        coilmat = coilmat.mul(proj);
        cubemat = cubemat.mul(camera.getViewMatrix());
        cubemat = cubemat.mul(proj);

        for (int i = 0; i < scene.getSolids().size();i++){
            switch (i) {
                //cube
                case 0 -> scene.changeModel(i,cubemat);
                //pyramid
                case 1 -> scene.changeModel(i, pyramidmat);
                //coil
                case 2 -> scene.changeModel(i,coilmat);
            }
        }
        AbstractPainter painter = new StraightPainter(pixelSize);
        painter.setImg(img);

        WireRenderer wireRenderer = new WireRenderer(painter,defaultmat);
        wireRenderer.Draw(scene.getSolids());
        repaint();
    }
}
