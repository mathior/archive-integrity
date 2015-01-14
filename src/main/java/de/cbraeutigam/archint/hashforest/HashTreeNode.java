package de.cbraeutigam.archint.hashforest;




/**
 * 
 * @author Christof Bräutigam (christof.braeutigam@cbraeutigam.de)
 * @version $Id: $
 * @since 2014-12-12
 *
 * @param <T>
 */
public class HashTreeNode<T extends HashValue> {
	
	private final T value;
	HashTreeNode<T> leftChild;
	HashTreeNode<T> rightChild;
	
	public HashTreeNode(T hashValue) {
		this.value = hashValue;
	}

	public HashTreeNode<T> getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(HashTreeNode<T> leftChild) {
		this.leftChild = leftChild;
	}

	public HashTreeNode<T> getRightChild() {
		return rightChild;
	}

	public void setRightChild(HashTreeNode<T> rightChild) {
		this.rightChild = rightChild;
	}

	public T getValue() {
		return value;
	}

}
