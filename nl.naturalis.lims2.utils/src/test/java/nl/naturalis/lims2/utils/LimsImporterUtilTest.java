package nl.naturalis.lims2.utils;

import static nl.naturalis.lims2.utils.TestUtils.writeFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.junit.Test;

public class LimsImporterUtilTest {

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

}
