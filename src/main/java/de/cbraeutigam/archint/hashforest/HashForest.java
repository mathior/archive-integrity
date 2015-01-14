package de.cbraeutigam.archint.hashforest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.cbraeutigam.archint.util.ByteSerializable;
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
	
	private static final long serialVersionUID = 4159661696848135993L;
	
	//private List<HashTreeNode<T>> hashTrees = new ArrayList<HashTreeNode<T>>();
	private List<T> leafs = new ArrayList<T>();
	private List<T[]> trees = new ArrayList<T[]>();
	
	private boolean isDirty = false;
	
	private void checkIsDirty() {
		if (isDirty) {
			trees = createForest(leafs);
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
	
	@Override
	public void writeTo(Writer w) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public void readFrom(Reader r) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not yet implemented");
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
