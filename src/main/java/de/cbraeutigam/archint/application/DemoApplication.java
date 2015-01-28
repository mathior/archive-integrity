package de.cbraeutigam.archint.application;

import java.io.BufferedReader;
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

import de.cbraeutigam.archint.hashforest.HashForest;
import de.cbraeutigam.archint.hashforest.HashForest.Mode;
import de.cbraeutigam.archint.hashforest.SHA512HashValue;
import de.cbraeutigam.archint.util.ChecksumProvider;
import de.cbraeutigam.archint.util.Ordering;

public class DemoApplication {

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

	private final static String ORDERFILENAME = "ord.txt";
	private final static String INTEGRITYFILENAME = "integrity.txt";
	private final static String PREMADEORDERINGFILENAME = "demofilelist.txt";
	private final static String VALIDMESSAGE = "VALID";
	private final static String INVALIDMESSAGE = "INVALID";

	public static boolean checkIntegrity(String baseDir) throws IOException,
			NoSuchAlgorithmException, MissingOrderingFileException,
			MissingDataFileException, MissingIntegrityFileException {

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
			throws IOException, NoSuchAlgorithmException,
			MissingOrderingFileException, MissingDataFileException {

		/*
		 * TODO: for contains checks the ordering information inside the hash
		 * forest doesn't work
		 */
		Ordering ordering1 = readOrdering(baseDir1 + File.separator
				+ ORDERFILENAME);
		HashForest<SHA512HashValue> hf1 = computeHashForest(baseDir1, ordering1
				.getIdentifiers().subList(1, ordering1.getIdentifiers().size()));

		// ...also currently both hashforests must be recomputed
		Ordering ordering2 = readOrdering(baseDir2 + File.separator
				+ ORDERFILENAME);
		HashForest<SHA512HashValue> hf2 = computeHashForest(baseDir2, ordering2
				.getIdentifiers().subList(1, ordering2.getIdentifiers().size()));

		return hf1.contains(hf2);
	}

	public static String createIntegrityData(String baseDir,
			HashForest.Mode mode) throws NoSuchAlgorithmException,
			MissingDataFileException, IOException {
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

		// TODO: This is a hack for the demo to provide a premade ordering
		File premadeOrdering = new File(baseDir + File.separator
				+ PREMADEORDERINGFILENAME);
		if (premadeOrdering.isFile() && premadeOrdering.canRead()) {
			BufferedReader br = new BufferedReader(new FileReader(
					premadeOrdering));
			String line;
			while (null != (line = br.readLine())) {
				if (line.trim().length() > 0) {
					ordering.add(line);
				}
			}
			br.close();
		} else {
			/*
			 * ordering should use relative paths, therefore use the full path
			 * and cut the full path part of the base dir
			 */
			File baseDirF = new File(baseDir);
			for (File f : fileTree) {
				ordering.add(f.getAbsolutePath().substring(
						baseDirF.getAbsolutePath().length() + 1));
			}
		}

		FileWriter fw = new FileWriter(orderFile);
		ordering.writeTo(fw);
		fw.close();

		HashForest<SHA512HashValue> hf = computeHashForest(baseDir,
				ordering.getIdentifiers());

		hf.setOrdering(ORDERFILENAME);

		if (mode.equals(Mode.ROOTS)) {
			hf.pruneForest();
		}
		fw = new FileWriter(integrityFile);
		hf.writeTo(fw);
		fw.close();

		StringBuilder statusMessage = new StringBuilder();
		statusMessage.append("Created integrity information for ");
		statusMessage.append(baseDir);
		statusMessage.append("\n");
		statusMessage.append("Files: ");
		statusMessage.append(ordering.getIdentifiers().size());
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

	private static SHA512HashValue getHash(String fileName)
			throws NoSuchAlgorithmException, MissingDataFileException {
		MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
		int bufferSize = 128;
		byte[] buffer = new byte[bufferSize];
		File f = new File(fileName);
		FileInputStream fis;
		try {
			fis = new FileInputStream(f);

			int bytesRead = fis.read(buffer);
			sha512.update(Arrays.copyOf(buffer, bytesRead));
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

	public static HashForest<SHA512HashValue> computeHashForest(String baseDir,
			List<String> ordering) throws NoSuchAlgorithmException,
			MissingDataFileException {
		HashForest<SHA512HashValue> hf = new HashForest<SHA512HashValue>();
		for (String fileName : ordering) {
			SHA512HashValue hashValue = getHash(baseDir + File.separator
					+ fileName);
			hf.update(hashValue);
		}
		return hf;
	}

	private static HashForest<SHA512HashValue> readHashForest(String fileName)
			throws MissingIntegrityFileException, IOException {
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

	private static Ordering readOrdering(String fileName)
			throws NoSuchAlgorithmException, MissingOrderingFileException,
			IOException {
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

	public static void main(String[] args) {

		// String homeDir = System.getProperty("user.home");
		// String testDirCurrent = homeDir
		// + "/Desktop/projekt-digitales-magazin/testcases";
		// String testDirOld = homeDir
		// + "/Desktop/projekt-digitales-magazin/testcases-old";
		//
		// args = new String[] {"-c", "--full", testDirCurrent};

		DemoApplication da = new DemoApplication();
		CliOptions opts = da.parseArgs(args);

		if (opts.getOption().equals(Option.HELP)) {
			printUsageAndExit(0, null);
		} else if (opts.getOption().equals(Option.CREATE)) {
			try {
				String statusMessage = createIntegrityData(opts.getPath1(),
						opts.getMode());
				System.out.println(statusMessage);
			} catch (MissingDataFileException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IOException e) {
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
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println(INVALIDMESSAGE);
				e.printStackTrace();
			} catch (MissingOrderingFileException e) {
				System.out.println(INVALIDMESSAGE);
				e.printStackTrace();
			} catch (MissingDataFileException e) {
				System.out.println(INVALIDMESSAGE);
				e.printStackTrace();
			} catch (MissingIntegrityFileException e) {
				System.out.println(INVALIDMESSAGE);
				e.printStackTrace();
			}
		} else if (opts.getOption().equals(Option.CONTAINS)) {
			try {
				boolean contains = checkContains(opts.getPath1(), opts.getPath2());
				if (contains) {
					System.out.println("VALID");
					System.out.println(opts.getPath1());
					System.out.println("is an extended version of");
					System.out.println(opts.getPath2());
				} else {
					System.out.println("INVALID");
					System.out.println(opts.getPath1());
					System.out.println("is NOT an extended version of");
					System.out.println(opts.getPath2());
				}
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (MissingOrderingFileException e) {
				System.out.println(INVALIDMESSAGE);
				e.printStackTrace();
			} catch (MissingDataFileException e) {
				System.out.println(INVALIDMESSAGE);
				e.printStackTrace();
			}
		}

		// createIntegrityData(testDirCurrent, Mode.ROOTS);
		// createIntegrityData(testDirOld, Mode.ROOTS);
		//
		// System.out.println(checkIntegrity(testDirCurrent));
		// System.out.println(checkIntegrity(testDirOld));
		//
		// System.out.println(checkContains(testDirCurrent, testDirOld));

	}

}
