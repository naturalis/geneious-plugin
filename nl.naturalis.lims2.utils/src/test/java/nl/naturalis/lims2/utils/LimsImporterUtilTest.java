package nl.naturalis.lims2.utils;

import static org.junit.Assert.*;
import static nl.naturalis.lims2.utils.TestUtils.*;

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

}
