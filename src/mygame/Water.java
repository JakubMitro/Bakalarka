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
import java.awt.Color;

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
//        final Material mat = new Material(am,
//                "ShaderBlow/MatDefs/FakeParticleBlow/FakeParticleBlow.j3md");
//        Material waterMat = am.loadMaterial(("Materials/FakeParticleBlow2/FakeParticleBlow.j3m"));

//        waterMat.setTexture("Texture", am.loadTexture("Effects/Explosion/flame.png"));
        waterEffect.setMaterial(waterMat);
        waterEffect.setLocalTranslation(pozicia.x, pozicia.y, pozicia.z);
        waterEffect.setEndColor( new ColorRGBA(1.5f, 1.5f, 1.5f, 1f) );  
        waterEffect.setStartColor( new ColorRGBA(1f, 0.25f, 0.25f, 0.25f) );
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
