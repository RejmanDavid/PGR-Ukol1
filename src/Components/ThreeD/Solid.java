package Components.ThreeD;

import transforms.*;

public class Solid {
    Point3D[] vertexBuffer;
    int[][] indexBuffer;
    int color;
    private Mat4 model = null;
    public boolean isSpinning;
    Vec3D position;
    Vec3D scale;
    double xrot,yrot,zrot;
    public Solid(Point3D [] vb, int[][] ib,int color){
        vertexBuffer = vb;
        indexBuffer = ib;
        this.color = color;
        reset();
    }
    public void reset(){
        position = new Vec3D();
        scale = new Vec3D(1);
        xrot = 0;yrot = 0;zrot = 0;
        isSpinning = false;
        updateModel();
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

    public Mat4 getModel() { return model; }
    private void updateModel(){
        Mat4 mat = new Mat4RotXYZ(xrot,yrot,zrot);
        mat = mat.mul(new Mat4Scale(scale));
        model = mat.mul(new Mat4Transl(position));
    }

    public void setPosition(Vec3D position) {
        this.position = position;
        updateModel();
    }

    public void setScale(Vec3D scale) {
        this.scale = scale;
        updateModel();
    }

    public void setRotation(double[] rotation) {
        xrot = rotation[0];
        yrot = rotation[1];
        zrot = rotation[2];
        updateModel();
    }

    public Vec3D getScale() {
        return scale;
    }

    public double[] getRotation() {
        return new double[]{xrot,yrot,zrot};
    }

    public Vec3D getPosition() {
        return position;
    }

    public void toggleSpinning(){
        isSpinning = !isSpinning;
    }
}
