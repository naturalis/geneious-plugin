package nl.naturalis.geneious.util;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.biomatters.geneious.publicapi.plugin.Icons;

/**
 * Provides differently sized and differently colored Naturalis icons.
 * 
 * @author Ayco Holleman
 *
 */
public class NaturalisIcons extends Icons {

  private static final String color = "blue";

  private static final ImageIcon original = new ImageIcon("images/nbc_" + color + ".png");
  private static final ImageIcon size16 = new ImageIcon("images/nbc_" + color + "_14x33.png");
  private static final ImageIcon size24 = new ImageIcon("images/nbc_" + color + "_16x38.png");
  private static final ImageIcon size32 = new ImageIcon("images/nbc_" + color + "_18x42.png");

  public static final NaturalisIcons INSTANCE = new NaturalisIcons();

  private NaturalisIcons() {
    super(original);
  }

  @Override
  public synchronized Icon getIcon16() {
    return size16;
  }

  @Override
  public synchronized Icon getIcon24() {
    return size24;
  }

  @Override
  public synchronized Icon getIcon32() {
    return size32;
  }

}
