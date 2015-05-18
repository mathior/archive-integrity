package de.cbraeutigam.archint.experiment;

import java.util.Locale;
import java.util.ResourceBundle;

public class Msg {
	
	private static ResourceBundle errorMsg = 
			ResourceBundle.getBundle("ErrorMessages", Locale.getDefault());

	public static void main(String[] args) {
		System.out.println(errorMsg.getString("cant_read_integrity"));
	}

}
