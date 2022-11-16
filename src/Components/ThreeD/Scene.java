package Components.ThreeD;

import transforms.Mat4;

import java.util.List;

public class Scene {
    List<Solid> solids;
    public Scene(List<Solid> solids){
        this.solids = solids;
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
    public List<Solid> getSolids(){
        return solids;
    }
    public void changeModel(int index,Mat4 mat){
        solids.get(index).setModel(mat);
    }
}
