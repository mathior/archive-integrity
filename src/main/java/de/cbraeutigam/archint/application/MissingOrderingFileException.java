package de.cbraeutigam.archint.application;


public class MissingOrderingFileException extends Exception {

	private static final long serialVersionUID = 5994625156398461520L;
	
	public MissingOrderingFileException(String message) {
		super(message);
	}

}
