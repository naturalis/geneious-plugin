package nl.naturalis.geneious.gui;

import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.RED;
import static java.awt.font.TextAttribute.FOREGROUND;
import static java.awt.font.TextAttribute.UNDERLINE;
import static java.awt.font.TextAttribute.UNDERLINE_ON;
import static nl.naturalis.common.CollectionMethods.tightHashMap;
import static nl.naturalis.common.Tuple.tuple;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import nl.naturalis.common.Tuple;

/**
 * Symbolic constants for message types, targeted at providing style attributes for them.
 */
@SuppressWarnings("unchecked")
public enum TextStyle {

  NORMAL(tuple(FOREGROUND, BLACK), tuple(UNDERLINE, null)),
  WARNING(tuple(FOREGROUND, RED), tuple(UNDERLINE, null)),
  HYPERLINK(tuple(FOREGROUND, BLUE), tuple(UNDERLINE, UNDERLINE_ON));

  private final HashMap<TextAttribute, Object> attribs;

  private TextStyle(Tuple<TextAttribute, Object>... textAttribs) {
    attribs = tightHashMap(textAttribs);
  }

  public Map<TextAttribute, Object> getTextAttributes() {
    return attribs;
  }

  public void applyTo(JLabel label) {
    Font font = label.getFont();
    if (this == WARNING) {
      font = font.deriveFont(Font.ITALIC);
    } else {
      font = font.deriveFont(Font.PLAIN);
    }
    label.setFont(font.deriveFont(attribs));
  }

  public void applyTo(JLabel label, String labelText) {
    Font font = label.getFont();
    if (this == WARNING) {
      font = font.deriveFont(Font.ITALIC);
    } else {
      font = font.deriveFont(Font.PLAIN);
    }
    label.setFont(font.deriveFont(attribs));
    label.setText(labelText);
  }

}
