package Components.Window;

import Components.Painters.AbstractPainter;
import Components.Painters.WireRenderer;
import Components.Painters.StraightPainter;
import Components.ThreeD.Scene;
import Components.ThreeD.Solid;
import transforms.*;

import javax.annotation.processing.SupportedSourceVersion;
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
    boolean isFocused = false;
    AbstractPainter painter;
    Scene scene = new Scene(new ArrayList<>());
    boolean isPerspective = true;
    int selectedObject = 0;
    JLabel rotLabelZ;
    enum objects {
        Cube,
        Pyramid,
        Coil
    }

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

        JTextArea tutorial = new JTextArea("""     
                        move: WASD + C + SPACE
                        click to toggle camera rotation
                        1st/3rd person view: 1 and 3
                        perspective/orthogonal cam: 2
                        your mouse will flip to the
                        other side if you move it outside""");
        tutorial.setEditable(false);

        JButton objectLabel = new JButton(String.valueOf(objects.values()[selectedObject]));
        JButton larrow = new JButton("<");
        JButton rarrow = new JButton(">");

        JLabel posLabel = new JLabel("POSITION");

        JLabel posLabelX = new JLabel("x:0.0");
        JButton posXPlus = new JButton("+");
        posXPlus.addActionListener(e -> {scene.moveObject(selectedObject,new Vec3D(0.1d,0,0));
            posLabelX.setText("x:"+Math.round(10*scene.getSolid(selectedObject).getPosition().getX())/10d);
        });
        JButton posXMinus = new JButton("-");
        posXMinus.addActionListener(e -> {scene.moveObject(selectedObject,new Vec3D(-0.1d,0,0));
            posLabelX.setText("x:"+Math.round(10*scene.getSolid(selectedObject).getPosition().getX())/10d);
        });
        JPanel posX = new JPanel();
        posX.add(posLabelX); posX.add(posXPlus);posX.add(posXMinus);

        JLabel posLabelY = new JLabel("y:0.0");
        JButton posYPlus = new JButton("+");
        posYPlus.addActionListener(e -> {scene.moveObject(selectedObject,new Vec3D(0,0.1d,0));
            posLabelY.setText("y:"+Math.round(10*scene.getSolid(selectedObject).getPosition().getY())/10d);
        });
        JButton posYMinus = new JButton("-");
        posYMinus.addActionListener(e -> {scene.moveObject(selectedObject,new Vec3D(0,-0.1d,0));
            posLabelY.setText("y:"+Math.round(10*scene.getSolid(selectedObject).getPosition().getY())/10d);
        });
        JPanel posY = new JPanel();
        posY.add(posLabelY); posY.add(posYPlus);posY.add(posYMinus);

        JLabel posLabelZ = new JLabel("z:0.0");
        JButton posZPlus = new JButton("+");
        posZPlus.addActionListener(e -> {scene.moveObject(selectedObject,new Vec3D(0,0,0.1d));
            posLabelZ.setText("z:"+Math.round(10*scene.getSolid(selectedObject).getPosition().getZ())/10d);
        });
        JButton posZMinus = new JButton("-");
        posZMinus.addActionListener(e -> {scene.moveObject(selectedObject,new Vec3D(0,0,-0.1d));
            posLabelZ.setText("z:"+Math.round(10*scene.getSolid(selectedObject).getPosition().getZ())/10d);
        });
        JPanel posZ = new JPanel();
        posZ.add(posLabelZ); posZ.add(posZPlus);posZ.add(posZMinus);

        JLabel rotLabel = new JLabel("ROTATION");

        JLabel rotLabelX = new JLabel("x:0.0");
        JButton rotXPlus = new JButton("+");
        rotXPlus.addActionListener(e -> {scene.rotateObject(selectedObject,Math.toRadians(5),0,0);
            rotLabelX.setText("x:"+Math.round(10*scene.getSolid(selectedObject).getRotation()[0])/10d);
        });
        JButton rotXMinus = new JButton("-");
        rotXMinus.addActionListener(e -> {scene.rotateObject(selectedObject,-Math.toRadians(5),0,0);
            rotLabelX.setText("x:"+Math.round(10*scene.getSolid(selectedObject).getRotation()[0])/10d);
        });
        JPanel rotX = new JPanel();
        rotX.add(rotLabelX); rotX.add(rotXPlus);rotX.add(rotXMinus);

        JLabel rotLabelY = new JLabel("y:0.0");
        JButton rotYPlus = new JButton("+");
        rotYPlus.addActionListener(e -> {scene.rotateObject(selectedObject,0,Math.toRadians(5),0);
            rotLabelY.setText("y:"+Math.round(10*scene.getSolid(selectedObject).getRotation()[1])/10d);
        });
        JButton rotYMinus = new JButton("-");
        rotYMinus.addActionListener(e -> {scene.rotateObject(selectedObject,0,-Math.toRadians(5),0);
            rotLabelY.setText("y:"+Math.round(10*scene.getSolid(selectedObject).getRotation()[1])/10d);
        });
        JPanel rotY = new JPanel();
        rotY.add(rotLabelY); rotY.add(rotYPlus);rotY.add(rotYMinus);

        rotLabelZ = new JLabel("z:0.0");
        JButton rotZPlus = new JButton("+");
        rotZPlus.addActionListener(e -> {scene.rotateObject(selectedObject,0,0,Math.toRadians(5));
            rotLabelZ.setText("z:"+Math.round(10*scene.getSolid(selectedObject).getRotation()[2])/10d);
        });
        JButton rotZMinus = new JButton("-");
        rotZMinus.addActionListener(e -> {scene.rotateObject(selectedObject,0,0,-Math.toRadians(5));
            rotLabelZ.setText("z:"+Math.round(10*scene.getSolid(selectedObject).getRotation()[2])/10d);
        });
        JPanel rotZ = new JPanel();
        rotZ.add(rotLabelZ); rotZ.add(rotZPlus);rotZ.add(rotZMinus);

        JLabel scaleLabel = new JLabel("SIZE");

        JLabel scaleLabelX = new JLabel("x:0.3");
        JButton scaleXPlus = new JButton("+");
        scaleXPlus.addActionListener(e -> {scene.scaleObject(selectedObject,new Vec3D(0.1d,0,0));
            scaleLabelX.setText("x:"+Math.round(10*scene.getSolid(selectedObject).getScale().getX())/10d);
        });
        JButton scaleXMinus = new JButton("-");
        scaleXMinus.addActionListener(e -> {scene.scaleObject(selectedObject,new Vec3D(-0.1d,0,0));
            scaleLabelX.setText("x:"+Math.round(10*scene.getSolid(selectedObject).getScale().getX())/10d);
        });
        JPanel scaleX = new JPanel();
        scaleX.add(scaleLabelX); scaleX.add(scaleXPlus);scaleX.add(scaleXMinus);

        JLabel scaleLabelY = new JLabel("y:0.3");
        JButton scaleYPlus = new JButton("+");
        scaleYPlus.addActionListener(e -> {scene.scaleObject(selectedObject,new Vec3D(0,0.1d,0));
            scaleLabelY.setText("y:"+Math.round(10*scene.getSolid(selectedObject).getScale().getY())/10d);
        });
        JButton scaleYMinus = new JButton("-");
        scaleYMinus.addActionListener(e -> {scene.scaleObject(selectedObject,new Vec3D(0,-0.1d,0));
            scaleLabelY.setText("y:"+Math.round(10*scene.getSolid(selectedObject).getScale().getY())/10d);
        });
        JPanel scaleY = new JPanel();
        scaleY.add(scaleLabelY); scaleY.add(scaleYPlus);scaleY.add(scaleYMinus);

        JLabel scaleLabelZ = new JLabel("z:0.3");
        JButton scaleZPlus = new JButton("+");
        scaleZPlus.addActionListener(e -> {scene.scaleObject(selectedObject,new Vec3D(0,0,0.1d));
            scaleLabelZ.setText("z:"+Math.round(10*scene.getSolid(selectedObject).getScale().getZ())/10d);
        });
        JButton scaleZMinus = new JButton("-");
        scaleZMinus.addActionListener(e -> {scene.scaleObject(selectedObject,new Vec3D(0,0,-0.1d));
            scaleLabelZ.setText("z:"+Math.round(10*scene.getSolid(selectedObject).getScale().getZ())/10d);
        });
        JPanel scaleZ = new JPanel();
        scaleZ.add(scaleLabelZ); scaleZ.add(scaleZPlus);scaleZ.add(scaleZMinus);

        JCheckBox checkBox = new JCheckBox("animate");
        checkBox.addActionListener(e -> {
            scene.toggleSpin(selectedObject);
        });

        larrow.addActionListener(e -> {
            selectedObject-=1;
            if (selectedObject<0) {
                selectedObject = objects.values().length-1;
            }
            objectLabel.setText(String.valueOf(objects.values()[selectedObject]));
            posLabelX.setText("x:"+Math.round(10*scene.getSolid(selectedObject).getPosition().getX())/10d);
            posLabelY.setText("y:"+Math.round(10*scene.getSolid(selectedObject).getPosition().getY())/10d);
            posLabelZ.setText("z:"+Math.round(10*scene.getSolid(selectedObject).getPosition().getZ())/10d);
            rotLabelX.setText("x:"+Math.round(10*scene.getSolid(selectedObject).getRotation()[0])/10d);
            rotLabelY.setText("y:"+Math.round(10*scene.getSolid(selectedObject).getRotation()[1])/10d);
            rotLabelZ.setText("z:"+Math.round(10*scene.getSolid(selectedObject).getRotation()[2])/10d);
            scaleLabelX.setText("x:"+Math.round(10*scene.getSolid(selectedObject).getScale().getX())/10d);
            scaleLabelY.setText("y:"+Math.round(10*scene.getSolid(selectedObject).getScale().getY())/10d);
            scaleLabelZ.setText("z:"+Math.round(10*scene.getSolid(selectedObject).getScale().getZ())/10d);
            checkBox.setSelected(scene.getSolid(selectedObject).isSpinning);
        });
        rarrow.addActionListener(e -> {
            selectedObject+=1;
            if (selectedObject>objects.values().length-1) {
                selectedObject = 0;
            }
            objectLabel.setText(String.valueOf(objects.values()[selectedObject]));
            posLabelX.setText("x:"+Math.round(10*scene.getSolid(selectedObject).getPosition().getX())/10d);
            posLabelY.setText("y:"+Math.round(10*scene.getSolid(selectedObject).getPosition().getY())/10d);
            posLabelZ.setText("z:"+Math.round(10*scene.getSolid(selectedObject).getPosition().getZ())/10d);
            rotLabelX.setText("x:"+Math.round(10*scene.getSolid(selectedObject).getRotation()[0])/10d);
            rotLabelY.setText("y:"+Math.round(10*scene.getSolid(selectedObject).getRotation()[1])/10d);
            rotLabelZ.setText("z:"+Math.round(10*scene.getSolid(selectedObject).getRotation()[2])/10d);
            scaleLabelX.setText("x:"+Math.round(10*scene.getSolid(selectedObject).getScale().getX())/10d);
            scaleLabelY.setText("y:"+Math.round(10*scene.getSolid(selectedObject).getScale().getY())/10d);
            scaleLabelZ.setText("z:"+Math.round(10*scene.getSolid(selectedObject).getScale().getZ())/10d);
            checkBox.setSelected(scene.getSolid(selectedObject).isSpinning);
        });

        JPanel sidePanel = new JPanel();
        JPanel selection = new JPanel();
        JPanel position = new JPanel();
        position.setLayout(new BoxLayout(position,BoxLayout.Y_AXIS));

        position.add(posLabel);
        position.add(posX,BorderLayout.WEST);
        position.add(posY,BorderLayout.WEST);
        position.add(posZ,BorderLayout.WEST);

        position.add(rotLabel);
        position.add(rotX,BorderLayout.WEST);
        position.add(rotY,BorderLayout.WEST);
        position.add(rotZ,BorderLayout.WEST);

        position.add(scaleLabel);
        position.add(scaleX,BorderLayout.WEST);
        position.add(scaleY,BorderLayout.WEST);
        position.add(scaleZ,BorderLayout.WEST);

        selection.add(larrow,BorderLayout.WEST);
        selection.add(objectLabel,BorderLayout.WEST);
        selection.add(rarrow,BorderLayout.WEST);
        sidePanel.setPreferredSize(new Dimension(200,height));
        sidePanel.add(selection,BorderLayout.NORTH);
        sidePanel.add(position,BorderLayout.NORTH);
        sidePanel.add(checkBox);
        sidePanel.add(tutorial);
        add(sidePanel,BorderLayout.EAST);

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
                    case '2' -> isPerspective = !isPerspective;
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
                if (e.getX()>width){return;}
                isFocused = !isFocused;
                if (isFocused){
                    remove(sidePanel);
                    pack();
                }else{
                    add(sidePanel,BorderLayout.EAST);
                    pack();
                }
                requestFocus();
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

        //cube
        scene.scaleObject(0,new Vec3D(-0.7d));
        //pyramid
        scene.moveObject(1,new Vec3D(1,0,0));
        //coil
        scene.moveObject(2,new Vec3D(0,1,0));
        scene.rotateObject(2,0,Math.toRadians(45),-Math.toRadians(45));

        pack();
        setVisible(true);
        Timer timer = new Timer(10, e -> Render());
        timer.start();
    }
    private void Render(){
        camera = camera.left(camMovement[0]);
        camera = camera.forward(camMovement[1]);
        camera = camera.up(camMovement[2]);

        if (scene.getSolid(selectedObject).isSpinning){
            rotLabelZ.setText("z:"+Math.round(10*scene.getSolid(selectedObject).getRotation()[2])/10d);
        }

        img = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_RGB);

        Mat4 proj = null;
        if (isPerspective){
            proj = new Mat4PerspRH(//vlastnost kamery, lze predem nasobit
                    Math.toRadians(60),//also for rotation?
                    img.getHeight()/(double)img.getWidth(),
                    0.1,
                    100
            );}else{
            proj = new Mat4OrthoRH(
                    Math.toRadians(60),//also for rotation?
                    img.getHeight()/(double)img.getWidth(),
                    0.1,
                    100
            );
        }


        AbstractPainter painter = new StraightPainter(pixelSize);
        painter.setImg(img);

        WireRenderer wireRenderer = new WireRenderer(painter,camera.getViewMatrix().mul(proj));
        wireRenderer.Draw(scene.getSolids());
        repaint();
    }
}
