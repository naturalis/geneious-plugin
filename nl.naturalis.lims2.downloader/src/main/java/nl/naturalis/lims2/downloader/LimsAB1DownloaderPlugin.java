/**
 * 
 */
package nl.naturalis.lims2.downloader;

import com.biomatters.geneious.publicapi.plugin.GeneiousPlugin;
import com.biomatters.geneious.publicapi.plugin.GeneiousService;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsAB1DownloaderPlugin extends GeneiousPlugin {

	static final String HELP = "Naturalis download ab1 files";

	// LimsAB1Fields limsAB1Fields = new LimsAB1Fields();

	@Override
	public String getAuthors() {
		return "Reinier.Kartowikromo at Naturalis Biodiversity Center";
	}

	@Override
	public String getDescription() {
		return "Download Files";
	}

	@Override
	public String getHelp() {
		return HELP;
	}

	@Override
	public int getMaximumApiVersion() {
		return 8;
	}

	@Override
	public String getMinimumApiVersion() {
		return "8.1";
	}

	@Override
	public String getName() {
		return "AB1 Tool Downloader";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	public GeneiousService[] getServices() {
		LimsAB1Fields.init();
		GeneiousService[] services = new GeneiousService[1];
		services[0] = new LimsAB1DBService();
		return services;
	}
}
