package de.cbraeutigam.archint.gui;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Insets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.cbraeutigam.archint.application.FileUtil;
import de.cbraeutigam.archint.application.MissingDataFileException;
import de.cbraeutigam.archint.hashforest.Const;
import de.cbraeutigam.archint.hashforest.HashForest;
import de.cbraeutigam.archint.hashforest.InvalidInputException;
import de.cbraeutigam.archint.hashforest.SHA512HashValue;
import de.cbraeutigam.archint.util.ChecksumProvider;
import de.cbraeutigam.archint.util.Ordering;


/**
 * Controller class that connects the main components.
 * 
 * @author Christof Bräutigam (christof.braeutigam@cbraeutigam.de)
 * @version $Id: $
 * @since 2015-03-30
 *
 */
public class MainFrame extends JFrame {
	
	private static final long serialVersionUID = -4024436092995281043L;
	
	private final static String ERROR_CANT_READ_INTEGRITY = "Error: cannot read integrity information!";
	private final static String ERROR_INVALID_INTEGRITY = "Error: integrity information invalid!";
	private final static String ERROR_CANT_READ_ORDERING =  "Error: cannot read ordering information!";
	private final static String ERROR_INVALID_ORDERING = "Error: ordering information invalid!";
	private final static String ERROR_CANT_READ_DATAFILE =  "Error: cannot read data file ";
	
	private final static String VALID =  "VALID";
	private final static String INVALID =  "INVALID";
	
	
	private JTextArea textArea;
	private JProgressBar progressBar;

	public MainFrame(String title) {
		super(title);
		
		// set layout manager
		
		setLayout(new BorderLayout());
		
		// create swing components
		
		textArea = new JTextArea();
		textArea.setMargin(new Insets(5,5,5,5));
		textArea.setEditable(false);
		
		progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
		
		
		// add swing components to content pane
		
		Container c = getContentPane();
		c.add(progressBar, BorderLayout.NORTH);
		c.add(new JScrollPane(textArea), BorderLayout.CENTER);
		
		
		
		
		
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select DIP directory");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File dir = fc.getSelectedFile();
            processDip(dir);
        } else {
        	textArea.append("No directory chosen." + Const.NEWLINE);
        }
		
	}
	
	private void processDip(File dir) {
		File integrityFile = new File(dir, HashForest.INTEGRITYFILENAME);
		File orderingFile = new File(dir, Ordering.ORDERFILENAME);
		
		if (!integrityFile.isFile() || !integrityFile.canRead()) {
			textArea.append(ERROR_CANT_READ_INTEGRITY);
			return;
		}
		
		if (!orderingFile.isFile() || !orderingFile.canRead()) {
			textArea.append(ERROR_CANT_READ_ORDERING);
			return;
		}
		
		textArea.append("Reading integrity file" + Const.NEWLINE);
		
		HashForest<SHA512HashValue> hf = new HashForest<SHA512HashValue>();
		try {
			hf.readFrom(new FileReader(integrityFile));
		} catch (FileNotFoundException e) {
			textArea.append(ERROR_CANT_READ_INTEGRITY);
			return;
		} catch (IOException e) {
			textArea.append(ERROR_CANT_READ_INTEGRITY);
			return;
		} catch (InvalidInputException e) {
			textArea.append(ERROR_INVALID_INTEGRITY);
			return;
		}
		
		textArea.append("Reading ordering file" + Const.NEWLINE);
		
		Ordering ordering = null;
		try {
			ordering = new Ordering(new ChecksumProvider(MessageDigest.getInstance("SHA-512")));
			ordering.readFrom(new FileReader(orderingFile));
		} catch (NoSuchAlgorithmException e) {
			textArea.append(ERROR_CANT_READ_ORDERING);
			return;
		} catch (FileNotFoundException e) {
			textArea.append(ERROR_CANT_READ_ORDERING);
			return;
		} catch (IOException e) {
			textArea.append(ERROR_CANT_READ_ORDERING);
			return;
		} catch (InvalidInputException e) {
			textArea.append(ERROR_INVALID_ORDERING);
			return;
		}
		
		int filesProcessed = 0;
		int numFiles = ordering.getIdentifiers().size();
		HashForest<SHA512HashValue> hfNew = new HashForest<SHA512HashValue>();
		for (String fn : ordering.getIdentifiers()) {
			textArea.append("Reading: " + fn + Const.NEWLINE);
			int progress = Math.min((filesProcessed/numFiles) * 100, 100);
			progressBar.setValue(progress);
			
			SHA512HashValue hash;
			try {
				hash = FileUtil.getHash(new File(dir, fn).getAbsolutePath());
			} catch (NoSuchAlgorithmException e) {
				textArea.append(ERROR_CANT_READ_DATAFILE + fn);
				return;
			} catch (MissingDataFileException e) {
				textArea.append(ERROR_CANT_READ_DATAFILE + fn);
				return;
			}
			hfNew.update(hash);
			filesProcessed += 1;
		}
		progressBar.setValue(100);
		
		if (hf.validate(hfNew)) {
			textArea.append(VALID);
		} else {
			textArea.append(INVALID);
		}
		
	}

}
