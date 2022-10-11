package Components.Graphics;

import java.awt.image.BufferedImage;

public abstract class AbstractPainter {
    //int x1,x2,y1,y2;
    int pixelSize;
    BufferedImage img;

    public AbstractPainter(int pixelSize, BufferedImage img) {
        this.pixelSize = pixelSize;
        this.img = img;
    }
    public BufferedImage Draw(int x1, int y1, int x2, int y2, int color){
        DrawPixel(x1,y1,color);
        DrawPixel(x2,y2,color);
        return img;
    }
    public BufferedImage Draw(int x1, int y1, int color){
        DrawPixel(x1,y1,color);
        return img;
    }
    protected void DrawPixel(int x,int y,int color){
        for (int i = 0; i < pixelSize; i++){
            for (int j = 0; j < pixelSize; j++){
                img.setRGB(x*pixelSize+i,y*pixelSize+j,color);
            }
        }
    }
}
