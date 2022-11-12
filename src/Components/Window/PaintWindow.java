package Components.Window;

import Components.Painters.AbstractPainter;
import Components.Painters.StraightPainter;
import Components.Painters.DottedPainter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

enum Shape {
    LINE,
    TRIANGLE,
    POLYGON,
    SEEDFILL,
    SCANLINE,
    CUT
}

public class PaintWindow extends JFrame {
    BufferedImage img;
    BufferedImage shownImg;
    JPanel mainPanel;
    JPanel sidePanel;
    int pixelSize = 20;
    int selectedColor = 0xFFFFFF;
    AbstractPainter painter;
    Shape selectedShape = Shape.LINE;
    List<int[]> polygonPoints = new ArrayList<>();
    List<float[]> permanentPoints = new ArrayList<>();

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
            public void mouseReleased(MouseEvent e) {//confirmation
                super.mouseReleased(e);
                polygonPoints.add(new int[]{e.getX()/pixelSize,e.getY()/pixelSize});
                switch (selectedShape){
                    case LINE:
                        if (polygonPoints.size()==2){
                            polygonPoints.clear();
                            img.setData(shownImg.getData());
                        }
                        break;
                    case TRIANGLE:
                        if (polygonPoints.size()==3){
                            polygonPoints.clear();
                            img.setData(shownImg.getData());
                        }
                        break;
                    case SEEDFILL:
                        Rasterize(e.getX()/pixelSize,e.getY()/pixelSize);
                        break;
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                switch (selectedShape){
                    case SEEDFILL:
                        break;
                    default:
                        Rasterize(e.getX()/pixelSize,e.getY()/pixelSize);
                        break;
                }
            }
        });

        mainPanel.addMouseMotionListener(new MouseAdapter() {//projection
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                Rasterize(e.getX()/pixelSize,e.getY()/pixelSize);
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
        clearButton.addActionListener(e -> {
            img = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_RGB);
            shownImg = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_RGB);
            repaint();
            polygonPoints = new ArrayList<>();
            permanentPoints = new ArrayList<>();
        });

        sidePanel.add(new JLabel("Line Type",JLabel.CENTER),constraint);

        JButton straightLineButton = new JButton("Straight Line");
        sidePanel.add(straightLineButton,constraint);
        straightLineButton.addActionListener(e -> painter = new StraightPainter(pixelSize));

        JButton dottedLineButton = new JButton("Dotted Line");
        sidePanel.add(dottedLineButton,constraint);
        dottedLineButton.addActionListener(e -> painter = new DottedPainter(pixelSize));

        sidePanel.add(new JLabel("Shape / Action",JLabel.CENTER),constraint);

        JButton lineButton = new JButton("Line");
        sidePanel.add(lineButton,constraint);
        lineButton.addActionListener(e -> {
            selectedShape = Shape.LINE;
            polygonPoints = new ArrayList<>();
            img.setData(shownImg.getData());
        });

        JButton polygonButton = new JButton("Polygon");
        sidePanel.add(polygonButton,constraint);
        polygonButton.addActionListener(e -> {
            selectedShape = Shape.POLYGON;
            polygonPoints = new ArrayList<>();
            img.setData(shownImg.getData());
        });

        JButton triangleButton = new JButton("Triangle");
        sidePanel.add(triangleButton,constraint);
        triangleButton.addActionListener(e -> {
            selectedShape = Shape.TRIANGLE;
            polygonPoints = new ArrayList<>();
            img.setData(shownImg.getData());
        });

        JButton fillButton = new JButton("Fill");
        sidePanel.add(fillButton,constraint);
        fillButton.addActionListener(e -> {
            selectedShape = Shape.SEEDFILL;
            polygonPoints = new ArrayList<>();
            img.setData(shownImg.getData());
        });

        JButton scanButton = new JButton("ScanLine Filled");
        sidePanel.add(scanButton,constraint);
        scanButton.addActionListener(e -> {
            selectedShape = Shape.SCANLINE;
            polygonPoints = new ArrayList<>();
            permanentPoints = new ArrayList<>();
            img.setData(shownImg.getData());
        });

        JButton cutButton = new JButton("Cut (ScanLine)");
        sidePanel.add(cutButton,constraint);
        cutButton.addActionListener(e -> {
            selectedShape = Shape.CUT;
            polygonPoints = new ArrayList<>();
            permanentPoints = new ArrayList<>();
            img.setData(shownImg.getData());
        });

        sidePanel.add(new JLabel("Color",JLabel.CENTER),constraint);

        JButton whiteButton = new JButton("White");
        whiteButton.setBackground(Color.white);
        sidePanel.add(whiteButton,constraint);
        whiteButton.addActionListener(e -> selectedColor = 0xFFFFFF);

        JButton redButton = new JButton("Red");
        redButton.setBackground(new Color(0xFF4444));
        sidePanel.add(redButton,constraint);
        redButton.addActionListener(e -> selectedColor = 0xFF0000);

        JButton greenButton = new JButton("Green");
        greenButton.setBackground(new Color(0x33FF33));
        sidePanel.add(greenButton,constraint);
        greenButton.addActionListener(e -> selectedColor = 0x00FF00);

        JButton blueButton = new JButton("Blue");
        blueButton.setBackground(new Color(0x4444FF));
        sidePanel.add(blueButton,constraint);
        blueButton.addActionListener(e -> selectedColor = 0x0000FF);

        add(sidePanel,BorderLayout.EAST);

        pack();
        setVisible(true);
    }
    void Rasterize(int x,int y){
        BufferedImage newImg = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_RGB);
        newImg.setData(img.getData());
        painter.setImg(newImg);

        switch (selectedShape) {
            case TRIANGLE -> {
                if (polygonPoints.size() == 0) {
                    painter.Draw(x, y, selectedColor);
                }
                if (polygonPoints.size() == 1) {
                    painter.Draw(polygonPoints.get(0)[0], polygonPoints.get(0)[1], x, y, selectedColor);
                }
                if (polygonPoints.size() > 1) {
                    painter.Draw(polygonPoints.get(0)[0], polygonPoints.get(0)[1], polygonPoints.get(1)[0], polygonPoints.get(1)[1], selectedColor);

                    int a1 = polygonPoints.get(0)[0], b1 = polygonPoints.get(0)[1];
                    int a2 = polygonPoints.get(1)[0], b2 = polygonPoints.get(1)[1];
                    int[] midPoint = new int[]{(a1 + a2) / 2, (b1 + b2) / 2};
                    float k1 = (((float) b2 - b1) / ((float) a2 - a1));
                    //float q1 = b1 - k1 * a1;
                    float k2 = -1 / k1;
                    float q2 = midPoint[1] - k2 * midPoint[0];
                    float q3 = y - k1 * x;

                    int interX, interY;
                    if (polygonPoints.get(0)[0] == polygonPoints.get(1)[0]) {
                        interX = x;
                        interY = midPoint[1];
                    } else if (polygonPoints.get(0)[1] == polygonPoints.get(1)[1]) {
                        interX = midPoint[0];
                        interY = y;
                    } else {
                        float m1 = (midPoint[0] + 1) * k2 + q2 - midPoint[1];
                        float n1 = -1;
                        float c1 = m1 * midPoint[0] + n1 * midPoint[1];

                        float m2 = (x + 1) * k1 + q3 - y;
                        float n2 = -1;
                        float c2 = m2 * x + n2 * y;

                        float det = m1 * n2 - m2 * n1;
                        interX = Math.round((n2 * c1 - n1 * c2) / det);
                        interY = Math.round((m1 * c2 - m2 * c1) / det);
                    }

                    painter.Draw(Math.round(interX), Math.round(interY), 0x00FF00);
                    painter.Draw(a1, b1, interX, interY, selectedColor);
                    painter.Draw(a2, b2, interX, interY, selectedColor);
                }
            }
            case POLYGON -> {
                for (int i = 0; i < polygonPoints.size(); i++) {
                    if (polygonPoints.size() > 1 && i != polygonPoints.size() - 1) {
                        painter.Draw(polygonPoints.get(i)[0], polygonPoints.get(i)[1], polygonPoints.get(i + 1)[0], polygonPoints.get(i + 1)[1], selectedColor);
                    }
                    painter.Draw(polygonPoints.get(0)[0], polygonPoints.get(0)[1], x, y, selectedColor);
                }
                if (polygonPoints.size() == 0) {
                    painter.Draw(x, y, selectedColor);
                }
                if (polygonPoints.size() > 1) {
                    painter.Draw(polygonPoints.get(polygonPoints.size() - 1)[0], polygonPoints.get(polygonPoints.size() - 1)[1], x, y, selectedColor);
                }
            }
            case SCANLINE -> {
                permanentPoints = new ArrayList<>();
                permanentPoints.add(new float[]{x,y});
                for (int[] cord :
                        polygonPoints) {
                    permanentPoints.add(new float[]{cord[0], cord[1]});
                }

                if(permanentPoints.size()>2){
                    for (int i = 0; i < img.getHeight()/pixelSize;i++){
                        List<Integer>collisions = new ArrayList<>();
                        for (int j = 0; j < permanentPoints.size();j++){
                            float[] p1 = permanentPoints.get(j);
                            float[] p2;
                            if(j+1==permanentPoints.size()){p2 = permanentPoints.get(0);}else{p2 = permanentPoints.get(j+1);}
                            if(p1[1]>p2[1]){float[]p3 = p1; p1 = p2; p2 = p3;}

                            if(p2[1]<p1[1]+1||p1[1]>i||p2[1]<=i){continue;}
                            float k = (p2[1]-p1[1])/(p2[0]-p1[0]);

                            float xcol;
                            if(p1[0]==p2[0]){xcol = p1[0];}else{
                                float q = p1[1]-k*p1[0];
                                xcol = (i-q)/k;
                            }

                            collisions.add(Math.round(xcol));
                        }
                        int xstart;int xend;
                        collisions = collisions.stream().sorted().collect(Collectors.toList());
                        while(collisions.size()>0){
                            xstart = Math.round(collisions.get(0));
                            collisions.remove(0);
                            xend = Math.round(collisions.get(0));
                            collisions.remove(0);
                            painter.Draw(xstart,i,xend,i,selectedColor);
                        }
                    }
                }
                for (int i = 0; i < polygonPoints.size(); i++) {
                    if (polygonPoints.size() > 1 && i != polygonPoints.size() - 1) {
                        painter.Draw(polygonPoints.get(i)[0], polygonPoints.get(i)[1], polygonPoints.get(i + 1)[0], polygonPoints.get(i + 1)[1], selectedColor);
                    }
                    painter.Draw(polygonPoints.get(0)[0], polygonPoints.get(0)[1], x, y, selectedColor);
                }
                if (polygonPoints.size() == 0) {
                    painter.Draw(x, y, selectedColor);
                }
                if (polygonPoints.size() > 1) {
                    painter.Draw(polygonPoints.get(polygonPoints.size() - 1)[0], polygonPoints.get(polygonPoints.size() - 1)[1], x, y, selectedColor);
                }
            }
            case CUT -> {
                permanentPoints = new ArrayList<>();
                permanentPoints.add(new float[]{x,y});
                for (int[] cord :
                        polygonPoints) {
                    permanentPoints.add(new float[]{cord[0], cord[1]});
                }

                if(permanentPoints.size()>2){
                    for (int i = 0; i < img.getHeight()/pixelSize;i++){
                        List<Integer>collisions = new ArrayList<>();
                        for (int j = 0; j < permanentPoints.size();j++){
                            float[] p1 = permanentPoints.get(j);
                            float[] p2;
                            if(j+1==permanentPoints.size()){p2 = permanentPoints.get(0);}else{p2 = permanentPoints.get(j+1);}
                            if(p1[1]>p2[1]){float[]p3 = p1; p1 = p2; p2 = p3;}

                            if(p2[1]<p1[1]+1||p1[1]>i||p2[1]<=i){continue;}

                            float k = (p2[1]-p1[1])/(p2[0]-p1[0]);

                            float xcol;
                            if(p1[0]==p2[0]){xcol = p1[0];}else{
                                float q = p1[1]-k*p1[0];
                                xcol = (i-q)/k;
                            }

                            collisions.add(Math.round(xcol));
                        }
                        int xstart;int xend;
                        collisions.add(0);collisions.add(img.getWidth()/pixelSize);
                        collisions = collisions.stream().sorted().collect(Collectors.toList());
                        while(collisions.size()>0){
                            xstart = Math.round(collisions.get(0));
                            collisions.remove(0);
                            xend = Math.round(collisions.get(0));
                            collisions.remove(0);
                            painter.Draw(xstart,i,xend,i,0x000000);
                        }
                    }
                }
                for (int i = 0; i < polygonPoints.size(); i++) {
                    if (polygonPoints.size() > 1 && i != polygonPoints.size() - 1) {
                        painter.Draw(polygonPoints.get(i)[0], polygonPoints.get(i)[1], polygonPoints.get(i + 1)[0], polygonPoints.get(i + 1)[1], selectedColor);
                    }
                    painter.Draw(polygonPoints.get(0)[0], polygonPoints.get(0)[1], x, y, selectedColor);
                }
                if (polygonPoints.size() == 0) {
                    painter.Draw(x, y, selectedColor);
                }
                if (polygonPoints.size() > 1) {
                    painter.Draw(polygonPoints.get(polygonPoints.size() - 1)[0], polygonPoints.get(polygonPoints.size() - 1)[1], x, y, selectedColor);
                }
            }
            case LINE -> {
                painter.Draw(x, y, selectedColor);
                if (polygonPoints.size() == 1) {
                    painter.Draw(polygonPoints.get(0)[0], polygonPoints.get(0)[1], x, y, selectedColor);
                }
            }
            case SEEDFILL -> {
                Color bgColor = new Color(newImg.getRGB(x*pixelSize, y*pixelSize));
                if(bgColor.equals(new Color(selectedColor))){polygonPoints = new ArrayList<>(); break;}
                seedFill(x,y,newImg,bgColor);
            }
        }
        shownImg.setData(newImg.getData());
        repaint();
    }
    private void seedFill(int x,int y,BufferedImage newImg,Color bgColor){
        if (x<0||y<0||(x+1)*pixelSize>img.getWidth()||(y+1)*pixelSize>img.getHeight()||!new Color(newImg.getRGB(x * pixelSize, y * pixelSize)).equals(bgColor)){
            polygonPoints.remove(0);
            return;
        }
        painter.Draw(x,y,selectedColor);
        polygonPoints.add(new int[]{0});
        polygonPoints.add(new int[]{0});
        polygonPoints.add(new int[]{0});
        polygonPoints.add(new int[]{0});
        seedFill(x+1,y,newImg,bgColor);
        seedFill(x-1,y,newImg,bgColor);
        seedFill(x,y+1,newImg,bgColor);
        seedFill(x,y-1,newImg,bgColor);
        polygonPoints.remove(0);
        if (polygonPoints.size()==0){img.setData(newImg.getData());}
    }
}
