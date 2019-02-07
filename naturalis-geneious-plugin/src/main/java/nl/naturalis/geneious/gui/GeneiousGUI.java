package nl.naturalis.geneious.gui;

import java.awt.Component;
import java.awt.Dimension;

import com.biomatters.geneious.publicapi.utilities.GuiUtilities;
import com.google.common.base.Preconditions;

/**
 * General utilities for the Geneious Desktop UI.
 */
public class GeneiousGUI {

  /**
   * The minimum width given to a component if not specifiable through a method parameter.
   */
  public static final int DEFAULT_MIN_WIDTH = 450;
  /**
   * The minimum height given to a component if not specifiable through a method parameter.
   */
  public static final int DEFAULT_MIN_HEIGHT = 300;

  /**
   * Scales the specified component relative to the main Geneious window as obtained by GuiUtilities.getMainFrame().
   * 
   * @param component
   * @param scale
   */
  public static void scaleComponent(Component component, float scale) {
    scaleComponent(component, scale, scale, DEFAULT_MIN_WIDTH, DEFAULT_MIN_HEIGHT);
  }

  /**
   * Scales the specified component relative to the main Geneious window as obtained by GuiUtilities.getMainFrame().
   * 
   * @param component
   * @param scale
   * @param minLength
   */
  public static void scaleComponent(Component component, float scale, int minLength) {
    scaleComponent(component, scale, scale, minLength, minLength);
  }

  /**
   * Scales the specified component relative to the main Geneious window as obtained by GuiUtilities.getMainFrame().
   * 
   * @param component
   * @param scaleW
   * @param scaleH
   */
  public static void scaleComponent(Component component, float scaleW, float scaleH) {
    scaleComponent(component, scaleW, scaleH, DEFAULT_MIN_WIDTH, DEFAULT_MIN_HEIGHT);
  }

  /**
   * Scales the specified component relative to the main Geneious window as obtained by GuiUtilities.getMainFrame().
   * 
   * @param component
   * @param scaleW
   * @param scaleH
   * @param minW
   * @param minH
   */
  public static void scaleComponent(Component component, float scaleW, float scaleH, int minW, int minH) {
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
   * Positions the specified component relative to the main Geneious window as obtained by GuiUtilities.getMainFrame(). Keeps a margin of
   * 250 pixels around the component.
   * 
   * @param component
   * @param offset
   */
  public static void offsetComponent(Component component) {
    offsetComponent(component, 250);
  }

  /**
   * Positions the specified component relative to the main Geneious window as obtained by GuiUtilities.getMainFrame(). Keeps the specified
   * margin around the component.
   * 
   * @param component
   * @param offset
   */
  public static void offsetComponent(Component component, int offset) {
    offsetComponent(component, offset, offset);
  }

  /**
   * Positions the specified component relative to the main Geneious window as obtained by GuiUtilities.getMainFrame().
   * 
   * @param component
   * @param fromLeft
   * @param fromTop
   */
  public static void offsetComponent(Component component, int fromLeft, int fromTop) {
    int maxW = GuiUtilities.getMainFrame().getWidth() - (2 * fromLeft);
    int maxH = GuiUtilities.getMainFrame().getHeight() - (2 * fromTop);
    offsetComponent(component, fromLeft, fromTop, fromLeft, fromTop, maxW, maxH);
  }

  /**
   * Positions the specified component relative to the main Geneious window as obtained by GuiUtilities.getMainFrame().
   * 
   * @param component
   * @param fromLeft
   * @param fromTop
   * @param maxLen
   */
  public static void offsetComponent(Component component, int fromLeft, int fromTop, int maxLen) {
    offsetComponent(component, fromLeft, fromTop, fromLeft, fromTop, maxLen, maxLen);
  }

  /**
   * Positions the specified component relative to the main Geneious window as obtained by GuiUtilities.getMainFrame().
   * 
   * @param component
   * @param fromLeft
   * @param fromTop
   * @param fromRight
   * @param fromBottom
   * @param maxW
   * @param maxH
   */
  public static void offsetComponent(Component component, int fromLeft, int fromTop, int fromRight, int fromBottom, int maxW, int maxH) {
    Preconditions.checkArgument(fromLeft >= 0, "fromLeft must be >= 0");
    Preconditions.checkArgument(fromTop >= 0, "fromTop must be >= 0");
    Preconditions.checkArgument(fromRight >= 0, "fromRight must be >= 0");
    Preconditions.checkArgument(fromBottom >= 0, "fromBottom must be >= 0");
    Preconditions.checkArgument(maxW >= 10, "maxW must be >= 10");
    Preconditions.checkArgument(maxH >= 10, "maxH must be >= 10");
    int w = GuiUtilities.getMainFrame().getWidth();
    int h = GuiUtilities.getMainFrame().getHeight();
    w = Math.min(maxW, w - fromLeft - fromRight);
    h = Math.min(maxH, h - fromTop - fromBottom);
    // Ensure component has a minium width and height;
    w = Math.max(DEFAULT_MIN_WIDTH, w);
    h = Math.max(DEFAULT_MIN_HEIGHT, h);
    // But also ensure component is never bigger than main window;
    w = Math.min(GuiUtilities.getMainFrame().getWidth(), w);
    h = Math.min(GuiUtilities.getMainFrame().getHeight(), h);
    component.setPreferredSize(new Dimension(w, h));
  }

}
