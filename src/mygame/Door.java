/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.collision.UnsupportedCollisionException;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.Queue;

/**
 *
 * @author Jakub
 */
public class Door extends Spatial{
    AssetManager am;
    private Node doorNode;
    private AnimChannel doorChannel;
    
    public Door(AssetManager am, float rotation, Vector3f position, Vector3f scale){
        this.am = am;
        
        doorNode = (Node) am.loadModel("Models/door/Door.mesh.j3o"); 
        doorNode.setLocalTranslation(position);
        doorNode.scale(scale.x, scale.y, scale.z);
        Quaternion qua = new Quaternion();
        qua.fromAngleAxis(rotation, new Vector3f(0,1,0));
        doorNode.setLocalRotation(qua);
        
        AnimControl doorAnim = doorNode.getControl(AnimControl.class);
        doorChannel = doorAnim.createChannel();
        doorChannel.setAnim("Closed");
    }

    /**
     * @return the doorNode
     */
    public Node getDoorNode() {
        return doorNode;
    }    

    /**
     * @return the doorChannel
     */
    public String getAnim() {
        return doorChannel.getAnimationName();
    }

    /**
     * @param doorChannel the doorChannel to set
     */
    public void setAnim(String anim) {
        doorChannel.setAnim(anim);
    }

    @Override
    public void updateModelBound() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setModelBound(BoundingVolume modelBound) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getVertexCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getTriangleCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Spatial deepClone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void depthFirstTraversal(SceneGraphVisitor visitor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void breadthFirstTraversal(SceneGraphVisitor visitor, Queue<Spatial> queue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int collideWith(Collidable other, CollisionResults results) throws UnsupportedCollisionException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
