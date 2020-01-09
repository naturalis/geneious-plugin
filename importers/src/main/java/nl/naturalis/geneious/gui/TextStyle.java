package nl.naturalis.geneious.gui;

import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.RED;
import static java.awt.Font.ITALIC;
import static java.awt.Font.PLAIN;
import static java.awt.font.TextAttribute.FONT;
import static java.awt.font.TextAttribute.FOREGROUND;
import static java.awt.font.TextAttribute.UNDERLINE;
import static java.awt.font.TextAttribute.UNDERLINE_ON;
import static nl.naturalis.common.CollectionMethods.tightHashMap;
import static nl.naturalis.common.Tuple.tuple;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import nl.naturalis.common.Tuple;

@SuppressWarnings("unchecked")
public enum TextStyle {

  NORMAL(tuple(FONT, PLAIN), tuple(FOREGROUND, BLACK), tuple(UNDERLINE, null)),
  WARNING(tuple(FONT, ITALIC), tuple(FOREGROUND, RED), tuple(UNDERLINE, null)),
  HYPERLINK(tuple(FONT, PLAIN), tuple(FOREGROUND, BLUE), tuple(UNDERLINE, UNDERLINE_ON));

  private final HashMap<TextAttribute, Object> attribs;

  private TextStyle(Tuple<TextAttribute, Object>... textAttribs) {
    attribs = tightHashMap(textAttribs);
  }

  public Map<TextAttribute, Object> getTextAttributes() {
    return attribs;
  }

  public void applyTo(JLabel label) {
    label.setFont(label.getFont().deriveFont(attribs));
  }

  public void applyTo(JLabel label, String labelText) {
    label.setFont(label.getFont().deriveFont(attribs));
    label.setText(labelText);
  }

}
