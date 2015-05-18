package de.cbraeutigam.archint.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.xml.stream.XMLStreamException;

import de.cbraeutigam.archint.gui.App;
import de.cbraeutigam.archint.hashforest.HashForest;
import de.cbraeutigam.archint.hashforest.HashForest.Mode;
import de.cbraeutigam.archint.hashforest.InvalidInputException;
import de.cbraeutigam.archint.hashforest.SHA512HashValue;
import de.cbraeutigam.archint.util.ChecksumProvider;
import de.cbraeutigam.archint.util.Ordering;

public class CliDemoApplication {

	private enum Option {
		HELP, CREATE, CHECK, CONTAINS
	}

	private class CliOptions {

		private Option option;
		private Mode mode;
		private String path1;
		private String path2;

		public CliOptions(Option option, boolean full, String path1,
				String path2) {
			this.option = option;
			if (full) {
				mode = Mode.FULL;
			} else {
				mode = Mode.ROOTS;
			}
			this.path1 = path1;
			this.path2 = path2;

		}

		public Option getOption() {
			return option;
		}

		public Mode getMode() {
			return mode;
		}

		public String getPath1() {
			return path1;
		}

		public String getPath2() {
			return path2;
		}

	}
	
	
	private final static String MANIFESTFILENAME = "manifest.xml";
	private final static String VALIDMESSAGE = "VALID";
	private final static String INVALIDMESSAGE = "INVALID";
	
	
	/**
	 * Helper function to read ordering either from a manifest.xml or from a
	 * simple text file.
	 * 
	 * @param orderingFilePath
	 * @return
	 * @throws XMLStreamException 
	 * @throws IOException 
	 * @throws MissingOrderingFileException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidInputException 
	 */
	private static List<String> readFileOrdering(String orderingFilePath)
			throws NoSuchAlgorithmException, MissingOrderingFileException,
			IOException, InvalidInputException {
		List<String> fileOrder = new ArrayList<String>();
		// file order should be provided explicitely by an ordering file
		Ordering ordering = readSimpleOrdering(orderingFilePath);
		fileOrder = ordering.getIdentifiers();
		return fileOrder;
	}
	
	/**
	 * Check integrity of the given directory.
	 * @param baseDir
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws MissingOrderingFileException
	 * @throws MissingDataFileException
	 * @throws MissingIntegrityFileException
	 * @throws XMLStreamException 
	 * @throws InvalidInputException 
	 */
	public static boolean checkIntegrity(String baseDir) throws IOException,
			NoSuchAlgorithmException, MissingOrderingFileException,
			MissingDataFileException, MissingIntegrityFileException,
			XMLStreamException, InvalidInputException {

		// read integrity data
		HashForest<SHA512HashValue> givenIntegrityData = 
				readHashForest(baseDir + File.separator + HashForest.INTEGRITYFILENAME);
		
		// read ordering from the file denoted in the integrity data
		String orderingFileName = givenIntegrityData.getOrderingInformationLocation();
		
		File orderingFile =
				new File(baseDir + File.separator + orderingFileName);
		
		if (!orderingFile.isFile() || !orderingFile.canRead()) {
			throw new MissingOrderingFileException(
					"Missing ordering information (should be "
				    + orderingFileName + ")");
		}
		
		List<String> fileOrder =
				readFileOrdering(orderingFile.getAbsolutePath());

		HashForest<SHA512HashValue> current = 
				computeHashForest(baseDir, fileOrder);
		
		// return current.validate(givenIntegrityData);
		return givenIntegrityData.validate(current);
	}

	/**
	 * Check if the first directory is a superset of the second directory.
	 * 
	 * @param baseDir1
	 * @param baseDir2
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws MissingOrderingFileException
	 * @throws MissingDataFileException
	 * @throws XMLStreamException 
	 * @throws InvalidInputException 
	 */
	public static boolean checkContains(String baseDir1, String baseDir2)
			throws IOException, NoSuchAlgorithmException,
			MissingOrderingFileException, MissingDataFileException,
			XMLStreamException, InvalidInputException {

		HashForest<SHA512HashValue> hf1;
		HashForest<SHA512HashValue> hf2;
		List<String> fileOrdering1;
		List<String> fileOrdering2;
		
		/*
		 * detect the type of ordering information, either a manifest.xml or a
		 * text file for both directories and compute and compare the hash
		 * forests
		 */
		
		/*
		 * TODO: for contains checks the ordering information inside the hash
		 * forest doesn't work, so we must remove it from the file ordering
		 * if it is read from a simple ordering file.
		 */
		
		File manifestFile1 =
				new File(baseDir1 + File.separator + MANIFESTFILENAME);
		File orderingFile1 =
				new File(baseDir1 + File.separator + Ordering.ORDERFILENAME);
		
		if (manifestFile1.isFile() && manifestFile1.canRead()) {
			fileOrdering1 = readFileOrdering(manifestFile1.getAbsolutePath());
			hf1 = computeHashForest(baseDir1, fileOrdering1);
		} else if (orderingFile1.isFile() && orderingFile1.canRead()) {
			fileOrdering1 = readFileOrdering(orderingFile1.getAbsolutePath());
			hf1 = computeHashForest(
					baseDir1, fileOrdering1.subList(1, fileOrdering1.size()));
		} else {
			throw new MissingOrderingFileException(
					"Missing ordering information in " + baseDir1);
		}
		
		File manifestFile2 =
				new File(baseDir2 + File.separator + MANIFESTFILENAME);
		File orderingFile2 =
				new File(baseDir2 + File.separator + Ordering.ORDERFILENAME);
		
		if (manifestFile2.isFile() && manifestFile2.canRead()) {
			fileOrdering2 = readFileOrdering(manifestFile2.getAbsolutePath());
			hf2 = computeHashForest(baseDir2, fileOrdering2);
		} else if (orderingFile2.isFile() && orderingFile2.canRead()) {
			fileOrdering2 = readFileOrdering(orderingFile2.getAbsolutePath());
			hf2 = computeHashForest(
					baseDir2, fileOrdering2.subList(1, fileOrdering2.size()));
		} else {
			throw new MissingOrderingFileException(
					"Missing ordering information in " + baseDir2);
		}
		
		return hf1.contains(hf2);
	}

	
	/**
	 * Creates integrity data for a given directory. If a manifest.xml exists in
	 * the directory, the file order given in the manifest is used to create the
	 * hash forest. If no manifest.xml exists, the files will be read in
	 * alphanumeric order and an ordering file will be written.
	 * 
	 * @param baseDir
	 * @param mode
	 * @return
	 * @throws NoSuchAlgorithmException
	 *             if SHA512 hash algorithm is not available
	 * @throws MissingDataFileException
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public static String createIntegrityData(String baseDir,
			HashForest.Mode mode) throws NoSuchAlgorithmException,
			MissingDataFileException, IOException, XMLStreamException {
		
		// this is the create routine, so if an ordering file exists, delete it
		File orderFile = new File(baseDir + File.separator + Ordering.ORDERFILENAME);
		if (orderFile.isFile()) {
			orderFile.delete();
		}
		
		// this is the create routine, so if an integrity file exists, delete it
		File integrityFile = new File(baseDir + File.separator
				+ HashForest.INTEGRITYFILENAME);
		if (integrityFile.isFile()) {
			integrityFile.delete();
		}
		
		/*
		 * check for an existing manifest.xml file that provides the ordering,
		 * if none exists read the files and write a ordering file
		 */
		List<String> fileOrder = new ArrayList<String>();
		
		
		// file order is provided implicitely by file names
		List<File> fileTree = listFileTree(new File(baseDir));
		Collections.sort(fileTree);
		// the ordering must be integrated into the integrity information
		Ordering ordering = new Ordering(
				new ChecksumProvider(MessageDigest.getInstance("SHA-512")));
		ordering.add(Ordering.ORDERFILENAME);
		/*
		 * ordering should use relative paths, therefore use the full path
		 * and cut the full path part of the base dir
		 */
		File baseDirF = new File(baseDir);
		for (File f : fileTree) {
			ordering.add(f.getAbsolutePath().substring(
					baseDirF.getAbsolutePath().length() + 1));
		}
		
		// save the ordering file
		FileWriter fw = new FileWriter(orderFile);
		ordering.writeTo(fw);
		fw.close();
		
		fileOrder = ordering.getIdentifiers();
		
		// create and write the integrity information
		HashForest<SHA512HashValue> hf = computeHashForest(baseDir, fileOrder);
		
		hf.setOrderingInformationLocation(Ordering.ORDERFILENAME);

		if (mode.equals(Mode.ROOTS)) {
			hf.pruneForest();
		}
		fw = new FileWriter(integrityFile);
		hf.writeTo(fw);
		fw.close();
		
		// create and return a status message
		StringBuilder statusMessage = new StringBuilder();
		statusMessage.append("Created integrity information for ");
		statusMessage.append(baseDir);
		statusMessage.append("\n");
		statusMessage.append("Files: ");
		statusMessage.append(fileOrder.size());
		statusMessage.append("\n");
		statusMessage.append("Mode: ");
		statusMessage.append(mode.toString());
		statusMessage.append("\n");
		statusMessage.append("Integrity information written to: ");
		statusMessage.append(integrityFile.getName());
		statusMessage.append("\n");
		statusMessage.append("Ordering information written to: ");
		statusMessage.append(orderFile.getName());
		statusMessage.append("\n");
		return statusMessage.toString();
	}

	/**
	 * Recursively list all files in the given directory.
	 * @param dir
	 * @return
	 */
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
	
//	/**
//	 * Helper method to compute the SHA512 hash value for a given file.
//	 * @param fileName
//	 * @return
//	 * @throws NoSuchAlgorithmException
//	 * @throws MissingDataFileException
//	 */
//	private static SHA512HashValue getHash(String fileName)
//			throws NoSuchAlgorithmException, MissingDataFileException {
//		MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
//		int bufferSize = 128;
//		byte[] buffer = new byte[bufferSize];
//		File f = new File(fileName);
//		FileInputStream fis;
//		
//		try {
//			fis = new FileInputStream(f);
//			
//			int bytesRead = fis.read(buffer);
//			
//			if (bytesRead == -1) {  // file is empty, update with empty message
//				sha512.update(new byte[0]);
//			} else {
//				sha512.update(Arrays.copyOf(buffer, bytesRead));
//			}
//			
//			while ((bytesRead = fis.read(buffer)) != -1) {
//				sha512.update(Arrays.copyOf(buffer, bytesRead));
//			}
//			fis.close();
//		} catch (FileNotFoundException e) {
//			throw new MissingDataFileException("Missing file: " + fileName);
//		} catch (IOException e) {
//			throw new MissingDataFileException("Missing file: " + fileName);
//		}
//		return new SHA512HashValue(sha512.digest());
//	}

	/**
	 * Compute the hash forest for all files in the given directory based on the
	 * given ordering.
	 * @param baseDir
	 * @param ordering
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws MissingDataFileException
	 */
	public static HashForest<SHA512HashValue> computeHashForest(String baseDir,
			List<String> ordering) throws NoSuchAlgorithmException,
			MissingDataFileException {
		HashForest<SHA512HashValue> hf = new HashForest<SHA512HashValue>();
		for (String fileName : ordering) {
			SHA512HashValue hashValue = FileUtil.getHash(baseDir + File.separator
					+ fileName);
			hf.update(hashValue);
		}
		return hf;
	}

	private static HashForest<SHA512HashValue> readHashForest(String fileName)
			throws MissingIntegrityFileException, IOException, InvalidInputException {
		File integrityFile = new File(fileName);
		if (!integrityFile.isFile()) {
			throw new MissingIntegrityFileException("No integrity information available!");
		}

		HashForest<SHA512HashValue> hf = new HashForest<SHA512HashValue>();
		FileReader fr = new FileReader(integrityFile);
		hf.readFrom(fr);
		fr.close();
		return hf;
	}

	/**
	 * Helper function to read a simple ordering text file.
	 * @param fileName
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws MissingOrderingFileException
	 * @throws IOException
	 * @throws InvalidInputException 
	 */
	private static Ordering readSimpleOrdering(String fileName)
			throws NoSuchAlgorithmException, MissingOrderingFileException,
			IOException, InvalidInputException {
		File orderFile = new File(fileName);
		if (!orderFile.isFile()) {
			throw new MissingOrderingFileException(
					"Ordering information file does not exist: " + fileName);
		}

		Ordering ordering = new Ordering(new ChecksumProvider(
				MessageDigest.getInstance("SHA-512")));
		FileReader fr = new FileReader(orderFile);
		ordering.readFrom(fr);
		fr.close();
		if (!ordering.isValid()) {
			throw new MissingOrderingFileException("Invalid data " + fileName);
		}
		return ordering;
	}

	private static void printUsageAndExit(int exitStatus, String errorMessage) {
		String usageMessage = new String(
				"Usage: java -jar demo.jar [OPTIONS] PATHS\n"
						+ "Create integrity information or test integrity of all files in PATH.\n"
						+ "Except for --full only the first option is considered, others are ignored.\n"
						+ "OPTIONS\n"
						+ "  -h  Help (output this text)\n"
						+ "  -c  Create integrity information for PATH.\n"
						+ "  -t  Test integrity information for PATH.\n"
						+ "  -p  Test if the first path is an extended version of the second path.\n"
						+ "      (experimental)\n"
						+ "  --full  Create full integrity information instead of just the roots.\n"
						+ "          Works only with -c, is ignored otherwise.\n");
		if (errorMessage != null && !errorMessage.equals("")) {
			usageMessage = "Error: " + errorMessage + "\n" + usageMessage;
		}
		if (exitStatus == 0) {
			System.out.println(usageMessage);
		} else {
			System.err.println(usageMessage);
		}

		System.exit(exitStatus);
	}

	private static void checkPath(String path) {
		if (path == null) {
			printUsageAndExit(1, "Path argument missing.");
		}

		File pathF = new File(path);

		if (!pathF.isDirectory()) {
			printUsageAndExit(1, "Path is not a directory: " + path);
		}
		if (!pathF.canRead()) {
			printUsageAndExit(1, "Path is not readable: " + path);
		}

	}

	private CliOptions parseArgs(String[] args) {

		Option option = null;
		boolean isFull = false;

		for (int i = args.length - 1; i >= 0; --i) {
			String arg = args[i];
			if (arg.equals("--full")) {
				isFull = true;
			} else if (arg.equals("-h")) {
				option = Option.HELP;
			} else if (arg.equals("-c")) {
				option = Option.CREATE;
			} else if (arg.equals("-t")) {
				option = Option.CHECK;
			} else if (arg.equals("-p")) {
				option = Option.CONTAINS;
			}
		}

		if (option == null) {
			printUsageAndExit(1, "No option given.");
		}

		if (option.equals(Option.HELP)) {
			return new CliOptions(option, isFull, null, null);
		}

		if (option.equals(Option.CONTAINS)) {
			if (args.length < 3) {
				printUsageAndExit(1, "Two paths expected.");
			}
			String path1 = args[args.length - 2];
			String path2 = args[args.length - 1];
			checkPath(path1);
			checkPath(path2);
			return new CliOptions(option, isFull, path1, path2);
		}

		String path = args[args.length - 1];
		checkPath(path);
		return new CliOptions(option, isFull, path, null);
	}
	
	public static void cliDemoMain(String[] args) {
		CliDemoApplication da = new CliDemoApplication();
		CliOptions opts = da.parseArgs(args);

		if (opts.getOption().equals(Option.HELP)) {
			printUsageAndExit(0, null);
		} else if (opts.getOption().equals(Option.CREATE)) {
			try {
				String statusMessage = createIntegrityData(
						opts.getPath1(),
						opts.getMode());
				System.out.println(statusMessage);
			} catch (MissingDataFileException e) {
				System.err.println("Error: Cannot read one or more of the data files!");
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				System.err.println("Error: SHA512 hash algorithm missing!");
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XMLStreamException e) {
				System.err.println("Error: Cannot read manifest.xml or xml file is broken.");
				e.printStackTrace();
			}
		} else if (opts.getOption().equals(Option.CHECK)) {
			boolean valid;
			try {
				valid = checkIntegrity(opts.getPath1());
				if (valid) {
					System.out.println(VALIDMESSAGE);
				} else {
					System.out.println(INVALIDMESSAGE);
				}
			} catch (NoSuchAlgorithmException e) {
				System.err.println("Error: SHA512 hash algorithm not available!");
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (MissingOrderingFileException e) {
				System.err.println("Error: Missing file order information!");
				e.printStackTrace();
			} catch (MissingDataFileException e) {
				System.err.println("Error: Cannot read one or more of the data files!");
				e.printStackTrace();
			} catch (MissingIntegrityFileException e) {
				System.err.println("Error: Missing integrity information!");
				e.printStackTrace();
			} catch (XMLStreamException e) {
				System.err.println("Error: Cannot read manifest.xml or xml file is broken!");
				e.printStackTrace();
			} catch (InvalidInputException e) {
				System.err.println("Error: Invalid integrity or ordering information!");
				e.printStackTrace();
			}
		} else if (opts.getOption().equals(Option.CONTAINS)) {
			try {
				boolean contains = checkContains(opts.getPath1(), opts.getPath2());
				if (contains) {
					System.out.println(opts.getPath1());
					System.out.println("is an extended version of");
					System.out.println(opts.getPath2());
				} else {
					System.out.println(opts.getPath1());
					System.out.println("is NOT an extended version of");
					System.out.println(opts.getPath2());
				}
			} catch (NoSuchAlgorithmException e) {
				System.err.println("Error: SHA512 hash algorithm not available!");
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (MissingOrderingFileException e) {
				System.err.println("Error: Missing file order information!");
				e.printStackTrace();
			} catch (MissingDataFileException e) {
				System.err.println("Error: Cannot read one or more of the data files!");
				e.printStackTrace();
			} catch (XMLStreamException e) {
				System.err.println("Error: Cannot read manifest.xml or xml file is broken!");
				e.printStackTrace();
			} catch (InvalidInputException e) {
				System.err.println("Error: Invalid ordering information!");
				e.printStackTrace();
			}
		}
	}
	
	private static void bugfixMain() {
		// String homeDir = System.getProperty("user.home");
		// String testDirCurrent = homeDir
		// + "/Desktop/projekt-digitales-magazin/testcases";
		// String testDirOld = homeDir
		// + "/Desktop/projekt-digitales-magazin/testcases-old";
		//
		// args = new String[] {"-c", "--full", testDirCurrent};

		

		// createIntegrityData(testDirCurrent, Mode.ROOTS);
		// createIntegrityData(testDirOld, Mode.ROOTS);
		//
		// System.out.println(checkIntegrity(testDirCurrent));
		// System.out.println(checkIntegrity(testDirOld));
		//
		// System.out.println(checkContains(testDirCurrent, testDirOld));
		
		String baseDir = "";
		
		String statusMessage = null;
		try {
			statusMessage = createIntegrityData(baseDir, Mode.FULL);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MissingDataFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(statusMessage);
		
		
	}

	public static void main(String[] args) {
		//bugfixMain();
	}

}
