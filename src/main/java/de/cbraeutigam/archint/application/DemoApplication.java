package de.cbraeutigam.archint.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.cbraeutigam.archint.hashforest.HashForest;
import de.cbraeutigam.archint.hashforest.HashForest.Mode;
import de.cbraeutigam.archint.hashforest.SHA512HashValue;
import de.cbraeutigam.archint.util.ChecksumProvider;
import de.cbraeutigam.archint.util.Ordering;

public class DemoApplication {

	private final static String ORDERFILENAME = "ord.txt";
	private final static String INTEGRITYFILENAME = "integrity.txt";

	public static boolean checkIntegrity(String baseDir) throws IOException,
			NoSuchAlgorithmException {

		HashForest<SHA512HashValue> givenIntegrityData = readHashForest(baseDir
				+ File.separator + INTEGRITYFILENAME);

		Ordering ordering = readOrdering(baseDir + File.separator
				+ givenIntegrityData.getOrdering());

		HashForest<SHA512HashValue> current = computeHashForest(baseDir,
				ordering.getIdentifiers());

		// return current.validate(givenIntegrityData);
		return givenIntegrityData.validate(current);
	}

	public static boolean checkContains(String baseDir1, String baseDir2)
			throws IOException, NoSuchAlgorithmException {
		
		/*
		 * TODO: for contains checks the ordering information inside the hash
		 * forest doesn't work
		 */
		Ordering ordering1 = readOrdering(baseDir1 + File.separator + ORDERFILENAME);
		HashForest<SHA512HashValue> hf1 = computeHashForest(
				baseDir1,
				ordering1.getIdentifiers().subList(1, ordering1.getIdentifiers().size()));
		
		// ...also currently both hashforests must be recomputed
		Ordering ordering2 = readOrdering(baseDir2 + File.separator + ORDERFILENAME);
		HashForest<SHA512HashValue> hf2 = computeHashForest(
				baseDir2,
				ordering2.getIdentifiers().subList(1, ordering2.getIdentifiers().size()));
		
		return hf1.contains(hf2);
	}

	public static void createIntegrityData(String baseDir, HashForest.Mode mode)
			throws NoSuchAlgorithmException, IOException {
		File orderFile = new File(baseDir + File.separator + ORDERFILENAME);
		if (orderFile.isFile()) {
			orderFile.delete();
		}
		File integrityFile = new File(baseDir + File.separator
				+ INTEGRITYFILENAME);
		if (integrityFile.isFile()) {
			integrityFile.delete();
		}

		List<File> fileTree = listFileTree(new File(baseDir));
		Collections.sort(fileTree);
		Ordering ordering = new Ordering(new ChecksumProvider(
				MessageDigest.getInstance("SHA-512")));
		ordering.add(ORDERFILENAME);
		for (File f : fileTree) {
			ordering.add(f.getAbsolutePath().substring(baseDir.length() + 1));
		}
		FileWriter fw = new FileWriter(orderFile);
		ordering.writeTo(fw);
		fw.close();

		HashForest<SHA512HashValue> hf = computeHashForest(
				baseDir, ordering.getIdentifiers());

		hf.setOrdering(ORDERFILENAME);

		if (mode.equals(Mode.ROOTS)) {
			hf.pruneForest();
		}
		fw = new FileWriter(integrityFile);
		hf.writeTo(fw);
		fw.close();
	}

	private static List<File> listFileTree(File dir) {
		List<File> fileTree = new ArrayList<File>();
		for (File entry : dir.listFiles()) {
			if (entry.isFile()) {
				fileTree.add(entry);
			} else {
				fileTree.addAll(listFileTree(entry));
			}
		}
		return fileTree;
	}

	private static SHA512HashValue getHash(String fileName) throws IOException,
			NoSuchAlgorithmException {
		MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
		int bufferSize = 128;
		byte[] buffer = new byte[bufferSize];
		File f = new File(fileName);
		FileInputStream fis = new FileInputStream(f);
		int bytesRead = fis.read(buffer);
		sha512.update(Arrays.copyOf(buffer, bytesRead));
		while ((bytesRead = fis.read(buffer)) != -1) {
			sha512.update(Arrays.copyOf(buffer, bytesRead));
		}
		fis.close();
		return new SHA512HashValue(sha512.digest());
	}

	public static HashForest<SHA512HashValue> computeHashForest(String baseDir,
			List<String> ordering) throws NoSuchAlgorithmException, IOException {
		HashForest<SHA512HashValue> hf = new HashForest<SHA512HashValue>();
		for (String fileName : ordering) {
			SHA512HashValue hashValue = getHash(baseDir + File.separator
					+ fileName);
			hf.update(hashValue);
		}
		return hf;
	}

	private static HashForest<SHA512HashValue> readHashForest(String fileName)
			throws IOException {
		File integrityFile = new File(fileName);
		if (!integrityFile.isFile()) {
			throw new IOException("No integrity information available!");
		}

		HashForest<SHA512HashValue> hf = new HashForest<SHA512HashValue>();
		FileReader fr = new FileReader(integrityFile);
		hf.readFrom(fr);
		fr.close();
		return hf;
	}

	private static Ordering readOrdering(String fileName) throws IOException,
			NoSuchAlgorithmException {
		File orderFile = new File(fileName);
		if (!orderFile.isFile()) {
			throw new IOException("Ordering information file does not exist: "
					+ fileName);
		}

		Ordering ordering = new Ordering(new ChecksumProvider(
				MessageDigest.getInstance("SHA-512")));
		FileReader fr = new FileReader(orderFile);
		ordering.readFrom(fr);
		fr.close();
		return ordering;
	}

	public static void main(String[] args) throws NoSuchAlgorithmException,
			IOException {
		String homeDir = System.getProperty("user.home");
		String testDirCurrent = homeDir
				+ "/Desktop/projekt-digitales-magazin/testcases";
		String testDirOld = homeDir
				+ "/Desktop/projekt-digitales-magazin/testcases-old";
		createIntegrityData(testDirCurrent, Mode.ROOTS);
		createIntegrityData(testDirOld, Mode.ROOTS);

		System.out.println(checkIntegrity(testDirCurrent));
		System.out.println(checkIntegrity(testDirOld));
		
		System.out.println(checkContains(testDirCurrent, testDirOld));
		
	}

}
