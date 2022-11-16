package Components.ThreeD;

import transforms.*;

public class Solid {
    Point3D[] vertexBuffer;
    int[][] indexBuffer;
    int color;
    private Mat4 model = new Mat4Identity();
    public Solid(Point3D [] vb, int[][] ib,int color){
        vertexBuffer = vb;
        indexBuffer = ib;
        this.color = color;
    }

    public int[][] getIndexBuffer() {
        return indexBuffer;
    }

    public Point3D getVertex(int index) {
        return vertexBuffer[index];
    }

    public int getColor() {
        return color;
    }

    public Mat4 getModel() {
        return model;
    }

    public void setModel(Mat4 model) {
        this.model = model;
    }
}
