package Components.Painters;

public class DottedPainter extends AbstractPainter {
    public DottedPainter(int pixelSize) {
        super(pixelSize);
    }

    /*trivial algorithm
     * Pros:
     * - easy to implement
     * - simple to understand and debug
     * Cons:
     * - inaccurate
     * - inconsistent
     */

    @Override
    public void Draw(int x1, int y1, int x2, int y2, int color) {
        super.Draw(x1, y1, x2, y2, color);
        if(x1==x2&&y1==y2){return;}
        boolean skip = false;

        float k = ((float)y2-y1)/((float)x2-x1);
        float q = y1-k*x1;

        if(k<1&&k>=-1){
            if (x1>x2){
                int tmpX = x2; x2 = x1; x1 = tmpX;
            }
            for (int x = x1; x<=x2;x++){
                if(skip){skip = false; continue;}
                int y = Math.round(k*x+q);
                DrawPixel(x,y,color);
                skip = true;
            }
        }else{
            if (y1>y2){
                int tmpY = y2; y2 = y1; y1 = tmpY;
            }
            for (int y = y1; y<=y2;y++){
                if(skip){skip = false; continue;}
                int x = Math.round((y-q)/k);
                if (Float.isInfinite(k)){x = x1;}
                DrawPixel(x,y,color);
                skip = true;
            }
        }
    }
}
