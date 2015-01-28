package de.cbraeutigam.archint.application;


public class MissingIntegrityFileException extends Exception {

	private static final long serialVersionUID = 9004646738034860997L;
	
	public MissingIntegrityFileException(String message) {
		super(message);
	}

}
