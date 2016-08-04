package nl.naturalis.lims2.utils;

import static nl.naturalis.lims2.utils.TestUtils.writeFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.junit.Test;

public class LimsImporterUtilTest {
	
	public void testGetLogFileName() {
		String tmpUserDir = System.getProperty("java.io.tmpdir");
		System.setProperty("user.dir", tmpUserDir);
		Path p = FileSystems.getDefault().getPath(tmpUserDir, "lims-import.properties");
		writeFile(p, "logname=foo.log");
		LimsImporterUtil liu = new LimsImporterUtil();
		String value = liu.getLogFilename();
		assertEquals("01", "foo.log", value);		
	}

	@Test
	public void testGetPropValues_01() throws IOException
	{
		String tmpUserDir = System.getProperty("java.io.tmpdir");
		System.setProperty("user.dir", tmpUserDir);
		Path p = FileSystems.getDefault().getPath(tmpUserDir, "lims-import.properties");
		writeFile(p, "foo=bar");
		LimsImporterUtil liu = new LimsImporterUtil();
		String value = liu.getDatabasePropValues("foo");
		assertEquals("01", "bar", value);
	}

	@Test
	public void testGetDatabasePropValues_01() throws IOException
	{
		String tmpUserDir = System.getProperty("java.io.tmpdir");
		System.setProperty("user.dir", tmpUserDir);
		Path p = FileSystems.getDefault().getPath(tmpUserDir, "limsdatabase.properties");
		writeFile(p, "foo=bar");
		LimsImporterUtil liu = new LimsImporterUtil();
		String value = liu.getDatabasePropValues("foo");
		assertEquals("01", "bar", value);
	}

	@Test
	public void testGetDatabasePropValues_02() throws IOException
	{
		String tmpUserDir = System.getProperty("java.io.tmpdir");
		System.setProperty("user.dir", tmpUserDir);
		Path p = FileSystems.getDefault().getPath(tmpUserDir, "limsdatabase.properties");
		writeFile(p, "foo=bar");
		LimsImporterUtil liu = new LimsImporterUtil();
		String value = liu.getDatabasePropValues("foo2");
		assertNull("01", value);
	}

	@Test
	public void testExtractNumber_01()
	{
		assertEquals("01", "", LimsImporterUtil.extractNumber(null));
		assertEquals("02", "", LimsImporterUtil.extractNumber(""));
		assertEquals("03", "", LimsImporterUtil.extractNumber("    "));
		assertEquals("04", "", LimsImporterUtil.extractNumber("a"));
		assertEquals("05", "7", LimsImporterUtil.extractNumber("7"));
		assertEquals("06", "57", LimsImporterUtil.extractNumber("57"));
		//assertEquals("07", "57 ", LimsImporterUtil.extractNumber("57"));
		//assertEquals("08", "57a", LimsImporterUtil.extractNumber("57"));
	}

}
