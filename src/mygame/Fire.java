/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.collision.UnsupportedCollisionException;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import java.util.Queue;

/**
 *
 * @author student
 */
public class Fire extends Spatial{
    private ParticleEmitter fireEffect;
    
    private float timeO = 0;
    private int state = 0;
    private Node explosionEffect = new Node("explosionFX");
    private ParticleEmitter flame, flash, spark, roundspark, smoketrail, debris,
                            shockwave;


    private static final int COUNT_FACTOR = 1;
    private static final float COUNT_FACTOR_F = 1f;

    private static final boolean POINT_SPRITE = true;
    private static final Type EMITTER_TYPE = POINT_SPRITE ? Type.Point : Type.Triangle;
    private AssetManager am;
    private float time = 0;
    private float speed = 50;
    private Thread explosionThread;
    private float x;
    private float y;
    private float z;
        float a = 0;
        float b = 0;
        float c = 0;
    private int i = 0;
    
    public Fire(AssetManager am, float x, float y, float z){
        this.am = am;
        this.x = x;
        this.y = y;
        this.z = z;
        
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
        
//        fireEffect.setGravity(0f,0f,0f);
//        fireEffect.setLowLife(0.2f);
//        fireEffect.setHighLife(2f);
        fireEffect.getParticleInfluencer().setVelocityVariation(0.3f);
        
    }
    
    public Fire(Material fireMat, float x, float y, float z){
        fireEffect = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 80);
        
    //    fireMat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
        fireEffect.setMaterial(fireMat);
        fireEffect.setLocalTranslation(x, y, z);
        fireEffect.setImagesX(4); 
        fireEffect.setImagesY(4); // 2x2 texture animation
        fireEffect.setEndColor( new ColorRGBA(1f, 0f, 0f, 1f) );   // red
        fireEffect.setStartColor( new ColorRGBA(1f, 1f, 0f, 0.5f) ); // yellow
        fireEffect.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 1.5f, 0));
        fireEffect.setStartSize(0.9f);
        fireEffect.setEndSize(0.2f);
        
//        fireEffect.setGravity(0f,0f,0f);
//        fireEffect.setLowLife(0.2f);
//        fireEffect.setHighLife(2f);
        fireEffect.getParticleInfluencer().setVelocityVariation(0.3f);
    }
    
    public ParticleEmitter fireNode(){
        return getFireEffect();
    }
    
    public void vybuch()
    {
        createFlame();
        createFlash();
//        createSpark();  //... bodočky 
//        createRoundSpark();
//        createSmokeTrail();
//        createDebris();  //... rozhadzujuce sa kocočky
//        createShockwave();
    }
    
    public void update()
    {        
//        explosionThread = new Thread()
//        {
//            @Override
//            public void run()
//            {
        if(i < 2000)
        {
            float tpf = 1;
            time += tpf / speed;
            if (time > 1f && state == 0){
                getFlash().emitAllParticles();
//                getSpark().emitAllParticles();
//                getSmoketrail().emitAllParticles();
//                getDebris().emitAllParticles();
//                getShockwave().emitAllParticles();
                setState(+1);
            }
            if (time > 1f + .05f / speed && state == 1){
                getFlame().emitAllParticles();
//                getRoundspark().emitAllParticles();
                setState(+1);
            }
            System.out.println("pocitam i: " + i);
            i++;
        }
//                }
//            }
//        };
//        getExplosionThread().start();
        
        
//         rewind the effect 
       if (time > 5 / speed && state == 2){
            setState(0);
            time = 0;

            getFlash().killAllParticles();
            getSpark().killAllParticles();
            getSmoketrail().killAllParticles();
            getDebris().killAllParticles();
            getFlame().killAllParticles();
            getRoundspark().killAllParticles();
            getShockwave().killAllParticles();
        }
    }
    
    private void createFlame(){
        flame = new ParticleEmitter("Flame", EMITTER_TYPE, 32 * COUNT_FACTOR);
        getFlame().setSelectRandomImage(true);
        getFlame().setStartColor(new ColorRGBA(1f, 0.4f, 0.05f, (float) (1f / COUNT_FACTOR_F)));
        getFlame().setEndColor(new ColorRGBA(.4f, .22f, .12f, 0f));
        getFlame().setStartSize(1.3f);
        getFlame().setEndSize(2f);
        getFlame().setShape(new EmitterSphereShape(Vector3f.ZERO, 1f));
        getFlame().setParticlesPerSec(0);
        getFlame().setGravity(0, -5, 0);
        getFlame().setLowLife(.4f);
        getFlame().setHighLife(.5f);
        getFlame().getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0.02f, 0));
        getFlame().getParticleInfluencer().setVelocityVariation(1f);
        getFlame().setImagesX(2);
        getFlame().setImagesY(2);
        Material mat = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", am.loadTexture("Effects/Explosion/flame.png"));
        mat.setBoolean("PointSprite", POINT_SPRITE);
        getFlame().setMaterial(mat);
        getExplosionEffect().attachChild(getFlame());
    }

    private void createFlash(){
        flash = new ParticleEmitter("Flash", EMITTER_TYPE, 24 * COUNT_FACTOR);
        getFlash().setSelectRandomImage(true);
        getFlash().setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, (float) (1f / COUNT_FACTOR_F)));
        getFlash().setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, 0f));
        getFlash().setStartSize(.1f);
        getFlash().setEndSize(3.0f);
        getFlash().setShape(new EmitterSphereShape(Vector3f.ZERO, .05f));
        getFlash().setParticlesPerSec(0);
        getFlash().setGravity(0, 0, 0);
        getFlash().setLowLife(.2f);
        getFlash().setHighLife(.2f);
        getFlash().getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0.01f, 0));
        getFlash().setInitialVelocity(new Vector3f(0, 5f, 0));
        getFlash().setVelocityVariation(1);
        getFlash().setImagesX(2);
        getFlash().setImagesY(2);
        Material mat = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", am.loadTexture("Effects/Explosion/flash.png"));
        mat.setBoolean("PointSprite", POINT_SPRITE);
        getFlash().setMaterial(mat);
        getExplosionEffect().attachChild(getFlash());
    }

    private void createRoundSpark(){
        roundspark = new ParticleEmitter("RoundSpark", EMITTER_TYPE, 20 * COUNT_FACTOR);
        getRoundspark().setStartColor(new ColorRGBA(1f, 0.29f, 0.34f, (float) (1.0 / COUNT_FACTOR_F)));
        getRoundspark().setEndColor(new ColorRGBA(0, 0, 0, (float) (0.5f / COUNT_FACTOR_F)));
        getRoundspark().setStartSize(1.2f);
        getRoundspark().setEndSize(1.8f);
        getRoundspark().setShape(new EmitterSphereShape(Vector3f.ZERO, 2f));
        getRoundspark().setParticlesPerSec(0);
        getRoundspark().setGravity(0, -.5f, 0);
        getRoundspark().setLowLife(1.8f);
        getRoundspark().setHighLife(2f);
        getRoundspark().setInitialVelocity(new Vector3f(0, 3, 0));
        getRoundspark().setVelocityVariation(.5f);
        getRoundspark().setImagesX(1);
        getRoundspark().setImagesY(1);
        Material mat = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", am.loadTexture("Effects/Explosion/roundspark.png"));
        mat.setBoolean("PointSprite", POINT_SPRITE);
        getRoundspark().setMaterial(mat);
        getExplosionEffect().attachChild(getRoundspark());
    }

    private void createSpark(){
        spark = new ParticleEmitter("Spark", ParticleMesh.Type.Triangle, 30 * COUNT_FACTOR);
        getSpark().setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, (float) (1.0f / COUNT_FACTOR_F)));
        getSpark().setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, 0f));
        getSpark().setStartSize(.5f);
        getSpark().setEndSize(.5f);
        getSpark().setFacingVelocity(true);
        getSpark().setParticlesPerSec(0);
        getSpark().setGravity(0, 5, 0);
        getSpark().setLowLife(1.1f);
        getSpark().setHighLife(1.5f);
        getSpark().getParticleInfluencer().setInitialVelocity(new Vector3f(0, 20, 0));
        getSpark().getParticleInfluencer().setVelocityVariation(1);
        getSpark().setImagesX(1);
        getSpark().setImagesY(1);
        Material mat = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", am.loadTexture("Effects/Explosion/spark.png"));
        getSpark().setMaterial(mat);
        getExplosionEffect().attachChild(getSpark());
    }

    private void createSmokeTrail(){
        smoketrail = new ParticleEmitter("SmokeTrail", ParticleMesh.Type.Triangle, 22 * COUNT_FACTOR);
        getSmoketrail().setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, (float) (1.0f / COUNT_FACTOR_F)));
        getSmoketrail().setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, 0f));
        getSmoketrail().setStartSize(.2f);
        getSmoketrail().setEndSize(1f);

//        smoketrail.setShape(new EmitterSphereShape(Vector3f.ZERO, 1f));
        getSmoketrail().setFacingVelocity(true);
        getSmoketrail().setParticlesPerSec(0);
        getSmoketrail().setGravity(0, 1, 0);
        getSmoketrail().setLowLife(.4f);
        getSmoketrail().setHighLife(.5f);
        getSmoketrail().setInitialVelocity(new Vector3f(0, 12, 0));
        getSmoketrail().setVelocityVariation(1);
        getSmoketrail().setImagesX(1);
        getSmoketrail().setImagesY(3);
        Material mat = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", am.loadTexture("Effects/Explosion/smoketrail.png"));
        getSmoketrail().setMaterial(mat);
        getExplosionEffect().attachChild(getSmoketrail());
    }

    private void createDebris(){
        debris = new ParticleEmitter("Debris", ParticleMesh.Type.Triangle, 15 * COUNT_FACTOR);
        getDebris().setSelectRandomImage(true);
        getDebris().setRandomAngle(true);
        getDebris().setRotateSpeed(FastMath.TWO_PI * 4);
        getDebris().setStartColor(new ColorRGBA(1f, 0.59f, 0.28f, (float) (1.0f / COUNT_FACTOR_F)));
        getDebris().setEndColor(new ColorRGBA(.5f, 0.5f, 0.5f, 0f));
        getDebris().setStartSize(.2f);
        getDebris().setEndSize(.2f);

//        debris.setShape(new EmitterSphereShape(Vector3f.ZERO, .05f));
        getDebris().setParticlesPerSec(0);
        getDebris().setGravity(0, 12f, 0);
        getDebris().setLowLife(1.4f);
        getDebris().setHighLife(1.5f);
        getDebris().setInitialVelocity(new Vector3f(0, 15, 0));
        getDebris().setVelocityVariation(.60f);
        getDebris().setImagesX(3);
        getDebris().setImagesY(3);
        Material mat = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", am.loadTexture("Effects/Explosion/Debris.png"));
        getDebris().setMaterial(mat);
        getExplosionEffect().attachChild(getDebris());
    }

    private void createShockwave(){
        shockwave = new ParticleEmitter("Shockwave", ParticleMesh.Type.Triangle, 1 * COUNT_FACTOR);
//        shockwave.setRandomAngle(true);
        getShockwave().setFaceNormal(Vector3f.UNIT_Y);
        getShockwave().setStartColor(new ColorRGBA(.48f, 0.17f, 0.01f, (float) (.8f / COUNT_FACTOR_F)));
        getShockwave().setEndColor(new ColorRGBA(.48f, 0.17f, 0.01f, 0f));

        getShockwave().setStartSize(0f);
        getShockwave().setEndSize(7f);

        getShockwave().setParticlesPerSec(0);
        getShockwave().setGravity(0, 0, 0);
        getShockwave().setLowLife(0.5f);
        getShockwave().setHighLife(0.5f);
        getShockwave().setInitialVelocity(new Vector3f(0, 0, 0));
        getShockwave().setVelocityVariation(0f);
        getShockwave().setImagesX(1);
        getShockwave().setImagesY(1);
        Material mat = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", am.loadTexture("Effects/Explosion/shockwave.png"));
        getShockwave().setMaterial(mat);
        getExplosionEffect().attachChild(getShockwave());
    }

    /**
     * @return the fireEffect
     */
    public ParticleEmitter getFireEffect() {
        return fireEffect;
    }

    /**
     * @return the timeO
     */
    public float getTimeO() {
        return timeO;
    }

    /**
     * @return the state
     */
    public int getState() {
        return state;
    }
    
    public void setState(int state){
        this.state = state;
    }

    /**
     * @return the explosionEffect
     */
    public Node getExplosionEffect() {
        return explosionEffect;
    }

    /**
     * @return the flame
     */
    public ParticleEmitter getFlame() {
        return flame;
    }

    /**
     * @return the flash
     */
    public ParticleEmitter getFlash() {
        return flash;
    }

    /**
     * @return the spark
     */
    public ParticleEmitter getSpark() {
        return spark;
    }

    /**
     * @return the roundspark
     */
    public ParticleEmitter getRoundspark() {
        return roundspark;
    }

    /**
     * @return the smoketrail
     */
    public ParticleEmitter getSmoketrail() {
        return smoketrail;
    }

    /**
     * @return the debris
     */
    public ParticleEmitter getDebris() {
        return debris;
    }

    /**
     * @return the shockwave
     */
    public ParticleEmitter getShockwave() {
        return shockwave;
    }

    /**
     * @return the explosionThread
     */
    public Thread getExplosionThread() {
        return explosionThread;
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
