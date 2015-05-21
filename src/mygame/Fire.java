/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

/**
 *
 * @author Jakub
 */
public class Fire{
    private ParticleEmitter fireEffect;    
    private AssetManager am;
    private float x;
    private float y;
    private float z;
    private boolean blow;
    
    public Fire(AssetManager am, float x, float y, float z){
        this.am = am;
        this.x = x;
        this.y = y;
        this.z = z;
        this.blow = false;
        
        fireEffect = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 80);        
        Material fireMat = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
        fireMat.setTexture("Texture", am.loadTexture("Effects/Explosion/flame.png"));
        fireEffect.setMaterial(fireMat);
        fireEffect.setLocalTranslation(x, y, z);
        fireEffect.setImagesX(1); 
        fireEffect.setImagesY(1); // 2x2 texture animation
        fireEffect.setEndColor( new ColorRGBA(1f, 0f, 0f, 1f) );   // red
        fireEffect.setStartColor( new ColorRGBA(1f, 1f, 0f, 0.5f) ); // yellow
        fireEffect.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 1.5f, 0));
        fireEffect.setStartSize(0.9f);
        fireEffect.setEndSize(0.2f);
        fireEffect.getParticleInfluencer().setVelocityVariation(0.3f);        
    }
    
    public ParticleEmitter fireNode(){
        return getFireEffect();
    }

    /**
     * @return the fireEffect
     */
    public ParticleEmitter getFireEffect() {
        return fireEffect;
    }

    /**
     * @return the x
     */
    public float getX() {
        return x;
    }

    /**
     * @return the y
     */
    public float getY() {
        return y;
    }

    /**
     * @return the z
     */
    public float getZ() {
        return z;
    }

    /**
     * @param y the y to set
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * @return the isBlow
     */
    public boolean isBlow() {
        return blow;
    }

    /**
     * @param isBlow the isBlow to set
     */
    public void setBlow(boolean blow) {
        this.blow = blow;
    }
}
