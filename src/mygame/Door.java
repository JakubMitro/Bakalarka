/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.asset.AssetManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author Jakub
 */
public class Door{
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
}
