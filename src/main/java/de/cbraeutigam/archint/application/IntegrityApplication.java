package de.cbraeutigam.archint.application;

import de.cbraeutigam.archint.hashforest.HashForest;
import de.cbraeutigam.archint.hashforest.HashValue;



/**
 * 
 * @author Christof Bräutigam (christof.braeutigam@cbraeutigam.de)
 * @version $Id: $
 * @since 2014-12-12
 *
 */
public interface IntegrityApplication {
	
	public HashForest<HashValue> compute();

}
