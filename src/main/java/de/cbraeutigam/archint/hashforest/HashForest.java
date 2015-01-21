package de.cbraeutigam.archint.hashforest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.cbraeutigam.archint.util.ByteSerializable;
import de.cbraeutigam.archint.util.ChecksumProvider;
import de.cbraeutigam.archint.util.DateProvider;
import de.cbraeutigam.archint.util.TextSerializable;

/**
 * 
 * @author Christof Bräutigam (christof.braeutigam@cbraeutigam.de)
 * @version $Id: $
 * @since 2014-12-12
 *
 * @param <T>
 */
public class HashForest<T extends HashValue>
implements TextSerializable, ByteSerializable {
	
	public enum Mode {
		FULL("full"),
		ROOTS("roots");
		
		private String modeString;
		
		public String toString() {
			return modeString;
		};
		
		private Mode(String s) {
			this.modeString = s;
		}
		
		public static Mode fromString(String s) {
			if (s.equals(Mode.FULL.modeString)) {
				return Mode.FULL;
			} else if (s.equals(Mode.ROOTS.modeString)) {
				return Mode.ROOTS;
			} else {
				return Mode.ROOTS;  // TODO: add safe default case
			}
		}
	}
	
	private static final long serialVersionUID = 4159661696848135993L;
	
	private int version = 1;
	private String ordering = "implicit";
	
	//private List<HashTreeNode<T>> hashTrees = new ArrayList<HashTreeNode<T>>();
	private List<T> leafs = new ArrayList<T>();
	private List<T[]> trees = new ArrayList<T[]>();
	
	// needed for HashForests in root mode
	private int leafsCount = 0;
	private int treesCount = 0;
	
	private boolean isDirty = false;
	private Mode mode = Mode.FULL;
	
	private void checkIsDirty() {
		if (isDirty && mode.equals(Mode.FULL)) {
			trees = createForest(leafs);
			leafsCount = leafs.size();
			treesCount = trees.size();
			isDirty = false;
		}
	}
	
	public boolean isEmpty() {
		return leafs.isEmpty();
	}
	
	private int[] computeLeafsPerTree(int leafsCount) {
		// compute numer of complete trees by counting the 1-bits in the
		// two-complement-representation
		int trees = Integer.bitCount(leafsCount);
		int[] treeSizes = new int[trees];
		
		int exp = 0;
		int idx = trees - 1;
		
		while (idx >= 0) {
			if ( ((1<<exp) & leafsCount) != 0) {
				treeSizes[idx] = 1<<exp;
				--idx;
			}
			++exp;
		}
		return treeSizes;
	}
	
	@SuppressWarnings("unchecked")
	private T[] createTree(List<T> leafs) {
		
		// a complete tree with n leafes has exact 2*n - 1 nodes
		int treeSize = 2 * leafs.size() - 1;
		HashValue[] tree = new HashValue[treeSize];
		for (int treeIdx = treeSize - 1, leafIdx = leafs.size() - 1;
				leafIdx >= 0;
				--treeIdx, --leafIdx) {
			tree[treeIdx] = leafs.get(leafIdx);
		}
		// in the array-based (0-based index) representation of a binary tree a
		// parent at index n has the children at 2*n+1 (left) and 2*n+2 (right)
		for (int treeIdx = treeSize - leafs.size() - 1;
				treeIdx >= 0;
				--treeIdx) {
			tree[treeIdx] = tree[2*treeIdx+1].concatenate(tree[2*treeIdx+2]);
		}
		return (T[]) tree;
	}
	
	private List<T[]> createForest(List<T> leafs) {
		int[] leafesPerTree = computeLeafsPerTree(leafs.size());
		
		List<T[]> forest = new ArrayList<T[]>();
		
		int startIdx = 0;
		for (int leafSize : leafesPerTree) {
			forest.add(createTree(leafs.subList(startIdx, startIdx + leafSize)));
			startIdx += leafSize;
		}
		
		return forest;
	}
	
	
	public void update(T hashValue) {
		leafs.add(hashValue);
		isDirty = true;
	}
	
	public List<T> getLeafs() {
		return Collections.unmodifiableList(leafs);
	}
	
	public List<T> getRoots() {
		checkIsDirty();
		List<T> roots = new ArrayList<T>();
		for (T[] tree : trees) {
			roots.add(tree[0]);
		}
		return roots;
	}
	
	public List<T[]> getTrees() {
		checkIsDirty();
		List<T[]> treesNew = new ArrayList<T[]>();
		for (T[] tree : trees) {
			treesNew.add(tree.clone());
		}
		return treesNew;
	}
	
	
	/*
	 * in this case just compare the roots, because the trees my be pruned
	 */
	public boolean validate(HashForest<T> other) {
		checkIsDirty();
		List<T> roots = getRoots();
		List<T> otherRoots = other.getRoots();	
		return roots.equals(otherRoots);
	}
	
	
	
	private boolean contains(T[] haystack, T needle) {
		boolean result = false;
		for (T node : haystack) {
			if (needle.equals(node)) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	public boolean contains(HashForest<T> other) {
		checkIsDirty();
		if (validate(other)) {
			return true;
		}
		
		List<T[]> trees = getTrees();
		List<T> otherRoots = other.getRoots();
		
		boolean result = true;
		for (T root : otherRoots) {
			boolean found = false;
			for (T[] tree : trees) {
				if (contains(tree, root)) {
					found = true;
					break;
				}
			}
			result &= found;
			if (!result) {
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * Set mode to just output the roots. This setting cannot be reversed.
	 * By keeping only the roots information a forest cannot be extended in the
	 * future, but contains() and validate() are supported.
	 */
	public void pruneForest() {
		checkIsDirty();  // recreate trees one last time
		this.mode = Mode.ROOTS;
	}
	
	public Mode getMode() {
		return mode;
	}
	
	public void setOrdering(String ordering) {
		this.ordering = ordering;
	}
	
	public String getOrdering() {
		return ordering;
	}
	
	private void updateChecksum(ChecksumProvider cp, String field, String value) {
		cp.update(field.getBytes(Charset.forName("UTF-8")));
		cp.update(value.getBytes(Charset.forName("UTF-8")));
	}
	
	private void writeChecked(Writer w, ChecksumProvider cp, String field, String value)
			throws IOException {
		updateChecksum(cp, field, value);
		w.write(field);
		w.write(Const.SEPARATOR);
		w.write(value);
		w.write(Const.NEWLINE);
	}
	
	@Override
	public void writeTo(Writer w) throws IOException {
		checkIsDirty();
		ChecksumProvider cp = null;
		try {
			cp = new ChecksumProvider(MessageDigest.getInstance("SHA-512"));
		} catch (NoSuchAlgorithmException e) {
			throw new IOException("Could not instatiate checksum provider");
		}
		
		writeChecked(w, cp, Const.VERSION, Integer.toString(version));
		String dateFormattet = DateProvider.date2String(new Date());
		writeChecked(w, cp, Const.DATE, dateFormattet);
		writeChecked(w, cp, Const.LEAFS, Integer.toString(leafsCount));
		writeChecked(w, cp, Const.TREES, Integer.toString(treesCount));
		writeChecked(w, cp, Const.ORDER, ordering);
		writeChecked(w, cp, Const.MODE, mode.toString());
		
		if (mode.equals(Mode.ROOTS)) {
			for (T[] tree : trees) {
				writeChecked(w, cp, Const.ROOT, tree[0].getHexString());
			}
		} else {
			for (T hashValue : leafs) {
				writeChecked(w, cp, Const.LEAF, hashValue.getHexString());
			}
		}
		
		String checksum = cp.get();
		w.write(Const.CHECKSUM);
		w.write(Const.SEPARATOR);
		w.write(checksum);
		w.write(Const.NEWLINE);
	}
	
	
	private String readChecked(ChecksumProvider cp, String line,
			String expectedField) throws InvalidInputException {
		String[] parts = line.split(Const.SEPARATOR);
		if (parts[0].equals(expectedField)) {
			updateChecksum(cp, parts[0], parts[1]);
			return parts[1];
		} else {
			throw new InvalidInputException(
					"Expected " + expectedField + ", got " + parts[0]);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void readFrom(Reader r) throws IOException {
		leafs = new ArrayList<T>();
		trees = new ArrayList<T[]>();
		
		ChecksumProvider cp = null;
		try {
			cp = new ChecksumProvider(MessageDigest.getInstance("SHA-512"));
		} catch (NoSuchAlgorithmException e) {
			throw new IOException("Could not instatiate checksum provider!");
		}
		BufferedReader br = new BufferedReader(r);
		String line = br.readLine();
		String value = readChecked(cp, line, Const.VERSION);
		version = Integer.parseInt(value);
		
		line = br.readLine();
		value = readChecked(cp, line, Const.DATE);
		
		line = br.readLine();
		value = readChecked(cp, line, Const.LEAFS);
		leafsCount = Integer.parseInt(value);
		
		line = br.readLine();
		value = readChecked(cp, line, Const.TREES);
		treesCount = Integer.parseInt(value);
		
		line = br.readLine();
		value = readChecked(cp, line, Const.ORDER);
		ordering = value;
		
		line = br.readLine();
		value = readChecked(cp, line, Const.MODE);
		mode = Mode.fromString(value);
		
		try {
			if (mode.equals(Mode.ROOTS)) {
				for (int i = 0; i < treesCount; ++i) {
					line = br.readLine();
					value = readChecked(cp, line, Const.ROOT);
					HashValue hashValue = new SHA512HashValue(value);
					HashValue[] tree = new HashValue[]{hashValue};
					trees.add((T[]) tree);
				}
			} else if (mode.equals(Mode.FULL)) {
				for (int i = 0; i < leafsCount; ++i) {
					line = br.readLine();
					value = readChecked(cp, line, Const.LEAF);
					HashValue hashValue = new SHA512HashValue(value);
					leafs.add((T) hashValue);
				}
			}
		} catch (NoSuchAlgorithmException e) {
			throw new IOException("Could not instantiate message "
					+ "digest algorithm for SHA512!");
		}
		
		String computedChecksum = cp.get();
		line = br.readLine();
		String[] parts = line.split(Const.SEPARATOR);
		if (!parts[0].equals(Const.CHECKSUM)
				|| !parts[1].equals(computedChecksum)) {
			throw new InvalidInputException(
					"Invalid checksum for integrity data!");
		}
		
		isDirty = true;
	}
	
	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		try {
			writeTo(sw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sw.toString();
	}

	@Override
	public void writeTo(OutputStream os) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public void readFrom(InputStream is) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not yet implemented");
	}

}
