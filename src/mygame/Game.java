package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.BasicShadowRenderer;
import java.util.ArrayList;
import mygame.jadex.JadexStarter;
import mygame.jadex.communication.Communicator;

/**
 * @author Jakub
 */
public class Game extends SimpleApplication implements ActionListener {

    private BulletAppState bulletAppState;
    private float hp = 100f;
    private float time = 0.7f;
    boolean w, a, s, d;
    private Vector3f walkDirection = new Vector3f(0, 0, 0);    
    private AudioNode audioNode; 
    private Spatial scene;
    private Communicator com;   
    Character janko, jozko, player, benny, crow, rytier;
    private static Sphere bullet;
    Node houseNode;
    private ArrayList<Spatial> fire;
    
    static float bLength = 0.48f;
    static float bWidth = 0.24f;
    static float bHeight = 0.12f;
    BasicShadowRenderer bsr;
    private static Box brick;
    private static SphereCollisionShape bulletCollisionShape;
    
    private ArrayList <Spatial> fireList;
    NiftyWelcomeScreen welcome;    
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
        flyCam.setMoveSpeed(80f);        
//        initJadex();      
        initScene();        
        initPlayer();        
        initInput();
        initAudio();
        com = Communicator.INSTANCE;
        initRescuee1();
        initRescuee2(); 
        initDog();
        initBird();
        initHouse();    
        initFire();    
//        initFire2();  
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
        
        if(!nasleduj)
            janko.walking();
        else
            janko.nasleduj(player, 5f);        
        
        vypisHP(); 
        if(player.checkHP())
            endGame(); 
    }
    
    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
        
    /**
     * Funkcia vypíše GAME OVER na obrazovku.
     */
    private void endGame()
    {
        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText text = new BitmapText(guiFont, false);
        text.setSize(50f);
        text.setText("GAME OVER");
        text.setLocalTranslation(200, 300, 000);
        guiNode.attachChild(text);
    }
    /**
     * Výpis života na obrazovku.
     */
    private void vypisHP()
    {
        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText text = new BitmapText(guiFont, false);
        text.setSize(guiFont.getCharSet().getRenderedSize());
        text.setText("HP: " + Math.round(player.getHp()));
        text.setLocalTranslation(500, 460, 000);
        guiNode.attachChild(text);
    }
    
    /**
     * cislo- urcuje vzdielnost od kamery
     */
    private void dontLookCrossWalls()
    {
        float x = cam.getDirection().getX();
        float z = cam.getDirection().getZ();
        float cislo = 1f;
        if(x >= 0 && z >=0)
        {
            cam.setLocation(player.getNode().getLocalTranslation().add(new Vector3f(-cislo, 2.8f, -cislo)));
        }
        else if(x < 0 && z >=0)
        {
            cam.setLocation(player.getNode().getLocalTranslation().add(new Vector3f(cislo, 2.8f, -cislo)));
        }
        else if(x < 0 && z < 0)
        {
            cam.setLocation(player.getNode().getLocalTranslation().add(new Vector3f(cislo, 2.8f, cislo)));
        }
        if(x >= 0 && z < 0)
        {
            cam.setLocation(player.getNode().getLocalTranslation().add(new Vector3f(-cislo, 2.8f, cislo)));
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

    
    
    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
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
        
        walkDirection.setY(0);
        player.getControl().setWalkDirection(walkDirection);
        player.getControl().setViewDirection(cam.getDirection());
        // uberanie zivota
//        if (player.nearFire(3, fire)) {
//            player.setHp(player.getHp() - 0.05f);
//            System.out.println("hp=" + player.getHp());
//        }
    }
    
/*------------------------------------------------------------------------------------------------------*/    
/* ------------------------ Funkcie na inicializáciu základného sveta --------------------------------- */
/*------------------------------------------------------------------------------------------------------*/ 

    public void initScene() {
        scene = assetManager.loadModel("Scenes/town/main.j3o");
        RigidBodyControl terrainControl = new RigidBodyControl(0f);
        scene.addControl(terrainControl);
        getPhysicsSpace().add(terrainControl);
        scene.setShadowMode(RenderQueue.ShadowMode.Receive);
        rootNode.attachChild(scene);
    }
    
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
    
    public void initState() {
        bulletAppState = new BulletAppState();
        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(false);
    }
    
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
        
        inputManager.addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "shoot");        
        inputManager.addMapping("shooot", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addListener(actionListener, "shooot");
        inputManager.addMapping("spread", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addListener(actionListener, "spread");
        inputManager.addMapping("nasleduj", new KeyTrigger(KeyInput.KEY_N));
        inputManager.addListener(actionListener, "nasleduj");    
    }
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            flyCam.setDragToRotate(false);
            if (name.equals("shooot") && !keyPressed)
                pouziHasicak();
            if(name.equals("spread") && !keyPressed)
                rozsirovanieOhna();
            if(name.equals("nasleduj") && !keyPressed)
                nasleduj = true;
        }
    };
    
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equalsIgnoreCase("W"))
            w = isPressed;
        if (name.equalsIgnoreCase("A"))
            a = isPressed;
        if (name.equalsIgnoreCase("S"))
            s = isPressed;
        if (name.equalsIgnoreCase("D"))
            d = isPressed;
    }
    
/*----------------------------------------------------------------------------------------------*/    
/* ------------------------ Funkcie na inicializáciu obejktov --------------------------------- */
/*----------------------------------------------------------------------------------------------*/ 
    
    public void initPlayer(){
        player = new Character("Hrac");
//        Node node = (Node)assetManager.loadModel("Models/player/Hero.mesh.j3o");
        player.makeNode("Player");
        player.getNode().scale(0.05f);
//        player.setNode(node);
        player.makeControl(new Vector3f(0.3f, 5f, 70f), new Vector3f(0,0,0));
//        player.makeAnimation("Stand");
        player.getControl().warp(new Vector3f(40.0f, 0f, 10.0f));
        player.getControl().setViewDirection(walkDirection);
        getPhysicsSpace().add(player.getControl());
        rootNode.attachChild(player.getNode());
    }
    
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
     * Inicializácia domu
     */    
    public void initHouse()
    {
        houseNode = (Node)assetManager.loadModel("Models/dom/dom.j3o"); 
        houseNode.setName("House");
        houseNode.setLocalTranslation(31.0f, 0.05f, 0.0f);  
        
            /** Pridanie slnka */ 
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(0.5f, 0.5f, 0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);     
        rootNode.attachChild(houseNode);
        
           /*Pridanie uzlov stien do zoznamu a následne odobratie uzlov dverí.*/
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
    
/*--------------------------------------------------------------------------------------*/    
/* ------------------------ Funkcie súvisiace s ohňom --------------------------------- */
/*--------------------------------------------------------------------------------------*/   
    
    private void initFire() {
        fireList = new ArrayList<Spatial>();
        fireList.add(new Fire(assetManager, 50, 0, -2));
        fireList.add(new Fire(assetManager, 50, 0, -10));
        fireList.add(new Fire(assetManager, 50, 0, -18));
        fireList.add(new Fire(assetManager, 50, 0, -17));
        fireList.add(new Fire(assetManager, 32, 0, -9));
        fireList.add(new Fire(assetManager, 32, 0, -1));
        fireList.add(new Fire(assetManager, 32, 0, 0));
        fireList.add(new Fire(assetManager, 33, 0, 0));
        fireList.add(new Fire(assetManager, 37, 0, 0));
        fireList.add(new Fire(assetManager, 40, 0, 0));
        fireList.add(new Fire(assetManager, 48, 0, 0));
        fireList.add(new Fire(assetManager, 42, 5, -10));
        
        for(int i =0; i < fireList.size(); i++)
        {
            Fire fire = (Fire) fireList.get(i);
            rootNode.attachChild(fire.fireNode());
        }
    }
    
    private void initFire2() {
        fire = new ArrayList<Spatial>();
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
        for (Spatial s : rootNode.getChildren()) {
            if (s.getName() != null && s.getName().equals("Emitter")) {
                fire.add(s);
            }
        }
    }
    
    /**
     * Funkcia vytvorí smršť vody a volá ďalšie funkcie pre kontrolu či si nachádza v blízkosti ohňa 
     * a ak áno, je tento oheň hasený.
     */
    private void pouziHasicak()
    {
        bulletAppState = new BulletAppState();
        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        stateManager.attach(bulletAppState);
        
        bullet = new Sphere(50, 50, 0.4f, true, false);
        bullet.setTextureMode(Sphere.TextureMode.Projected);
        bulletCollisionShape = new SphereCollisionShape(0.4f);
        brick = new Box(Vector3f.ZERO, bLength, bHeight, bWidth);
        brick.scaleTextureCoordinates(new Vector2f(1f, .5f));
        
        Water water = new Water(assetManager, new Vector3f(cam.getLocation()));
        SphereCollisionShape bulletCollisionShape = new SphereCollisionShape(0.4f);
        RigidBodyControl bulletNode = new RigidBodyControl(bulletCollisionShape, 1);
        bulletNode.setLinearVelocity(cam.getDirection().mult(25));
        water.getWaterEffect().addControl(bulletNode);
        rootNode.attachChild(water.getWaterEffect());
        getPhysicsSpace().add(bulletNode);
        
        hasit(5);
    }
    
    /**
     * Určenie, do ktorej strany sa hráč pozerá, či ide o kladné alebo záporne smery 
     * a podľa toho sa určí, či sa má hodnota výpočtu vzidlenosti pripočítavať alebo
     * odpočítavať.
     * @param vzdialenost hodnota, o ktorú ma byť hráč vzdialený od ohňa, aby mohol hasiť
     */
    public void hasit(int vzdialenost)
    {
        System.out.println("Robim funkciu hasit");
        float x = cam.getDirection().getX();
        float z = cam.getDirection().getZ();
        
        if(x >= 0 && z >=0)
            vzdialenostOhna((-vzdialenost),(-vzdialenost));
        else if(x < 0 && z >=0)
            vzdialenostOhna(vzdialenost,(-vzdialenost));
        else if(x < 0 && z < 0)
            vzdialenostOhna(vzdialenost,vzdialenost);
        if(x >= 0 && z < 0)
            vzdialenostOhna((-vzdialenost),vzdialenost);
    }
    
    /**
     * Funkcia určí, či je hráč v dostatotčne blízkej vzidlenosti od ohňa, aby ho mohol hasiť.
     * Vo funkcií sa robia prepočty podľa polohy smeru hráča v kladných a záporných smeroch.
     * @param rozmedzieA určuje X-ovú súradnicu vzdialenosti v kladnom aj zápornom smere
     * @param rozmedzieB určuje Z-ovú súradnicu vzdialenosti v kladnom aj zápornom smere
     */
    public void vzdialenostOhna(int rozmedzieA, int rozmedzieB)
    {
        float playerX = player.getNode().getLocalTranslation().getX();
        float playerZ = player.getNode().getLocalTranslation().getZ();
        Fire fire;
        
        System.out.println("Som v vzdialenostOhna");
        
        for(int i = 0; i < fireList.size(); i++)
        {
            fire = (Fire) fireList.get(i);
            float fireX = fire.getX();
            float fireZ = fire.getZ();
            
            if(fireX > playerX && fireZ > playerZ)
            {
                if( (fireX < (playerX - rozmedzieA)) && (fireZ < (playerZ - rozmedzieB)))
                    hasenieOhna(fire);
            }                
            else if(fireX > playerX && fireZ < playerZ)
            {
                if( (fireX < (playerX - rozmedzieA)) && (fireZ > (playerZ - rozmedzieB)))
                    hasenieOhna(fire);
            }                
            else if(fireX < playerX && fireZ > playerZ)
            {
                if( (fireX > (playerX - rozmedzieA)) && (fireZ < (playerZ - rozmedzieB)))
                    hasenieOhna(fire);
            }                
            else if(fireX < playerX && fireZ < playerZ)
            {
                if( (fireX > (playerX - rozmedzieA)) && (fireZ > (playerZ - rozmedzieB)))
                    hasenieOhna(fire);
            }
        }
        
    }
    
    /**
     * Zmenší veľkosť ohňa zníženímjeho pozíce o 1. Ak je jeho pozícia pod-6, zruší ho celkom.
     * @param fire oheň, ktorý má byť zmenšený
     */
    private void hasenieOhna(Fire fire)
    {
        System.out.println("Zmenšujem oheň");
        float a = fire.fireNode().getLocalTranslation().getX();
        float b = fire.fireNode().getLocalTranslation().getY() - 1;
        float c = fire.fireNode().getLocalTranslation().getZ();
        fire.fireNode().setLocalTranslation(a, b, c);
        
        if(fire.fireNode().getLocalTranslation().getY() <= -6)
        {
            System.out.println("Ruším oheň");
            fire.fireNode().killAllParticles();
            fire.fireNode().setEnabled(false);
        }
    }
    
    /**
     * Zväčšenie ohňa o jednu pätinu z pôvodnej veľkosti.
     */
    private void rozsirovanieOhna()
    {
        System.out.println("Rozsirujem ohen");
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
}
