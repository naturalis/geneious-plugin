package nl.naturalis.geneious.split;

import javax.swing.SwingWorker;

public class NameSplitter extends SwingWorker<Void, Void> {

  private final NameSplitterConfig cfg;

  public NameSplitter(NameSplitterConfig cfg) {
    this.cfg = cfg;
  }

  @Override
  protected Void doInBackground() throws Exception {
    splitNames();
    return null;
  }

  public void splitNames() {

  }

}
