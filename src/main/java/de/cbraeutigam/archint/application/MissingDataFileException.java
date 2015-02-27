package de.cbraeutigam.archint.application;

/**
 * Exception that signals a missing data file, i.e. one (or more) of the files
 * whose integrity information should be computed doesn't exist or is not
 * readable.
 *  
 * @author Christof Bräutigam (christof.braeutigam@cbraeutigam.de)
 * @version $Id: $
 * @since 2015-02-27
 *
 */
public class MissingDataFileException extends Exception {

	private static final long serialVersionUID = -1984226670626242853L;
	
	public MissingDataFileException(String message) {
		super(message);
	}

}
