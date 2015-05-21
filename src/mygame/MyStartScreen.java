package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;

/**
 *
 */
public class MyStartScreen extends AbstractAppState implements ScreenController {

  private Nifty nifty;
  private Application app;
  private Screen screen;
  public static Boolean bool = false;

  /** custom methods */
  public MyStartScreen() {
    /** You custom constructor, can accept arguments */
  }

  public void startGame(String nextScreen) {
      bool = true;
    nifty.gotoScreen(nextScreen);  // switch to another screen
  }
  
  public void setHpBar(Element element, float progress) {
        final int MIN_WIDTH = 32;
        int pixelWidth = (int) (MIN_WIDTH + (element.getParent().getWidth() - MIN_WIDTH) * progress);
        element.setConstraintWidth(new SizeValue(pixelWidth + "px"));
        element.getParent().layoutElements();
    }

  public void quitGame() {
    System.exit(1);
    app.stop();
  }

  public String getPlayerName() {
    return System.getProperty("user.name");
  }

  /** Nifty GUI ScreenControl methods */
  public void bind(Nifty nifty, Screen screen) {
    this.nifty = nifty;
    this.screen = screen;
  }

  public void onStartScreen() {
  }

  public void onEndScreen() {
  }

  /** jME3 AppState methods */
  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    this.app = app;
  }

  @Override
  public void update(float tpf) {
    if (screen.getScreenId().equals("hud")) {
      Element niftyElement = nifty.getCurrentScreen().findElementByName("score");
      // Display the time-per-frame -- this field could also display the score etc...
      niftyElement.getRenderer(TextRenderer.class).setText((int)(tpf*100000) + ""); 
    }
  }
}
