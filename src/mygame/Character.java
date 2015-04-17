/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jakub
 */
public class Character {
    private Node node;
    private BetterCharacterControl control;
    private AnimChannel animacia;
    private String meno;
    private boolean kraca = true;
    public Character(String meno){
        this.meno = meno;
    }
    
    public void makeNode(String meno){
        node = new Node(meno);
    }
    
    public void makeControl(Vector3f parametre, Vector3f gravitacia){
        setControl(new BetterCharacterControl(parametre.x, parametre.y, parametre.z));
        getControl().setGravity(gravitacia);
        node.addControl(getControl());
    }
    
    public void makeAnimation(String anim){
        AnimControl animControl = node.getControl(AnimControl.class);
        setAnimacia(animControl.createChannel());
        getAnimacia().setAnim(anim);
    } 
    
    public void makeAnimation(String anim, String child){
        Node no = (Node) node.getChild(child);
        AnimControl animControl = no.getControl(AnimControl.class);
        setAnimacia(animControl.createChannel());
        getAnimacia().setAnim(anim);
    }
    
    public void doSomething(){
        Thread thread = new Thread(){
            @Override
            public void run() {
                setKraca(false);
                System.out.println(meno + " som v kolízií a preto by som chcel niečo robiť."
                        + "Budem sa zhovárať 5 sekúnd.");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Character.class.getName()).log(Level.SEVERE, null, ex);
                }
                setKraca(true);
            }
        };
        thread.start();
    }
    

    /**
     * @return the node
     */
    public Node getNode() {
        return node;
    }

    /**
     * @param node the node to set
     */
    public void setNode(Node node) {
        this.node = node;
    }

    /**
     * @return the control
     */
    public BetterCharacterControl getControl() {
        return control;
    }

    /**
     * @param control the control to set
     */
    public void setControl(BetterCharacterControl control) {
        this.control = control;
    }

    /**
     * @return the animacia
     */
    public AnimChannel getAnimacia() {
        return animacia;
    }

    /**
     * @param animacia the animacia to set
     */
    public void setAnimacia(AnimChannel animacia) {
        this.animacia = animacia;
    }

    /**
     * @return the meno
     */
    public String getMeno() {
        return meno;
    }

    /**
     * @param meno the meno to set
     */
    public void setMeno(String meno) {
        this.meno = meno;
    }

    /**
     * @return the kraca
     */
    public boolean isKraca() {
        return kraca;
    }

    /**
     * @param kraca the kraca to set
     */
    public void setKraca(boolean kraca) {
        this.kraca = kraca;
    }
    
    public boolean isNear(Character me, Character who, float distance){
        Vector3f follower = me.getNode().getLocalTranslation();
        Vector3f following = who.getNode().getLocalTranslation();
        if(Math.abs(follower.getX() - following.getX())< distance &&
              Math.abs(follower.getY() - following.getY())< distance &&
                Math.abs(follower.getZ() - following.getZ())< distance)
            return true;
        return false;
    }
}