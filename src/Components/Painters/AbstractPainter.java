package Components.Painters;

import java.awt.image.BufferedImage;

public abstract class AbstractPainter {
    //int x1,x2,y1,y2;
    int pixelSize;
    BufferedImage img;

    public AbstractPainter(int pixelSize) {
        this.pixelSize = pixelSize;
    }
    public void setImg(BufferedImage img){
        this.img = img;
    }
    public void Draw(int x1, int y1, int x2, int y2, int color){
        DrawPixel(x1,y1,color);
        DrawPixel(x2,y2,color);
    }
    public void Draw(int x1, int y1, int color){
        DrawPixel(x1,y1,color);
    }
    protected void DrawPixel(int x,int y,int color){
        if (x>=img.getWidth()/pixelSize||x<0||y>=img.getHeight()/pixelSize||y<0){System.out.println("Pixel out of bounds"); return;}
        for (int i = 0; i < pixelSize; i++){
            for (int j = 0; j < pixelSize; j++){
                img.setRGB(x*pixelSize+i,y*pixelSize+j,color);
            }
        }
    }
}
