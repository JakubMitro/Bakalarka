package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.tools.Color;

public class NiftyWelcomeScreen {
    private AssetManager assetManager;
    private InputManager inputManager;
    private AudioRenderer audioRenderer;
    private ViewPort guiViewPort;
    private FlyByCamera flyCamera;
    
  public NiftyWelcomeScreen(AssetManager assetManager, InputManager inputManager,
    AudioRenderer audioRenderer, ViewPort guiViewPort, FlyByCamera flyCamera)
  {
        this.assetManager = assetManager;
        this.inputManager = inputManager;
        this.audioRenderer = audioRenderer;
        this.guiViewPort = guiViewPort;
        this.flyCamera = flyCamera;
  }

  public void InitNifty(NiftyWelcomeScreen welcomeScreen)
  {
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
                assetManager, inputManager, audioRenderer, guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
//        nifty.fromXml("Interface/tutorial/screen3.xml", "start");
        guiViewPort.addProcessor(niftyDisplay);
        flyCamera.setDragToRotate(true);

        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");

        // vytvorenie uvitacieho okna
        nifty.addScreen("start", new ScreenBuilder("start") {{
            controller(new mygame.MyStartScreen());
            layer(new LayerBuilder("background") {{
                childLayoutCenter();

                // nastavenie obrázka na pozadie
                image(new ImageBuilder() {{
                    filename("Textures/ohen.jpg");
                }});

            }});

            // Vrstva pre vytvorenie panelov na pridávanie textov
            layer(new LayerBuilder("foreground") {{
                    childLayoutVertical();

                // panel pre názov
                panel(new PanelBuilder("panel_top") {{
                    childLayoutCenter();
                    alignCenter();
                    height("15%");
                    width("75%");

                    // text názvu
                    text(new TextBuilder() {{
                        text("Zachran rodinu");
                        font("Interface/Fonts/Default.fnt"); 
                        pixels(999);
                        height("100%");
                        width("100%");
                    }});

                }});

                // panel pre popis hry
                panel(new PanelBuilder("panel_mid") {{
                    childLayoutCenter();
                    alignCenter();
                    height("50%");
                    width("75%");

                    // text o hre
                    text(new TextBuilder() {{
                        text("Hra sa odohravá v mestskom prostredí. Úlohou hrajucého je "
                                + "zachránt osoby z horiaceho domu. K obsluhe sú klavesy: "
                                + "\n\nH: hasenie ohna \nN: nasledovanie hrajúveho zachranenovanou osobou\n"
                                + "\nPozor!!! Ohen sa síri, tak rýchlo na pomoc!");
                        font("Interface/Fonts/Default.fnt");
                        wrap(true);
                        height("120%");
                        width("120%");
                    }});

                }});

                //panel s tlačidlami
                panel(new PanelBuilder("panel_bottom") {{
                    childLayoutHorizontal();
                    alignCenter();
                    height("25%");
                    width("75%");

                    //vľavo
                    panel(new PanelBuilder("panel_bottom_left") {{
                        childLayoutCenter();
                        valignCenter();
                        height("50%");
                        width("50%");

                        // vytovrenie tlačidla pre štart hry
                        control(new ButtonBuilder("StartButton", "Start") {{
                          alignCenter();
                          valignCenter();
                          height("50%");
                          width("50%");
                          visibleToMouse(true);
    //                      interactOnClick("startGame2()");
                          interactOnClick("startGame(hud)");
                        }});

                    }});

                    //vpravo
                    panel(new PanelBuilder("panel_bottom_right") {{
                        childLayoutCenter();
                        valignCenter();
                        height("50%");
                        width("50%");

                        // vytvorenie tlačidla pre ukončenie hry
                        control(new ButtonBuilder("QuitButton", "Koniec") {{
                          alignCenter();
                          valignCenter();
                          height("50%");
                          width("50%");
                          visibleToMouse(true); 
                          interactOnClick("quitGame()");
                        }});

                    }});
                }}); 
            }});

        }}.build(nifty));


        nifty.addScreen("hud", new ScreenBuilder("hud") {{
            controller(new mygame.MyStartScreen());
        }}.build(nifty));

        nifty.gotoScreen("start"); // spustenie uvitacieho okna
  }
}
