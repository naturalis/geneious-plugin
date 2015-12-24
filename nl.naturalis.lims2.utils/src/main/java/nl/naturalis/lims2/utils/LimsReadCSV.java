/**
 * 
 */
package nl.naturalis.lims2.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsReadCSV {

	BufferedReader br = null;
	String line = "";
	String cvsSplitBy = "\t";

	public LimsReadCSV(String fileName) {

		try {

			br = new BufferedReader(new FileReader(fileName));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] country = line.split(cvsSplitBy);

				System.out.println("Country [code= " + country[4] + " , name="
						+ country[5] + "]");

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("Done");
	}

}
