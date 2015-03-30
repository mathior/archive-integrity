package de.cbraeutigam.archint.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import de.cbraeutigam.archint.hashforest.SHA512HashValue;

/**
 * Helper class for file hashing.
 * 
 * @author Christof Bräutigam (christof.braeutigam@cbraeutigam.de)
 * @version $Id: $
 * @since 2015-03-30
 *
 */
public class FileUtil {

	/**
	 * Helper method to compute the SHA512 hash value for a given file.
	 * @param fileName
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws MissingDataFileException
	 */
	public static SHA512HashValue getHash(String fileName)
			throws NoSuchAlgorithmException, MissingDataFileException {
		MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
		int bufferSize = 128;
		byte[] buffer = new byte[bufferSize];
		File f = new File(fileName);
		FileInputStream fis;
		
		try {
			fis = new FileInputStream(f);
			
			int bytesRead = fis.read(buffer);
			
			if (bytesRead == -1) {  // file is empty, update with empty message
				sha512.update(new byte[0]);
			} else {
				sha512.update(Arrays.copyOf(buffer, bytesRead));
			}
			
			while ((bytesRead = fis.read(buffer)) != -1) {
				sha512.update(Arrays.copyOf(buffer, bytesRead));
			}
			fis.close();
		} catch (FileNotFoundException e) {
			throw new MissingDataFileException("Missing file: " + fileName);
		} catch (IOException e) {
			throw new MissingDataFileException("Missing file: " + fileName);
		}
		return new SHA512HashValue(sha512.digest());
	}

}
