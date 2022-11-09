package Components.Painters;

import Components.ThreeD.Solid;
import transforms.Point3D;

public class SolidPainter{
    AbstractPainter painter;
    public SolidPainter(AbstractPainter painter) {
        this.painter = painter;
    }
    public void Draw(Solid s, int color) {
        for (int[] index :
                s.getIndexBuffer()) {
            paint(s.getVertex(index[0]),s.getVertex(index[1]),color);
        }
    }
    private void paint(Point3D p1, Point3D p2,int color){
        int x1 = (int)p1.getX(),x2 = (int)p2.getX();
        int y1 = (int)p1.getY(),y2 = (int)p2.getY();
        painter.Draw(x1,y1,x2,y2,color);
    }
}
