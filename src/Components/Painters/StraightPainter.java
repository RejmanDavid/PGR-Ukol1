package Components.Painters;

import java.awt.image.BufferedImage;

public class StraightPainter extends AbstractPainter {
    public StraightPainter(int pixelSize) {
        super(pixelSize);
    }

    @Override
    public void Draw(int x1, int y1, int x2, int y2, int color) {
        super.Draw(x1, y1, x2, y2, color);

        float k = ((float)y2-y1)/((float)x2-x1);
        System.out.println(k);
        float q = y1-k*x1;

        if(k<1&&k>=-1){
            if (x1>x2){
                int tmpX = x2; x2 = x1; x1 = tmpX;
            }
            for (int x = x1; x<=x2;x++){
                int y = Math.round(k*x+q);
                DrawPixel(x,y,color);
            }
        }else{
            if (y1>y2){
                int tmpY = y2; y2 = y1; y1 = tmpY;
            }
            for (int y = y1; y<=y2;y++){
                int x = Math.round((y-q)/k);
                if (Float.isInfinite(k)){x = x1;}
                DrawPixel(x,y,color);
            }
        }
    }
}
