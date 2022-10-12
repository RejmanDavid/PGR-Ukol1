package Components.Painters;

import java.awt.image.BufferedImage;

public class DottedPainter extends AbstractPainter {
    public DottedPainter(int pixelSize) {
        super(pixelSize);
    }

    @Override
    public void Draw(int x1, int y1, int x2, int y2, int color) {
        super.Draw(x1, y1, x2, y2, color);
        boolean skip = false;//because i skip the original, shouldnt skip its neighbor
        if((x2-x1)>(y2-y1)){
            if((x1-x2)<(y2-y1)){
                for (int i = x1; i <= x2; i++){
                    if(skip){skip = false; continue;}
                    double dist = ((double)i-x1)/(x2-(double)x1);
                    int addition = (int)((y2-y1)*dist);
                    DrawPixel(i,y1+addition,color);
                    skip = true;
                    //System.out.println(i+" "+dist);
                }
            }else{
                for (int i = y1; i > y2; i--){
                    if(skip){skip = false;continue;}
                    double dist = ((double)i-y1)/(y2-(double)y1);
                    int addition = (int)((x2-x1)*dist);
                    DrawPixel(x1+addition,i,color);
                    skip = true;
                }
            }
        }else{
            int tempx = x1; int tempy = y1;
            x1 = x2; y1 = y2;//this doesnt need to be here, im just lazy
            x2 = tempx; y2 = tempy;

            if((x1-x2)<(y2-y1)){
                for (int i = x1; i <= x2; i++){
                    if(skip){skip = false;continue;}
                    double dist = ((double)i-x1)/(x2-(double)x1);
                    int addition = (int)((y2-y1)*dist);
                    DrawPixel(i,y1+addition,color);
                    skip = true;
                    //System.out.println(i+" "+dist);
                }
            }else{
                for (int i = y1; i >= y2; i--){
                    if(skip){skip = false;continue;}
                    double dist = ((double)i-y1)/(y2-(double)y1);
                    int addition = (int)((x2-x1)*dist);
                    DrawPixel(x1+addition,i,color);
                    skip = true;
                }
            }
        }
    }
}
