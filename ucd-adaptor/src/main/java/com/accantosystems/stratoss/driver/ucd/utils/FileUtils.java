package com.accantosystems.stratoss.driver.ucd.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FileUtils {
	
	private FileUtils() {
		super();
	}
	
	public static String loadFileIntoString(final String fileName) throws IOException {
		// This appears to be the fastest way to load a file into a String (circa 2017 (JDK8))
		// http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
		
		try (ByteArrayOutputStream result = new ByteArrayOutputStream();
			 InputStream inputStream = FileUtils.class.getResourceAsStream("/" + fileName); ) {
			
			if ( inputStream == null ) {
				throw new FileNotFoundException(String.format("Cannot find file [%s]", fileName));
			}
			
			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) != -1) {
			    result.write(buffer, 0, length);
			}
			return result.toString(StandardCharsets.UTF_8.name());
		}
	}

}
