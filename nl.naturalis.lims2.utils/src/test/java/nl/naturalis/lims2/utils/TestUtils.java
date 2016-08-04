package nl.naturalis.lims2.utils;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestUtils {

	private TestUtils()
	{
	}

	public static void writeFile(String path, String contents)
	{
		Path p = FileSystems.getDefault().getPath(path);
		writeFile(p, contents);
	}

	public static void writeFile(Path p, String contents)
	{
		try {
			Files.deleteIfExists(p);
			Files.write(p, contents.getBytes());
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void deleteFile(Path p)
	{
		try {
			Files.deleteIfExists(p);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
