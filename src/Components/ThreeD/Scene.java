package Components.ThreeD;

import transforms.Mat4;
import transforms.Vec3D;

import javax.swing.*;
import java.util.List;

public class Scene {
    List<Solid> solids;
    public Scene(List<Solid> solids){
        this.solids = solids;
        Timer timer = new Timer(10, e -> animate());
        timer.start();
    }
    public void addObject(Solid solid){
        solids.add(solid);
    }
    public void removeObject(Solid solid){
        solids.remove(solid);
    }
    public void removeObject(int index){
        solids.remove(index);
    }
    public Solid getSolid(int index){return solids.get(index);}
    public List<Solid> getSolids(){
        return solids;
    }
    public void toggleSpin(int index){
        solids.get(index).toggleSpinning();
    }
    public void moveObject(int index, Vec3D vector){
        Vec3D newVector = solids.get(index).getPosition();
        newVector = new Vec3D(newVector.getX()+vector.getX(),newVector.getY()+vector.getY(),newVector.getZ()+vector.getZ());
        solids.get(index).setPosition(newVector);
    }
    public void rotateObject(int index,double x,double y,double z){
        double[] rotations = solids.get(index).getRotation();
        solids.get(index).setRotation(new double[]{x + rotations[0], y + rotations[1], z + rotations[2]});
    }
    public void scaleObject(int index,Vec3D vector){
        Vec3D newVector = solids.get(index).getScale();
        newVector = new Vec3D(newVector.getX()+vector.getX(),newVector.getY()+vector.getY(),newVector.getZ()+vector.getZ());
        solids.get(index).setScale(newVector);
    }
    public void resetObject(int index){
        solids.get(index).reset();
    }
    private void animate(){
        for (Solid s :
                solids) {
            if (s.isSpinning) {
                double[] rotations = s.getRotation();
                rotations = new double[]{rotations[0],rotations[1],rotations[2]+Math.toRadians(0.5d)};
                s.setRotation(rotations);
            }
        }
    }
}
