package nl.naturalis.geneious.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import com.biomatters.geneious.publicapi.utilities.GuiUtilities;
import com.google.common.base.Preconditions;

/**
 * Utilities for the Geneious Desktop UI.
 */
public class GuiUtils {

  /**
   * Provides a bare-knuckle way of disabling a Swing component. This method is especially targeted at Geneious's
   * WritableDatabaseServiceTree class (an extension of JTree), which won't listen to the regular <code>setEnabled</code> or
   * <code>setEditable</code> methods. To achieve the same effect we simply remove all relevant listeners from the component.
   * 
   * @param component
   */
  public static void paralyse(Component component) {
    component.setFocusable(false);
    MouseListener[] mouseListeners = component.getMouseListeners();
    for (MouseListener listener : mouseListeners) {
      component.removeMouseListener(listener);
    }
    KeyListener[] keyListeners = component.getKeyListeners();
    for (KeyListener listener : keyListeners) {
      component.removeKeyListener(listener);
    }
  }

  /**
   * Scales the specified component relative to the main Geneious window.
   * 
   * @param component
   * @param scaleW
   * @param scaleH
   * @param minW
   * @param minH
   */
  public static void scale(Component component, double scaleW, double scaleH, int minW, int minH) {
    Preconditions.checkArgument(0 < scaleW && scaleW <= 1F, "scaleW must be between 0 and 1");
    Preconditions.checkArgument(0 < scaleH && scaleH <= 1F, "scaleH must be between 0 and 1");
    Preconditions.checkArgument(minW >= 10, "minW must be >= 10");
    Preconditions.checkArgument(minH >= 10, "minH must be >= 10");
    int w = GuiUtilities.getMainFrame().getWidth();
    int h = GuiUtilities.getMainFrame().getHeight();
    w = (int) Math.max(minW, w * scaleW);
    h = (int) Math.max(minH, h * scaleH);
    component.setPreferredSize(new Dimension(w, h));
  }

  /**
   * Positions the specified component relative to the main Geneious window.
   * 
   * @param component
   * @param fromLeft
   * @param fromTop
   * @param fromRight
   * @param fromBottom
   * @param minW
   * @param minH
   */
  public static void position(Component component, int fromLeft, int fromTop, int fromRight, int fromBottom, int minW, int minH) {
    Preconditions.checkArgument(fromLeft >= 0, "fromLeft must be >= 0");
    Preconditions.checkArgument(fromTop >= 0, "fromTop must be >= 0");
    Preconditions.checkArgument(fromRight >= 0, "fromRight must be >= 0");
    Preconditions.checkArgument(fromBottom >= 0, "fromBottom must be >= 0");
    Preconditions.checkArgument(minW >= 10, "maxW must be >= 10");
    Preconditions.checkArgument(minH >= 10, "maxH must be >= 10");
    int w = GuiUtilities.getMainFrame().getWidth();
    int h = GuiUtilities.getMainFrame().getHeight();
    w = Math.min(minW, w - fromLeft - fromRight);
    h = Math.min(minH, h - fromTop - fromBottom);
    component.setPreferredSize(new Dimension(w, h));
  }

}
