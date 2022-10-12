package Components.Painters;

import java.awt.image.BufferedImage;

public class StraightPainter extends AbstractPainter {
    public StraightPainter(int pixelSize) {
        super(pixelSize);
    }

    @Override
    public void Draw(int x1, int y1, int x2, int y2, int color) {
        super.Draw(x1, y1, x2, y2, color);

        if((x2-x1)>(y2-y1)){
            if((x1-x2)<(y2-y1)){
                for (int i = x1; i <= x2; i++){
                    double dist = ((double)i-x1)/(x2-(double)x1);
                    int addition = (int)((y2-y1)*dist);
                    DrawPixel(i,y1+addition,color);
                    //System.out.println(i+" "+dist);
                }
            }else{
                for (int i = y1; i > y2; i--){
                    double dist = ((double)i-y1)/(y2-(double)y1);
                    int addition = (int)((x2-x1)*dist);
                    DrawPixel(x1+addition,i,color);
                }
            }
        }else{
            int tempx = x1; int tempy = y1;
            x1 = x2; y1 = y2;
            x2 = tempx; y2 = tempy;

            if((x1-x2)<(y2-y1)){
                for (int i = x1; i <= x2; i++){
                    double dist = ((double)i-x1)/(x2-(double)x1);
                    int addition = (int)((y2-y1)*dist);
                    DrawPixel(i,y1+addition,color);
                    //System.out.println(i+" "+dist);
                }
            }else{
                for (int i = y1; i >= y2; i--){
                    double dist = ((double)i-y1)/(y2-(double)y1);
                    int addition = (int)((x2-x1)*dist);
                    DrawPixel(x1+addition,i,color);
                }
            }
        }
    }
}
