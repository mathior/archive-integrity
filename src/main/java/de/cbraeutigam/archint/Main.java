package de.cbraeutigam.archint;

import de.cbraeutigam.archint.application.CliDemoApplication;
import de.cbraeutigam.archint.gui.App;

public class Main {

	public static void main(String[] args) {
		// Locale.setDefault(new Locale("de", "DE"));
		if (0 == args.length) {
			App app = new App();
			app.run();
		} else {
			CliDemoApplication.cliDemoMain(args);
		}
	}

}
