package de.cbraeutigam.archint.gui;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Starter class for the basic integrity test GUI.
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
				ResourceBundle messages = ResourceBundle.getBundle("Messages", Locale.getDefault());
				JFrame frame = new MainFrame(messages.getString("main_frame_title"));
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
