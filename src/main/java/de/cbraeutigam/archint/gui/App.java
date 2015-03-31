package de.cbraeutigam.archint.gui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * 
 * 
 * @author Christof Bräutigam (christof.braeutigam@cbraeutigam.de)
 * @version $Id: $
 * @since 2015-03-30
 * 
 */
public class App {

	public void run() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame frame = new MainFrame("DIP integrity check");
				frame.setSize(500, 400);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}

	public static void main(String[] args) {
		App app = new App();
		app.run();
	}

}
