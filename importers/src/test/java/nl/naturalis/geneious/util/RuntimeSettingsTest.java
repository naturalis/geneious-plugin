package nl.naturalis.geneious.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

public class RuntimeSettingsTest {

  @Test
  public void test2() {
    RuntimeSettings.INSTANCE.setSeqLastSelectedTargetFolderId("ABC");
    assertEquals("ABC",RuntimeSettings.INSTANCE.getSeqLastSelectedTargetFolderId());
    RuntimeSettings.INSTANCE.setSmplLastSelectedTargetFolderId("XYZ");
    assertEquals("ABC",RuntimeSettings.INSTANCE.getSeqLastSelectedTargetFolderId());
    assertEquals("XYZ",RuntimeSettings.INSTANCE.getSmplLastSelectedTargetFolderId());
    RuntimeSettings.INSTANCE.setSeqLastSelectedTargetFolderId("DEF");
    assertEquals("DEF",RuntimeSettings.INSTANCE.getSeqLastSelectedTargetFolderId());
    assertEquals("XYZ",RuntimeSettings.INSTANCE.getSmplLastSelectedTargetFolderId());
    RuntimeSettings.INSTANCE.setSmplLastSelectedTargetFolderId("");
    assertNull(RuntimeSettings.INSTANCE.getSmplLastSelectedTargetFolderId());
    RuntimeSettings.INSTANCE.setSmplLastSelectedTargetFolderId("    ");
    assertNull(RuntimeSettings.INSTANCE.getSmplLastSelectedTargetFolderId());
  }

}
