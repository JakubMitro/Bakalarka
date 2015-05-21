/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.FlyByCamera;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jakub
 */
public class Character {
    private float hp;
    private Node node;
    private BetterCharacterControl control;
    private AnimChannel animacia;
    private String meno;
    private boolean kraca;
    private boolean alive;
    private boolean busy;

    public Character(String meno) {
        this.meno = meno;
        alive = true;
        kraca = true;
        busy = false;
        hp = 450;
    }
    public float getHp(){
        return hp;
    }
    public void setHp(float hp){
        this.hp = hp;
    }
    public boolean isBusy(){
        return busy;
    }
    public void setBusy(boolean busy){
        this.busy = busy;
    }
    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void makeNode(String meno) {
        node = new Node(meno);

    }

    public void makeControl(Vector3f parametre, Vector3f gravitacia) {
        setControl(new BetterCharacterControl(parametre.x, parametre.y, parametre.z));
        getControl().setGravity(gravitacia);
        node.addControl(getControl());
    }

    public void makeAnimation(String anim) {
        AnimControl animControl = node.getControl(AnimControl.class);
        setAnimChanel(animControl.createChannel());
        getAnimChanel().setAnim(anim);
    }

    public void makeAnimation(String anim, String child) {
        Node no = (Node) node.getChild(child);
        AnimControl animControl = no.getControl(AnimControl.class);
        setAnimChanel(animControl.createChannel());
        getAnimChanel().setAnim(anim);
    }

    public void doSomething() {
        Thread thread = new Thread() {
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
     *
     * @return meno aktualnej animacie
     */
    public String getAnimation() {
        return animacia.getAnimationName();
    }

    /**
     * nastavenie novej animacie
     *
     * @param name nova animacia
     */
    public void setAnimation(String name) {
        animacia.setAnim(name);
    }
    public void setAnimation(String name, float blend) {
        animacia.setAnim(name, blend);
    }

    /**
     * @return the animacia
     */
    public AnimChannel getAnimChanel() {
        return animacia;
    }

    /**
     * @param animacia the animacia to set
     */
    public void setAnimChanel(AnimChannel animacia) {
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

    public boolean compare(Vector3f x, Vector3f y, float dist) {
        return (Math.abs(x.getX() - y.getX()) < dist
                && Math.abs(x.getY() - y.getY()) < dist
                && Math.abs(x.getZ() - y.getZ()) < dist);
    }

    public boolean isNear(Character who, float distance) {
        Vector3f follower = node.getLocalTranslation();//me.getNode().getLocalTranslation();
        Vector3f following = who.getNode().getLocalTranslation();

        return compare(follower, following, distance);
    }

    public boolean nearFire(float distance, List<Spatial> fire) {
        Vector3f me = node.getLocalTranslation();
        for (Spatial f : fire) {
            Vector3f fireLoc = f.getLocalTranslation();
            if (compare(fireLoc, me, distance)) {
                return true;
            }
        }
        return false;
    }

    public void nasleduj(Character player, Float rychlost) {
        Vector3f pozicia = player.getNode().getLocalTranslation().subtract(node.getLocalTranslation());
        control.setWalkDirection(new Vector3f(pozicia.getX()/rychlost, pozicia.getY()/rychlost, pozicia.getZ()/rychlost)); 
        control.setViewDirection(pozicia);
        
        if (isNear(player, 2)) {
            control.setWalkDirection(Vector3f.ZERO);
            animacia.setAnim("Stand");
        } else {
            if (animacia.getAnimationName().equalsIgnoreCase("Stand")) {
                animacia.setAnim("Walk");
            }
        }
    }

    /**
     * pohyb standardoun rychlostou
     */
    public void walking() {
        walking(1);
    }

    /**
     * nahodny pohyb hraca
     *
     * @param rychlost rychlost - 1 standardna, < 1 pomalsia, > 1 rychlejsia
     */
    public void walking(float rychlost) {
        animacia.setSpeed(rychlost);
        int rozsah = 500;
        Random generate = new Random();
        float k = generate.nextInt(rozsah);
        float l = generate.nextInt(rozsah);
        float x = control.getWalkDirection().getX();
        float y = control.getWalkDirection().getY();
        float z = control.getWalkDirection().getZ();
        float pomx = x;
        float pomz = z;

        if (k == 10) {
            x = rychlost;
        } else if (k == 20) {
            x = 0;
        } else if (k == 30) {
            x = -rychlost;
        }
        if (l == 15) {
            z = rychlost;
        } else if (l == 25) {
            z = 0;
        } else if (l == 35) {
            z = -rychlost;
        }
        if ((pomx == rychlost || pomz == rychlost || pomx == -rychlost || pomz == -rychlost) && (x == 0 && z == 0) && (meno.equals("Jozko") || meno.equals("Janko"))) {
            animacia.setAnim("Stand");
        } else if ((pomx == 0 && pomz == 0) && (x == rychlost || z == rychlost || x == -rychlost || z == -rychlost) && (meno.equals("Jozko") || meno.equals("Janko"))) {
            animacia.setAnim("Walk");
        } else if ((pomx == 0 && pomz == 0) && (x == rychlost || z == rychlost || x == -rychlost || z == -rychlost) && meno.equals("Benny")) {
            animacia.setAnim("run");
        } else if ((pomx == rychlost || pomz == rychlost || pomx == -rychlost || pomz == -rychlost) && (x == 0 && z == 0) && meno.equals("Benny")) {
            animacia.setAnim("idle");
        }

        Vector3f position = new Vector3f(x, y, z);
        control.setWalkDirection(position);
        if (!position.equals(new Vector3f(0, y, 0))) {
            control.setViewDirection(position);
        }
    }

    public void runFromFire(float speed) {
        if (!busy) {
            animacia.setAnim("Walk");
            Vector3f dir = control.getWalkDirection();
            dir.addLocal(dir.negate().mult(2f));
            if (dir.equals(Vector3f.ZERO)) {
                dir = new Vector3f(1f, 0f, 0f);
            }
            control.setWalkDirection(dir);
            control.setViewDirection(dir);
            busy = true;
        }
    }

    /**
     * zastavi pohyb postavy
     */
    public void stopWalking() {
        control.setWalkDirection(Vector3f.ZERO);
        animacia.setAnim("Stand");
    }

    /**
     * Metóda, ktorá zabezpečuje, aby postava nespadla zo sveta. Ak postava
     * dôjde na kraj, otočí sa o 180° a pokračuje novým smerom.
     */
    public void stayInWorld() {
        if (node.getLocalTranslation().getX() > 20) {
            control.setWalkDirection(new Vector3f(-1, 0, 0));
            control.setViewDirection(new Vector3f(-1, 0, 0));
        } else if (node.getLocalTranslation().getX() < -20) {
            control.setWalkDirection(new Vector3f(1, 0, 0));
            control.setViewDirection(new Vector3f(1, 0, 0));
        } else if (node.getLocalTranslation().getZ() < -20) {
            control.setWalkDirection(new Vector3f(0, 0, 1));
            control.setViewDirection(new Vector3f(0, 0, 1));
        } else if (node.getLocalTranslation().getZ() > 20) {
            control.setWalkDirection(new Vector3f(0, 0, -1));
            control.setViewDirection(new Vector3f(0, 0, -1));
        }
    }
    
    public void dontWalkCrossWalls()
    {
        float x = node.getLocalTranslation().getX();
        float z = node.getLocalTranslation().getZ();
        System.out.println("X: " + x);
        System.out.println("Z: " + z);
        if(x < 55 && x > 32 && z < -17 && z > -23)
        {
            System.out.println("robim dont");
            control.setWalkDirection(control.getWalkDirection().negate());
            control.setViewDirection(control.getViewDirection().negate());
        }
    }
    
    public boolean checkHP()
    {
        if(hp >= 640)
        {
            node.removeControl(getControl());
            return true;
        }
        return false;
    }
    /**
     * Hlavná funkcia zabezpečujúca kráčanie postáv "a" a "b" po svete. Funkcia
     * volá funkciu pre kolíziu postáv a funkciu zabezpečujúcu nespadnutie
     * postáv z herného sveta. V prípade, že sa postavy "a" a "b" k sebe
     * približia na menej ako 9 jednotiek, je ich smer nastavený tak, aby prišli
     * k sebe a začali konvezáciu. Po konverzácií postavy môžu popri sebe
     * prechádzať, no ďalšiu konverzáciu začnú až ked sa ich vzdialenosť zväčší
     * na 10 jednotiek a následne zmenší pod 9 jednotiek.
     *
     * @param a postava
     * @param b postava
     *
     * public void walkInWorld(Character a, Character b) { Node aNode =
     * a.getNode(); Node bNode = b.getNode(); BetterCharacterControl aControl =
     * a.getControl(); BetterCharacterControl bControl = b.getControl();
     *
     * if (a.isKraca() && b.isKraca()) { if (isKraca()) { walking(a);
     * walking(b); meeting = false; } if
     * (aNode.getWorldBound().distanceTo(bNode.getLocalTranslation()) > 10) {
     * walking = false; meeting = true; } if (meeting) {
     * //System.out.println(a.getMeno() + ": " +
     * bNode.getWorldBound().distanceTo(aNode.getLocalTranslation()));
     * //System.out.println(b.getMeno() + ": " +
     * aNode.getWorldBound().distanceTo(bNode.getLocalTranslation()));
     *
     * if (aNode.getWorldBound().distanceTo(bNode.getLocalTranslation()) > 9) {
     * walking(a); walking(b); } else {
     * bControl.setViewDirection(aNode.getLocalTranslation().subtract(bNode.getLocalTranslation()));
     * bControl.setWalkDirection(aNode.getLocalTranslation().subtract(bNode.getLocalTranslation()));
     * aControl.setViewDirection(bNode.getLocalTranslation().subtract(aNode.getLocalTranslation()));
     * aControl.setWalkDirection(bNode.getLocalTranslation().subtract(aNode.getLocalTranslation()));
     *
     * collision(a, b, player); collision(a, b, null); } } }
     *
     * if (!aControl.getWalkDirection().equals(zeroDirection)) {
     * stayInWorld(aNode, aControl); } if
     * (!bControl.getWalkDirection().equals(zeroDirection)) { stayInWorld(bNode,
     * bControl); } }
     */
    /**
     * Funkcia kontrolujúca kolíziu jednotlivých postáv a,b,c. Môžu nastať dva
     * prípady, že sú zadané všetke tri postavy alebo len dva postavy, ktorých
     * vzájomnú kolíziu je potebné kontrolovať.
     *
     * @param a Postava, ktorej je kontrolovaná kolízia.
     * @param b Postava, s ktorou je kontrolovaná kolízia.
     * @param c Postava, s ktorou je kontrolovaná kolízia.
     *
     * private void collision(Character a, Character b, Character c) { if (c !=
     * null) { if
     * (a.getNode().getWorldBound().distanceTo(b.getNode().getLocalTranslation())
     * < 3 &&
     * a.getNode().getWorldBound().distanceTo(c.getNode().getLocalTranslation())
     * < 2) { //System.out.println("Konverzuje " + a.getMeno() + " a " +
     * b.getMeno() + " a " + c.getMeno() + "."); a.setAnimation("Stand");
     * b.setAnimation("Stand"); c.setAnimation("Stand");
     * a.getControl().setWalkDirection(zeroDirection);
     * b.getControl().setWalkDirection(zeroDirection); walking = true;
     * a.doSomething(); b.doSomething(); } } else if
     * (a.getNode().getWorldBound().distanceTo(b.getNode().getLocalTranslation())
     * < 3) { //System.out.println("Konverzuje " + a.getMeno() + " a " +
     * b.getMeno() + "."); a.setAnimation("Stand"); b.setAnimation("Stand");
     * a.getControl().setWalkDirection(zeroDirection);
     * b.getControl().setWalkDirection(zeroDirection); walking = true;
     * a.doSomething(); b.doSomething(); } }
     */
}