package Components.Window;

import Components.Graphics.AbstractPainter;
import Components.Graphics.StraightPainter;
import Components.Graphics.DottedPainter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

enum Shape {
    LINE,
    TRIANGLE,
    POLYGON
}

public class PaintWindow extends JFrame {
    BufferedImage img;
    BufferedImage shownImg;
    JPanel mainPanel;
    JPanel sidePanel;
    int pixelSize = 20;
    int selectedColor = 0xFFFFFF;
    int originalX,originalY;
    AbstractPainter painter;
    Shape selectedShape = Shape.LINE;
    byte triangleStage;//TODO: use polygonPoints instead and finish up selecting Painter
    List<int[]> polygonPoints = new ArrayList<int[]>();

    public PaintWindow(String title, int width, int height){
        super(title);
        setLayout(new BorderLayout());
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((dim.width-width)/2, (dim.height-height)/2);

        img = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        shownImg = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        mainPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(shownImg,0,0,null);
            }
        };
        painter = new StraightPainter(20);

        mainPanel.setPreferredSize(new Dimension(width,height));
        add(mainPanel,BorderLayout.CENTER);

        mainPanel.requestFocus();
        mainPanel.requestFocusInWindow();
        mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                img.setData(shownImg.getData());
                polygonPoints.add(new int[]{e.getX()/pixelSize,e.getY()/pixelSize});
            }
        });

        mainPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {//projection
                super.mouseMoved(e);

                BufferedImage newImg = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_RGB);
                newImg.setData(img.getData());
                painter.setImg(newImg);

                int x = e.getX()/pixelSize;
                int y = e.getY()/pixelSize;

                if(selectedShape == Shape.TRIANGLE) {
                    switch (triangleStage) {
                        case 0://start
                            triangleStage++;
                            break;
                    }
                }else{//POLYGON

                    for (int i = 0; i < polygonPoints.size(); i++){
                        if (polygonPoints.size() > 2 && i != polygonPoints.size()-1) {
                            painter.Draw(polygonPoints.get(i)[0],polygonPoints.get(i)[1],polygonPoints.get(i+1)[0],polygonPoints.get(i+1)[1],selectedColor);
                            System.out.println("connecting "+i+" with "+(i+1));
                        }else /*if (polygonPoints.size() > 1)*/ {
                            painter.Draw(polygonPoints.get(i)[0],polygonPoints.get(i)[1],polygonPoints.get(0)[0],polygonPoints.get(0)[1],selectedColor);
                        }/*else{
                            painter.Draw(x,y,selectedColor);
                        }*/
                    }
                    shownImg.setData(newImg.getData());
                }

                repaint();
            }
        });

        sidePanel = new JPanel();
        sidePanel.setSize(0,height);
        sidePanel.setLayout(new GridBagLayout());
        GridBagConstraints constraint = new GridBagConstraints();
        constraint.gridwidth = GridBagConstraints.REMAINDER;
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.ipady = 5;

        JButton clearButton = new JButton("Clear");
        sidePanel.add(clearButton,constraint);
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                img = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_RGB);
                shownImg = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_RGB);
                repaint();
                polygonPoints = new ArrayList<int[]>();
            }
        });

        sidePanel.add(new JLabel("Line Type",JLabel.CENTER),constraint);

        JButton straightLineButton = new JButton("Straight Line");
        sidePanel.add(straightLineButton,constraint);
        straightLineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                painter = new StraightPainter(pixelSize);
            }
        });

        JButton dottedLineButton = new JButton("Dotted Line");
        sidePanel.add(dottedLineButton,constraint);
        dottedLineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                painter = new DottedPainter(pixelSize);
            }
        });

        sidePanel.add(new JLabel("Shape",JLabel.CENTER),constraint);

        JButton lineButton = new JButton("Line");
        sidePanel.add(lineButton,constraint);
        lineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedShape = Shape.LINE;
            }
        });

        JButton polygonButton = new JButton("Polygon");
        sidePanel.add(polygonButton,constraint);
        polygonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedShape = Shape.POLYGON;
                polygonPoints = new ArrayList<int[]>();
            }
        });

        JButton triangleButton = new JButton("Triangle");
        sidePanel.add(triangleButton,constraint);
        triangleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedShape = Shape.TRIANGLE;//switch to polygons
                triangleStage = 0;
                polygonPoints = new ArrayList<int[]>();
            }
        });

        sidePanel.add(new JLabel("Color",JLabel.CENTER),constraint);

        JButton whiteButton = new JButton("White");
        whiteButton.setBackground(Color.white);
        sidePanel.add(whiteButton,constraint);
        whiteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedColor = 0xFFFFFF;
            }
        });

        JButton redButton = new JButton("Red");
        redButton.setBackground(new Color(0xFF4444));
        sidePanel.add(redButton,constraint);
        redButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedColor = 0xFF0000;
            }
        });

        JButton greenButton = new JButton("Green");
        greenButton.setBackground(new Color(0x33FF33));
        sidePanel.add(greenButton,constraint);
        greenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedColor = 0x00FF00;
            }
        });

        JButton blueButton = new JButton("Blue");
        blueButton.setBackground(new Color(0x4444FF));
        sidePanel.add(blueButton,constraint);
        blueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedColor = 0x0000FF;
            }
        });

        add(sidePanel,BorderLayout.EAST);

        pack();
        setVisible(true);

    }
}
