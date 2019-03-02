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
   * Resizes the specified component to a square with the specified side length.
   * @param component
   * @param w
   * @param h
   */
  public static void resize(Component component, int len) {
    component.setPreferredSize(new Dimension(len, len));
  }

  /**
   * Resizes the specified component to the specified width and height.
   * @param component
   * @param w
   * @param h
   */
  public static void resize(Component component, int w, int h) {
    component.setPreferredSize(new Dimension(w, h));
  }

  /**
   * Scales the specified component relative to the main Geneious window as obtained by GuiUtilities.getMainFrame().
   * 
   * @param component
   * @param scale
   */
  public static void scale(Component component, double scale) {
    scale(component, scale, scale, DEFAULT_MIN_WIDTH, DEFAULT_MIN_HEIGHT);
  }

  /**
   * Scales the specified component relative to the main Geneious window as obtained by GuiUtilities.getMainFrame().
   * 
   * @param component
   * @param scale
   * @param minLength
   */
  public static void scale(Component component, double scale, int minLength) {
    scale(component, scale, scale, minLength, minLength);
  }

  /**
   * Scales the specified component relative to the main Geneious window as obtained by GuiUtilities.getMainFrame().
   * 
   * @param component
   * @param scaleW
   * @param scaleH
   */
  public static void scale(Component component, double scaleW, double scaleH) {
    scale(component, scaleW, scaleH, DEFAULT_MIN_WIDTH, DEFAULT_MIN_HEIGHT);
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
   * Positions the specified component relative to the main Geneious window as obtained by GuiUtilities.getMainFrame(). Keeps a margin of
   * 250 pixels around the component.
   * 
   * @param component
   * @param offset
   */
  public static void position(Component component) {
    position(component, 250);
  }

  /**
   * Positions the specified component relative to the main Geneious window as obtained by GuiUtilities.getMainFrame(). Keeps the specified
   * margin around the component.
   * 
   * @param component
   * @param offset
   */
  public static void position(Component component, int offset) {
    position(component, offset, offset);
  }

  /**
   * Positions the specified component relative to the main Geneious window as obtained by GuiUtilities.getMainFrame().
   * 
   * @param component
   * @param fromLeft
   * @param fromTop
   */
  public static void position(Component component, int fromLeft, int fromTop) {
    int maxW = GuiUtilities.getMainFrame().getWidth() - (2 * fromLeft);
    int maxH = GuiUtilities.getMainFrame().getHeight() - (2 * fromTop);
    position(component, fromLeft, fromTop, fromLeft, fromTop, maxW, maxH);
  }

  /**
   * Positions the specified component relative to the main Geneious window as obtained by GuiUtilities.getMainFrame().
   * 
   * @param component
   * @param fromLeft
   * @param fromTop
   * @param minW
   */
  public static void position(Component component, int fromLeft, int fromTop, int minW, int minH) {
    position(component, fromLeft, fromTop, fromLeft, fromTop, minW, minH);
  }

  /**
   * Positions the specified component relative to the main Geneious window as obtained by GuiUtilities.getMainFrame().
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
