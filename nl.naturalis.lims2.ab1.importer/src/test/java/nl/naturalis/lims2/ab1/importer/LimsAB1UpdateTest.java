package nl.naturalis.lims2.ab1.importer;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

public class LimsAB1UpdateTest {

	@Before
	public void setup() throws Exception {
		String filename = "e4010125015_Sil_tri_MJ243_COI-A01_M13F_A01_008.ab1";
		System.out.println("@Before filename: " + filename);

	}

	@Test
	public void updateAB1Files() {
		String filename = "e4010125015_Sil_tri_MJ243_COI-A01_M13F_A01_008.ab1";
		String Result = null;
		if (filename.contains("_")) {
			String[] underscore = StringUtils.split(filename, "_");
			Result = "ExtractID: " + underscore[0] + " PCRPlaatID: "
					+ underscore[3] + " Marker: "
					+ underscore[4].substring(0, underscore[4].indexOf("-"));
			assertEquals("Extract AB1 ID", underscore[0], underscore[0]);
			assertEquals("Extract AB1 PCR PlaatID", underscore[3],
					underscore[3]);
			assertEquals("Extract AB1 Marker", underscore[4], underscore[4]);
			System.out.println("@Test: " + Result);
			System.out.println("@Test ID: " + underscore[0]);
			System.out.println("@Test PCRPlaatID: " + underscore[3]);
			System.out.println("@Test Marker: " + underscore[4]);
		}
	}

}
