package Components.ThreeD;

import transforms.Point3D;

public class Solid {
    Point3D[] vertexBuffer;
    int[][] indexBuffer;
    public Solid(Point3D [] vb, int[][] ib){
        vertexBuffer = vb;
        indexBuffer = ib;
    }

    public int[][] getIndexBuffer() {
        return indexBuffer;
    }

    public Point3D getVertex(int index) {
        return vertexBuffer[index];
    }
}
