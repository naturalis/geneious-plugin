package nl.naturalis.geneious;

import com.biomatters.geneious.publicapi.plugin.*;

public class NaturalisGeneiousPlugin extends GeneiousPlugin {
    public String getName() {
        return "NaturalisGeneiousPluginPlugin";
    }

    public String getHelp() {
        return "NaturalisGeneiousPluginPlugin";
    }

    public String getDescription() {
        return "NaturalisGeneiousPluginPlugin";
    }

    public String getAuthors() {
        return "Biomatters";
    }

    public String getVersion() {
        return "0.1";
    }

    public String getMinimumApiVersion() {
        return "4.1";
    }

    public int getMaximumApiVersion() {
        return 4;
    }

    @Override
    public DocumentFileImporter[] getDocumentFileImporters() {
        return new DocumentFileImporter[]{new ExampleFastaImporter()};
    }
}