package Components.Painters;

import Components.ThreeD.Solid;
import transforms.Mat4;
import transforms.Point3D;
import transforms.Vec3D;

import java.util.List;

public class WireRenderer {
    AbstractPainter painter;
    Mat4 projectionMat;
    public WireRenderer(AbstractPainter painter, Mat4 mat) {
        this.painter = painter;
        projectionMat = mat;
    }
    public void Draw(Solid s) {
        for (int[] index :
                s.getIndexBuffer()) {
            //System.out.println(s.getModel());
            paint(s.getVertex(index[0]),s.getVertex(index[1]),s.getColor(),s.getModel());
        }
    }
    public void Draw(List<Solid> slist){
        for (Solid object :
                slist) {
            Draw(object);
        }
    }
    private void paint(Point3D p1, Point3D p2, int color, Mat4 mat){
        mat = mat.mul(projectionMat);
        p1=p1.mul(mat);
        p2=p2.mul(mat);

        if (p1.getW()<0.01d||p2.getW()<0.01d){return;}//this works for me so far, >1 is rare and doesn't cause any problems
        //if (-p1.getW()>= p1.getX()>= p1.getW()){dont return}//same for y, z and p2
        p1 = p1.mul(1/p1.getW());
        p2 = p2.mul(1/p2.getW());
        double x1 = p1.getX(),x2 = p2.getX();
        double y1 = p1.getY(),y2 = p2.getY();
        x1 = (x1+1)*painter.img.getWidth()/2/painter.pixelSize;x2 = (x2+1)*painter.img.getWidth()/2/painter.pixelSize;
        y1 = (y1+1)*painter.img.getHeight()/2/painter.pixelSize;y2 = (y2+1)*painter.img.getHeight()/2/painter.pixelSize;
        painter.Draw((int)Math.round(x1),(int)Math.round(y1),(int)Math.round(x2),(int)Math.round(y2),color);
    }
}
