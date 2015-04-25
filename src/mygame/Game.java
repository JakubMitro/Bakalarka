package mygame;

//import antlr.collections.impl.IntRange;
import com.jme3.animation.AnimChannel;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.BasicShadowRenderer;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.water.SimpleWaterProcessor;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import mygame.jadex.JadexStarter;
import mygame.jadex.communication.AgentProps;
import mygame.jadex.communication.Communicator;

/**
 * @author Jakub
 */
public class Game extends SimpleApplication implements ActionListener {

    private BulletAppState bulletAppState;
    private float hp = 100f;
    private ArrayList<Spatial> fire;
    boolean w, a, s, d;
    private float time = 0.7f;
    private Vector3f walkDirection = new Vector3f(0, 0, 0);
    private Vector3f zeroDirection = new Vector3f(0, 0, 0);
    private AudioNode audioNode; 
    boolean walking = false;
    boolean meeting = true;
    private Spatial scene;
    private Communicator com;
    private AgentProps jaimeJadex;
    private AgentProps joeyJadex;    
    Character janko, jozko, player, benny, crow, rytier;
    Fire explozia;
    Fire fire1;
    Fire fire2;
    Fire fire3;
    Fire fire4;
    Fire fire5, fire60;
    private static Sphere bullet;
    private Hasenie hasenie;   
    Node houseNode;
    
    static float bLength = 0.48f;
    static float bWidth = 0.24f;
    static float bHeight = 0.12f;
    Material mat;
    Material mat2;
    Material mat3;
    BasicShadowRenderer bsr;
    private static Box brick;
    private static SphereCollisionShape bulletCollisionShape;
    ArrayList fireList;
    NiftyWelcomeScreen welcome;
    private boolean mouse;
    
    boolean nasleduj = false;
    
    
    public void turnOn() {
        this.start();
    }

    public void initJadex() {
        String[] agents = new String[2];
        agents[0] = "mygame/jadex/meeting/jaime.agent.xml";
        agents[1] = "mygame/jadex/meeting/joey.agent.xml";
        JadexStarter start = new JadexStarter(agents);
        Thread t = new Thread(start);
        t.start();
    }
    
    @Override
    public void simpleInitApp() {
        
        welcome = new NiftyWelcomeScreen(assetManager, inputManager, audioRenderer, guiViewPort, flyCam);
        setDisplayFps(false);
        setDisplayStatView(false);
        welcome.InitNifty(welcome);
        initState();
        initLight();
//        flyCam.setMoveSpeed(50f);        
//        initJadex();      
        initScene();        
        initPlayer();
        
//        flyCam.setEnabled(true);
//        CameraNode camNode = new CameraNode("Camera Node",cam);
////        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
//        camNode.setLocalTranslation(new Vector3f(0, -2,-1));
////        player.getNode().attachChild(camNode);
//        camNode.lookAt(player.getNode().getLocalTranslation(), Vector3f.UNIT_Y);
        
        initInput();
        initAudio();
        com = Communicator.INSTANCE;
        initRescuee1();
        initRescuee2();   
        initWater();
        initDog();
        initBird();
        initHouse();
//        initWaterfall();
        
        initHasenie();
        initFire();
        
        
    
    }

    @Override
    public void simpleUpdate(float tpf) { 
        player.getControl().setViewDirection(cam.getDirection());
        playerAction(tpf);
        janko.walking(0.5f);
        if (jozko.nearFire(2, fireList)) {
            jozko.runFromFire(speed);
            hp -= 0.8;
            System.out.println("hp jozko" + hp);
        } else {
            jozko.setBusy(false);
            jozko.walking();
        }
//        workWithHouse();
//        explozia.update();  
    //        explozia.update();
    //        angle += tpf;
    //        angle %= FastMath.TWO_PI;
    //        float x = FastMath.cos(angle) * 2;
    //        float y = FastMath.sin(angle) * 2;
    //        emit.setLocalTranslation(x, 2, y);
    //        if(jozko.isKraca() || player.isKraca())
    //            collision(jozko, player, null);
    //            collision(janko, player, null);  
    //            collision(janko, player, null);
        
//        walkInWorld(jozko, janko);   
//        walking(jozko, 1);
//        walking(benny, 3);
//        
//        if(!nasleduj)
//            walking(janko, 1);
//        else
//            nasleduj(janko, 5);
//        walking(crow, 1);
//        somVOhni(player,3);   
//        System.out.println("X: " + player.getNode().getLocalTranslation().getX());
//        System.out.println("Z: " + player.getNode().getLocalTranslation().getZ());
          
//         System.out.println("jozko "+(Vector3f) jaimeJadex.get(IAgentProps.MoveTo)+" janko "+(Vector3f)joeyJadex.get(IAgentProps.MoveTo));
//        if ((Boolean) joeyJadex.get(IAgentProps.Leave) && (Boolean) jaimeJadex.get(IAgentProps.Leave) && 
//                joeyJadex.get(IAgentProps.MoveTo) != null && jaimeJadex.get(IAgentProps.MoveTo) != null) {
//            presun(jozko.getNode(), jozko.getControl(), (Vector3f) jaimeJadex.get(IAgentProps.MoveTo));
//            System.out.println("jozko ide tu "+jaimeJadex.get(IAgentProps.MoveTo));
//            presun(janko.getMeno(), janko.getControl(), (Vector3f)joeyJadex.get(IAgentProps.MoveTo));
//            System.out.println("janko ide tu "+joeyJadex.get(IAgentProps.MoveTo));
//            setAnimation(jozko.getAnimacia(), "Walk");
//            setAnimation(janko.getAnimacia(), "Walk");
//        }
        
        dontLookCrossWalls();
        
//        cam.setLocation(player.getNode().getLocalTranslation().add(new Vector3f(0.0f, 1.8f, 0.0f)));
        walkDirection.set(0,0,0);
        time +=tpf;
        
        if(a){
            walkDirection.addLocal(cam.getLeft().mult(5f));
            if(time >= 0.7f){
                audioNode.play();
                time = 0;
            }
        }
        if(s){
            walkDirection.addLocal(cam.getDirection().negate().mult(5f));
            if(time >= 0.7f){
                audioNode.play();
                time = 0;
            }
        }
        if(d){
            walkDirection.addLocal(cam.getLeft().negate().mult(5f));
            if(time >= 0.7f){
                audioNode.play();
                time = 0;
            }
        }
        if(w){
            walkDirection.addLocal(cam.getDirection().mult(5f));
            if(time >= 0.7f){
                audioNode.play();
                time = 0;
            }
        }
        walkDirection.setY(0);        
        player.getControl().setWalkDirection(walkDirection);  
    }
    
    /**
     * Postava a bude nasledovať hráča.
     * @param a postava, ktorá má nasledovať
     * @param rychlost rýchlosť pohybu postavy, čím vyššie číslo tým pomalšia rýchlosť 
     */
//    private void nasleduj(Character a, float rychlost)
//    {
//        Vector3f pozicia = player.getNode().getLocalTranslation().subtract(a.getNode().getLocalTranslation());
//        a.getControl().setWalkDirection(new Vector3f(pozicia.getX()/rychlost, pozicia.getY()/rychlost, pozicia.getZ()/rychlost)); 
//        a.getControl().setViewDirection(pozicia);
//
//        if(a.isNear(player, a, 2))
//        {
//            a.getControl().setWalkDirection(zeroDirection);
//            setAnimation(a.getAnimacia(),"Stand");
//        }
//        else
//        {       
//            if(a.getAnimacia().getAnimationName().equalsIgnoreCase("Stand"))
//                setAnimation(a.getAnimacia(),"Walk");
//        }
//    }
        
    /**
     * Hlavná funkcia zabezpečujúca kráčanie postáv "a" a "b" po svete. Funkcia volá funkciu pre kolíziu postáv 
     * a funkciu zabezpečujúcu nespadnutie postáv z herného sveta. V prípade, že sa postavy "a" a "b" k sebe približia
     * na menej ako 9 jednotiek, je ich smer nastavený tak, aby prišli k sebe a začali konvezáciu. Po konverzácií 
     * postavy môžu popri sebe prechádzať, no ďalšiu konverzáciu začnú až ked sa ich vzdialenosť zväčší na 10 jednotiek 
     * a následne zmenší pod 9 jednotiek. 
     * @param a postava
     * @param b postava
     */
//    private void walkInWorld(Character a, Character b)
//    { 
//        Node aNode = a.getNode();
//        Node bNode = b.getNode();
//        BetterCharacterControl aControl = a.getControl();
//        BetterCharacterControl bControl = b.getControl();
//        
//        if(a.isKraca() && b.isKraca())
//        {
//            if(walking)
//            {
//                walking(a, 1);
//                walking(b, 1);
//                meeting = false;
//            }
//            if(aNode.getWorldBound().distanceTo(bNode.getLocalTranslation()) > 10)
//            {
//                walking = false;
//                meeting = true;
//            }
//            if(meeting)
//            {
//                System.out.println(a.getMeno() + ": " + bNode.getWorldBound().distanceTo(aNode.getLocalTranslation()));
//                System.out.println(b.getMeno() + ": " + aNode.getWorldBound().distanceTo(bNode.getLocalTranslation()));
//
//                if(aNode.getWorldBound().distanceTo(bNode.getLocalTranslation()) > 9)
//                {
//                    walking(a, 1);
//                    walking(b, 1);        
//                }
//                else
//                {
//                    bControl.setViewDirection(aNode.getLocalTranslation().subtract(bNode.getLocalTranslation()));
//                    bControl.setWalkDirection(aNode.getLocalTranslation().subtract(bNode.getLocalTranslation()));        
//                    aControl.setViewDirection(bNode.getLocalTranslation().subtract(aNode.getLocalTranslation())); 
//                    aControl.setWalkDirection(bNode.getLocalTranslation().subtract(aNode.getLocalTranslation()));  
//
//                    collision(a, b, player);
//                    collision(a, b, null);
//                }
//            } 
//        }
//        
//        if(!aControl.getWalkDirection().equals(zeroDirection))
//            stayInWorld(aNode, aControl);
//        if(!bControl.getWalkDirection().equals(zeroDirection))
//            stayInWorld(bNode, bControl);
//    }
    
    private void dontLookCrossWalls()
    {
        float x = cam.getDirection().getX();
        float z = cam.getDirection().getZ();
        float cislo = 1f;
        if(x >= 0 && z >=0)
        {
            System.out.println("Pozerám vpravo pred seba.");
            cam.setLocation(player.getNode().getLocalTranslation().add(new Vector3f(-cislo, 2.8f, -cislo)));
        }
        else if(x < 0 && z >=0)
        {
            System.out.println("Pozerám vpravo za seba.");
            cam.setLocation(player.getNode().getLocalTranslation().add(new Vector3f(cislo, 2.8f, -cislo)));
        }
        else if(x < 0 && z < 0)
        {
            System.out.println("Pozerám vľavo za seba.");
            cam.setLocation(player.getNode().getLocalTranslation().add(new Vector3f(cislo, 2.8f, cislo)));
        }
        if(x >= 0 && z < 0)
        {
            System.out.println("Pozerám vľavo pred seba.");
            cam.setLocation(player.getNode().getLocalTranslation().add(new Vector3f(-cislo, 2.8f, cislo)));
        }
    }
    /**
     * Funkcia kontrolujúca kolíziu jednotlivých postáv a,b,c. Môžu nastať dva prípady, že sú zadané všetke tri postavy
     * alebo len dva postavy, ktorých vzájomnú kolíziu je potebné kontrolovať.
     * @param a Postava, ktorej je kontrolovaná kolízia.
     * @param b Postava, s ktorou je kontrolovaná kolízia.
     * @param c Postava, s ktorou je kontrolovaná kolízia.
     */
    private void collision(Character a, Character b, Character c) {
        if (c != null) {
            if (a.getNode().getWorldBound().distanceTo(b.getNode().getLocalTranslation()) < 3 && a.getNode().getWorldBound().distanceTo(c.getNode().getLocalTranslation()) < 2) {
                //System.out.println("Konverzuje " + a.getMeno() + " a " + b.getMeno() + " a " + c.getMeno() + ".");
                a.setAnimation("Stand");
                b.setAnimation("Stand");
                c.setAnimation("Stand");
                a.getControl().setWalkDirection(Vector3f.ZERO);
                b.getControl().setWalkDirection(Vector3f.ZERO);
                walking = true;
                a.doSomething();
                b.doSomething();
            }
        } else if (a.getNode().getWorldBound().distanceTo(b.getNode().getLocalTranslation()) < 3) {
            //System.out.println("Konverzuje " + a.getMeno() + " a " + b.getMeno() + ".");
            a.setAnimation("Stand");
            b.setAnimation("Stand");
            a.getControl().setWalkDirection(Vector3f.ZERO);
            b.getControl().setWalkDirection(Vector3f.ZERO);
            walking = true;
            a.doSomething();
            b.doSomething();
        }
    }
    
    /**
     * Funkcia walking zabezpečuje náhodne kráčanie postavy po svete. Animácie sa menia iba v tom prípade,
     * že sa zmenil stav postavy zo stojacej na kráčajúcu, respektíve naopak. Posledný príkaz znamená,
     * že ak sa postava zastaví, nenastaví sa jej smerovanie na 0,0,0 (dopredu), ale zostane v tom smere,
     * v ktorom sa pohybovala.
     * @param control Control postavy,ktorá má kráčať.
     * @param anim Premenná pre zmenu animácie postavy pri zastávení a pohnutí sa
     */
//    private void walking(Character a, int rychlost) 
//    {
//        int rozsah = 500;
//        Random generate = new Random();  
//        float k = generate.nextInt(rozsah);
//        float l = generate.nextInt(rozsah);        
//        float x = a.getControl().getWalkDirection().getX();
//        float y = a.getControl().getWalkDirection().getY();
//        float z = a.getControl().getWalkDirection().getZ();        
//        float pomx = x;
//        float pomz = z;
//        
//        if(k == 10)
//            x = rychlost;
//        else if(k == 20)
//            x = 0;
//        else if(k == 30)
//            x = -rychlost;
//        if(l == 15)
//            z = rychlost;
//        else if(l == 25)
//            z = 0;
//        else if(l == 35)
//            z = -rychlost;   
//        if((pomx == rychlost || pomz == rychlost || pomx == -rychlost || pomz == -rychlost) && (x == 0 && z == 0) && (a.getMeno().equals("Jozko") || a.getMeno().equals("Janko")))
//            setAnimation(a.getAnimation("Stand"));
//        else if((pomx == 0 && pomz == 0) && (x == rychlost || z == rychlost || x == -rychlost || z == -rychlost) && (a.getMeno().equals("Jozko") || a.getMeno().equals("Janko")))
//            setAnimation(a.getAnimacia(),"Walk");
//        else if((pomx == 0 && pomz == 0) && (x == rychlost || z == rychlost || x == -rychlost || z == -rychlost) && a.getMeno().equals("Benny"))
//            setAnimation(a.getAnimacia(),"run");
//        else if((pomx == rychlost || pomz == rychlost || pomx == -rychlost || pomz == -rychlost) && (x == 0 && z == 0) && a.getMeno().equals("Benny"))
//            setAnimation(a.getAnimacia(),"idle");
//        
//        Vector3f position = new Vector3f(x,y,z);
//        a.getControl().setWalkDirection(position); 
//        if(!position.equals(new Vector3f(0,y,0)))            
//            a.getControl().setViewDirection(position);
//    }
    
    private void animStayWalk(Character a)
    {
        if(a.getControl().getWalkDirection().equals(zeroDirection))
        {
            setAnimation(null, INPUT_MAPPING_EXIT);
        }
    }

    /**
     * Metóda, ktorá zabezpečuje, aby postava nespadla zo sveta. Ak postava dôjde na kraj, 
     * otočí sa o 180° a pokračuje novým smerom.
     * @param node Uzol postavy, ktorej pozícia je kontrolovaná.
     * @param control Ovládanie postavy, ktorej má byť zmenený smer pohybu.
     */
    private void stayInWorld(Node node, BetterCharacterControl control){
        if(node.getLocalTranslation().getX() > 20 )
        {            
            control.setWalkDirection(new Vector3f(-1,0,0));
            control.setViewDirection(new Vector3f(-1,0,0));
        }
        else if(node.getLocalTranslation().getX() < -20)
        {
            control.setWalkDirection(new Vector3f(1,0,0));
            control.setViewDirection(new Vector3f(1,0,0));
        }
        else if(node.getLocalTranslation().getZ() < -20)
        {
            control.setWalkDirection(new Vector3f(0,0,1));
            control.setViewDirection(new Vector3f(0,0,1));
        }
        else if(node.getLocalTranslation().getZ() > 20)
        {
            control.setWalkDirection(new Vector3f(0,0,-1));
            control.setViewDirection(new Vector3f(0,0,-1));
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

    /*
     * Zabezpečí inicializáciu scény = mapu sveta.
     */
    public void initScene() {
//        scene = assetManager.loadModel("Scenes/Island.j3o");
        scene = assetManager.loadModel("Scenes/town/main.j3o");
        RigidBodyControl terrainControl = new RigidBodyControl(0f);
        scene.addControl(terrainControl);
        getPhysicsSpace().add(terrainControl);
        scene.setShadowMode(RenderQueue.ShadowMode.Receive);
        rootNode.attachChild(scene);
    }

    /*
     * Zabzepečí inicializáciu svetla (slnka) vo vytvorenom svete.
     */
    public void initLight() {
        /** A white, directional light source. */
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);   
        
        /** A white ambient light source. */ 
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient); 
        
    }
    /*
     * Zabezepčí inicializáciu stavu.
     */
    public void initState() {
        bulletAppState = new BulletAppState();
        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(false);
    }

    /*
     * Pridávanie audio súborov do prostredia.
     */
    private void initAudio() {
        /* Neprehráva mp3jky */
        audioNode = new AudioNode(assetManager, "Sounds/footsteps-6.wav", false);
        audioNode.setPositional(false);
        audioNode.setLooping(false);
        rootNode.attachChild(audioNode);

        AudioNode natureAudio = new AudioNode(assetManager, "Sounds/Nature.ogg", false);
        natureAudio.setPositional(false);
        natureAudio.setLooping(true);
        rootNode.attachChild(natureAudio);
        natureAudio.play();
    }

    /*
     * Zabezpečí inicializáciu hráča.
     */
    public void initPlayer(){
        player = new Character("Hrac");
//        Node node = (Node)assetManager.loadModel("Models/player/Hero.mesh.j3o");
        player.makeNode("Player");
        player.getNode().scale(0.05f);
//        player.setNode(node);
        player.makeControl(new Vector3f(0.3f, 5f, 70f), new Vector3f(0,0,0));
//        player.makeAnimation("Stand");
        player.getControl().warp(new Vector3f(-10.0f, 0f, 0.0f));
        player.getControl().setViewDirection(walkDirection);
        getPhysicsSpace().add(player.getControl());
        rootNode.attachChild(player.getNode());
    }
    
     private void playerAction(float tpf) {
        
        cam.setLocation(player.getNode().getLocalTranslation().add(new Vector3f(0.0f, 2.5f, 3.0f)));
        walkDirection.set(0, 0, 0);
        time += tpf;
        if (a) {
            walkDirection.addLocal(cam.getLeft().mult(5f));
            if (time >= 0.7f) {
                audioNode.play();
                time = 0;
            }
        }
        if (s) {
            walkDirection.addLocal(cam.getDirection().negate().mult(5f));
            if (time >= 0.7f) {
                audioNode.play();
                time = 0;
            }
        }
        if (d) {
            walkDirection.addLocal(cam.getLeft().negate().mult(5f));
            if (time >= 0.7f) {
                audioNode.play();
                time = 0;
            }
        }
        if (w) {
            walkDirection.addLocal(cam.getDirection().mult(5f));
            if (time >= 0.7f) {
                audioNode.play();
                time = 0;
            }
        }
//        if(mouse){
//            if(!player.getAnimation().equals("UseHatchet"))
//            player.setAnimation("UseHatchet");
//        } else{
//            player.setAnimation("Stand");
//        }
        
        walkDirection.setY(0);
        player.getControl().setWalkDirection(walkDirection);
        player.getControl().setViewDirection(cam.getDirection());
        // uberanie zivota
        if (player.nearFire(3, fireList)) {
            hp -= 0.05;
            System.out.println("hp=" + hp);
        }
    }

    /*
     * Zabezpečuje čítanie znakov z klávesnice.
     */
    public void initInput() {
        inputManager.addMapping("W", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("A", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("S", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("D", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("E", new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping("LMB",
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("f1", new KeyTrigger(KeyInput.KEY_F1));
        inputManager.addMapping("f2", new KeyTrigger(KeyInput.KEY_F2));
        inputManager.addMapping("f3", new KeyTrigger(KeyInput.KEY_F3));
        inputManager.addMapping("f4", new KeyTrigger(KeyInput.KEY_F4));
        inputManager.addMapping("f5", new KeyTrigger(KeyInput.KEY_F5));
        inputManager.addMapping("f6", new KeyTrigger(KeyInput.KEY_F6));
        inputManager.addMapping("f7", new KeyTrigger(KeyInput.KEY_F7));
        inputManager.addMapping("f8", new KeyTrigger(KeyInput.KEY_F8));
        inputManager.addMapping("f9", new KeyTrigger(KeyInput.KEY_F9));
        inputManager.addMapping("f10", new KeyTrigger(KeyInput.KEY_F10));
        inputManager.addMapping("f11", new KeyTrigger(KeyInput.KEY_F11));
        inputManager.addMapping("f12", new KeyTrigger(KeyInput.KEY_F12));
        inputManager.addListener(this, new String[]{"W", "A", "S", "D", "E", "LMB", "f1", "f2", "f3", "f4", "f5", "f6", "f7", "f8", "f9", "f10", "f11", "f12"});
    }
    /*
     * Priradi konkrétnemu znaku hodnotu pravda, v prípade stlačenia klávesy.
     */

    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equalsIgnoreCase("W")) {
            w = isPressed;
        }
        if (name.equalsIgnoreCase("A")) {
            a = isPressed;
        }
        if (name.equalsIgnoreCase("S")) {
            s = isPressed;
        }
        if (name.equalsIgnoreCase("D")) {
            d = isPressed;
        }
    }
    
    /*
     * Funkcia na vytvorenie postavy.
     */
    public void initRescuee1() 
    {
        jozko = new Character("Jozko");
        Node node = (Node)assetManager.loadModel("Models/rescuee1/Hero.mesh.j3o"); 
        node.setLocalTranslation(-20.0f,0f,-20.0f);
        jozko.makeNode("jozko");
        jozko.setNode(node);
        jozko.makeControl(new Vector3f(0.8f, 4f, 80f), new Vector3f(0.0f, -30f, 0.0f));
        jozko.makeAnimation("Stand");
        getPhysicsSpace().add(jozko.getControl());
        rootNode.attachChild(jozko.getNode());        
    }
    
    /*
     * Funkcia na vytvorenie postavy.
     */
    public void initRescuee2() 
    {
        janko = new Character("Janko");
        Node node = (Node)assetManager.loadModel("Models/rescuee2/Hero.mesh.j3o"); 
        node.setLocalTranslation(-10.0f,0f,-20.0f);
        janko.makeNode("janko");
        janko.setNode(node);
        janko.makeControl(new Vector3f(0.5f, 4f, 80f), new Vector3f(0.0f, -30f, 0.0f));
        janko.makeAnimation("Stand");
        getPhysicsSpace().add(janko.getControl());
        rootNode.attachChild(janko.getNode());        
    }
    
    private void initDog(){
        benny = new Character("Benny");
        Node node = (Node) assetManager.loadModel("Models/prostredie/pes/benny.j3o");
        node.setLocalTranslation(-10.0f, 0.0f, -15.0f);
        benny.makeNode("Benny");
        node.scale(0.6f, 0.6f, 0.6f);
//        node.setLocalTranslation(17.0f, 0.0f, -1.0f);
        benny.setNode(node);
        benny.makeControl(new Vector3f(0.01f, 0.01f, 0.01f), new Vector3f(0.0f, -30f, 0.0f));
        benny.makeAnimation("idle", "Cube");
        benny.getControl().setViewDirection(new Vector3f(-1, 0.0f, 0));
        getPhysicsSpace().add(benny.getControl());
        rootNode.attachChild(benny.getNode());
    }
    
    private void initBird(){
        crow = new Character("Crow");
        Node node = (Node) assetManager.loadModel("Models/prostredie/vták/crow.j3o");
        crow.makeNode("Crow");
        node.scale(0.2f, 0.2f, 0.2f);
        node.setLocalTranslation(0.0f, 3.0f, 0.0f);
        crow.setNode(node);
        crow.makeControl(new Vector3f(0.56f, 4f, 80f), new Vector3f(0.0f, 0f, 0.0f));
        crow.makeAnimation("Fly", "Crow");
        crow.getControl().setViewDirection(new Vector3f(-1, 0.0f, -1.0f));
//        getPhysicsSpace().add(crow.getControl());
        rootNode.attachChild(crow.getNode());
    }

    /**
     * Nastavenie animácie postavám.
     * @param anim Postava, ktorej má byť animácia pridelená.
     * @param type Typ animácie, ktorý má byť priradený. 
     */
    private void setAnimation(AnimChannel anim, String type) {
        anim.setAnim(type);
    }
    
    /**
     * Funkcia vytvorí vodnú plochu o veľkosti quad. Polohu vody udáva water.setLocalTranslation- 
     * daný bod tvorí ľavý dolný bod vodného štvoruholníka. 
     */
    public void initWater()
    {
        SimpleWaterProcessor waterProcessor = new SimpleWaterProcessor(assetManager);
        waterProcessor.setReflectionScene(scene);
        waterProcessor.setWaterColor(ColorRGBA.Brown);
        
        Quad quad = new Quad(300,300);
        quad.scaleTextureCoordinates(new Vector2f(3f,3f));

        Geometry water=new Geometry("water", quad);
        water.setShadowMode(RenderQueue.ShadowMode.Receive);
        water.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        water.setMaterial(waterProcessor.getMaterial());
        water.setLocalTranslation(-100, -0.1f, 100f);

        rootNode.attachChild(water);
        viewPort.addProcessor(waterProcessor);
        rootNode.attachChild(scene);
    }
    
    private void initFire() {
        fireList = new ArrayList<Spatial>();
        Material fireMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        Fire fire1 = new Fire(fireMat, 50, 0, -2);
        Fire fire2 = new Fire(fireMat, 50, 0, -10);
        Fire fire3 = new Fire(fireMat, 50, 0, -18);
        Fire fire4 = new Fire(fireMat, 32, 0, -17);
        Fire fire5 = new Fire(fireMat, 32, 0, -9);
        Fire fire6 = new Fire(fireMat, 32, 0, -1);
        Fire fire7 = new Fire(fireMat, 33, 0, 0);
        Fire fire8 = new Fire(fireMat, 37, 0, 0);
        Fire fire9 = new Fire(fireMat, 40, 0, 0);
        Fire fire10 = new Fire(fireMat, 48, 0, 0);
        Fire fire11 = new Fire(fireMat, 42, 5, -10);
        rootNode.attachChild(fire1.fireNode());
        rootNode.attachChild(fire2.fireNode());
        rootNode.attachChild(fire3.fireNode());
        rootNode.attachChild(fire4.fireNode());
        rootNode.attachChild(fire5.fireNode());
        rootNode.attachChild(fire6.fireNode());
        rootNode.attachChild(fire7.fireNode());
        rootNode.attachChild(fire8.fireNode());
        rootNode.attachChild(fire9.fireNode());
        rootNode.attachChild(fire10.fireNode());
        rootNode.attachChild(fire11.fireNode());
       
        fireList = new ArrayList<Fire>(); 
        fireList.add(fire1);
        fireList.add(fire2);
        fireList.add(fire3);
        fireList.add(fire4);
        fireList.add(fire5);
        fireList.add(fire6);
        fireList.add(fire7);
        fireList.add(fire8);
        fireList.add(fire9);
        fireList.add(fire10);
        fireList.add(fire11);
        System.out.println("Fire: " + fireList.size());
    }
        
    public void initHouse()
    {
        houseNode = (Node)assetManager.loadModel("Models/dom/dom.j3o"); 
//        houseNode = (Node)assetManager.loadModel("Models/dom/dae/dom.j3o"); 
        houseNode.setName("House");
        houseNode.setLocalTranslation(31.0f, 0.05f, 0.0f);  
        
            /** A white, directional light source */ 
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(0.5f, 0.5f, 0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);     
        rootNode.attachChild(houseNode);
        
        ArrayList walls = new ArrayList <Node>();
        for(int i = 0; i < houseNode.getChildren().size(); i++)
        {
            walls.add(houseNode.getChild(i));         
        }
        walls.remove(houseNode.getChild("SketchUp.011"));
        walls.remove(houseNode.getChild("SketchUp.012"));
        walls.remove(houseNode.getChild("SketchUp.013"));
        walls.remove(houseNode.getChild("SketchUp.014"));
        walls.remove(houseNode.getChild("SketchUp.015"));
        walls.remove(houseNode.getChild("SketchUp.016"));
        walls.remove(houseNode.getChild("SketchUp.017"));
        
        for(int i = 0; i < walls.size(); i++)
        {
            Node wallNode = (Node) walls.get(i);
            CollisionShape houseShape = CollisionShapeFactory.createMeshShape(wallNode);
            RigidBodyControl houseControl = new RigidBodyControl(houseShape, 0);
            houseNode.addControl(houseControl);
            getPhysicsSpace().add(houseControl); 
            rootNode.attachChild(houseNode);
        }
    } 
    
    private void initHasenie(){
        bulletAppState = new BulletAppState();
        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        stateManager.attach(bulletAppState);

        
        bullet = new Sphere(50, 50, 0.4f, true, false);
        bullet.setTextureMode(Sphere.TextureMode.Projected);
        bulletCollisionShape = new SphereCollisionShape(0.4f);
        brick = new Box(Vector3f.ZERO, bLength, bHeight, bWidth);
        brick.scaleTextureCoordinates(new Vector2f(1f, .5f));

//        initMaterial();
        
        inputManager.addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "shoot");
        
        inputManager.addMapping("shooot", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addListener(actionListener, "shooot");
        
        inputManager.addMapping("explosion", new KeyTrigger(KeyInput.KEY_E));
        inputManager.addListener(actionListener, "explosion");
        
        inputManager.addMapping("spread", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addListener(actionListener, "spread");
        
        inputManager.addMapping("kill", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addListener(actionListener, "kill");
        
        inputManager.addMapping("nasleduj", new KeyTrigger(KeyInput.KEY_N));
        inputManager.addListener(actionListener, "nasleduj");
    }
    
    private ActionListener actionListener = new ActionListener() {

        public void onAction(String name, boolean keyPressed, float tpf) {
        flyCam.setDragToRotate(false);
            if (name.equals("shooot") && !keyPressed) {
                Water water = new Water(assetManager, new Vector3f(cam.getLocation()));
                SphereCollisionShape bulletCollisionShape = new SphereCollisionShape(0.4f);
                RigidBodyControl bulletNode = new RigidBodyControl(bulletCollisionShape, 1);
                bulletNode.setLinearVelocity(cam.getDirection().mult(25));
                water.getWaterEffect().addControl(bulletNode);
                rootNode.attachChild(water.getWaterEffect());
                getPhysicsSpace().add(bulletNode);
                hasit();
            }
            if(name.equals("explosion") && !keyPressed)
            {
                initExplosion();
            }
            if(name.equals("spread") && !keyPressed)
            {
                rozsirovanieOhna();
            }
            if(name.equals("nasleduj") && !keyPressed)
            {
                nasleduj = true;
            }
        }
    };
    
    public void initMaterial() {
        mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key = new TextureKey("Textures/dirt.jpg");
        key.setGenerateMips(true);
        Texture tex = assetManager.loadTexture(key);
        mat.setTexture("ColorMap", tex);

        mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key2 = new TextureKey("Textures/grass.jpg");
        key2.setGenerateMips(true);
        Texture tex2 = assetManager.loadTexture(key2);
        mat2.setTexture("ColorMap", tex2);

        mat3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key3 = new TextureKey("Textures/grass.jpg");
        key3.setGenerateMips(true);
        Texture tex3 = assetManager.loadTexture(key3);
        tex3.setWrap(WrapMode.Repeat);
        mat3.setTexture("ColorMap", tex3);
    }
    
    public void hasit()
    {
        int rozmedzie = 10;
        float x = cam.getDirection().getX();
        float z = cam.getDirection().getZ();
        if(x >= 0 && z >=0)
        {
            System.out.println("Pozerám vpravo pred seba.");
            vzdialenostOhna((-rozmedzie),(-rozmedzie));
        }
        else if(x < 0 && z >=0)
        {
            System.out.println("Pozerám vpravo za seba.");
            vzdialenostOhna(rozmedzie,(-rozmedzie));
        }
        else if(x < 0 && z < 0)
        {
            System.out.println("Pozerám vľavo za seba.");
            vzdialenostOhna(rozmedzie,rozmedzie);
        }
        if(x >= 0 && z < 0)
        {
            System.out.println("Pozerám vľavo pred seba.");
            vzdialenostOhna((-rozmedzie),rozmedzie);
        }
    }
    
    /**
     * 
     * @param rozmedzieA určuje X-ovú súradnicu vzdialenosti v kladnom aj zápornom smere
     * @param rozmedzieB určuje Z-ovú súradnicu vzdialenosti v kladnom aj zápornom smere
     */
    public void vzdialenostOhna(int rozmedzieA, int rozmedzieB)
    {
        float playerX = player.getNode().getLocalTranslation().getX();
        float playerZ = player.getNode().getLocalTranslation().getZ();
        Fire fire;
        
        for(int i = 0; i < fireList.size(); i++)
        {
            fire = (Fire) fireList.get(i);
            float fireX = fire.getX();
            float fireZ = fire.getZ();
            
            if(fireX > playerX && fireZ > playerZ)
            {
                if( (playerX < fireX && fireX < (playerX - rozmedzieA)) && (playerZ < fireZ && fireZ < (playerZ - rozmedzieB)))
                {
                    float a = fire.fireNode().getLocalTranslation().getX();
                    float b = fire.fireNode().getLocalTranslation().getY() - 1;
                    float c = fire.fireNode().getLocalTranslation().getZ();
                    fire.fireNode().setLocalTranslation(a, b, c);
                    zrusenieOhna(fire);
                }
            }
            else if(fireX > playerX && fireZ < playerZ)
            {
                if( (playerX < fireX && fireX < (playerX - rozmedzieA)) && (playerZ > fireZ && fireZ > (playerZ - rozmedzieB)))
                {
                    float a = fire.fireNode().getLocalTranslation().getX();
                    float b = fire.fireNode().getLocalTranslation().getY() - 1;
                    float c = fire.fireNode().getLocalTranslation().getZ();
                    fire.fireNode().setLocalTranslation(a, b, c);
                    zrusenieOhna(fire);
                }
            }
            else if(fireX < playerX && fireZ > playerZ)
            {
                if( (playerX > fireX && fireX > (playerX - rozmedzieA)) && (playerZ < fireZ && fireZ < (playerZ - rozmedzieB)))
                {
                    float a = fire.fireNode().getLocalTranslation().getX();
                    float b = fire.fireNode().getLocalTranslation().getY() - 1;
                    float c = fire.fireNode().getLocalTranslation().getZ();
                    fire.fireNode().setLocalTranslation(a, b, c);
                    zrusenieOhna(fire);
                }
            }
            else if(fireX < playerX && fireZ < playerZ)
            {
                if( (playerX > fireX && fireX > (playerX - rozmedzieA)) && (playerZ > fireZ && fireZ > (playerZ - rozmedzieB)))
                {
                    float a = fire.fireNode().getLocalTranslation().getX();
                    float b = fire.fireNode().getLocalTranslation().getY() - 1;
                    float c = fire.fireNode().getLocalTranslation().getZ();
                    fire.fireNode().setLocalTranslation(a, b, c);
                    zrusenieOhna(fire);
                }
            }
        }
        
    }
    
    private void initExplosion()
    {        
        System.out.println("robim exploziu");
        explozia = new Fire(assetManager, 32.0f, 10.0f, 0.0f);
        explozia.vybuch();
        explozia.getExplosionEffect().setLocalTranslation(-10.0f, 3.0f, 20.0f);
        explozia.getExplosionEffect().setLocalTranslation(32.0f, 3.0f, -10.0f);        
        explozia.getExplosionEffect().setLocalScale(5.2f);
        rootNode.attachChild(explozia.getExplosionEffect()); 
        explozia.update();
        initFire();
    }
    
    private void rozsirovanieOhna()
    {
        System.out.println("rozsirujem ohen");
        Fire fire = null;
        for(int i = 0; i < fireList.size(); i++)
        {
            fire = (Fire) fireList.get(i);
            float startSize = fire.fireNode().getStartSize();
            float endSize = fire.fireNode().getEndSize();
            fire.fireNode().setStartSize(startSize * 1.2f);
            fire.fireNode().setEndSize(endSize * 0.8f);
        }
    }
    
    private void zrusenieOhna(Fire fire)
    {
        if(fire.fireNode().getLocalTranslation().getY() <= -6)
        {
            fire.fireNode().killAllParticles();
            fire.fireNode().setEnabled(false);
        }
    }   
    
    private boolean somVOhni(Character a, int vzdialenost)
    {
        Fire fire;        
        for(int i = 0; i < fireList.size(); i++)
        {
            fire = (Fire) fireList.get(i);
            float vzdialenostX = Math.abs(a.getNode().getLocalTranslation().x - fire.fireNode().getLocalTranslation().x);
            float vzdialenostZ = Math.abs(a.getNode().getLocalTranslation().z - fire.fireNode().getLocalTranslation().z);
            
            if((vzdialenostX <= fire.fireNode().getStartSize() + vzdialenost) && (vzdialenostZ <= fire.fireNode().getStartSize() + vzdialenost))
                return true;
        }
        return false;
    }
}
