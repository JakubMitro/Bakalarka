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
public class Water {
    private ParticleEmitter waterEffect;
    
    public Water(AssetManager am, Vector3f pozicia)
    {        
        waterEffect = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 80);
        Material waterMat = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
        waterMat.setTexture("Texture", am.loadTexture("Effects/Explosion/water.png"));
        waterEffect.setMaterial(waterMat);
        waterEffect.setLocalTranslation(pozicia.x, pozicia.y, pozicia.z);
        waterEffect.setEndColor(ColorRGBA.White);  
        waterEffect.setStartColor( ColorRGBA.Blue );
        waterEffect.getParticleInfluencer().setInitialVelocity(new Vector3f(0, -1.5f, 1));
        waterEffect.setStartSize(0.9f);
        waterEffect.setEndSize(0.2f);
        waterEffect.getParticleInfluencer().setVelocityVariation(0.1f);
    }

    /**
     * @return the waterEffect
     */
    public ParticleEmitter getWaterEffect() {
        return waterEffect;
    }
}
