package de.cbraeutigam.archint.hashforest;

import java.io.IOException;

public class InvalidInputException extends IOException {

	private static final long serialVersionUID = -5655068480224494695L;
	
	public InvalidInputException(String message) {
		super(message);
	}

}
