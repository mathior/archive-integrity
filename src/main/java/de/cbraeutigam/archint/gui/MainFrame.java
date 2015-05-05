package de.cbraeutigam.archint.gui;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import de.cbraeutigam.archint.application.FileUtil;
import de.cbraeutigam.archint.application.MissingDataFileException;
import de.cbraeutigam.archint.hashforest.Const;
import de.cbraeutigam.archint.hashforest.HashForest;
import de.cbraeutigam.archint.hashforest.InvalidInputException;
import de.cbraeutigam.archint.hashforest.SHA512HashValue;
import de.cbraeutigam.archint.util.ChecksumProvider;
import de.cbraeutigam.archint.util.Ordering;


/**
 * Main class for the basic integrity test GUI. Defines and connects the
 * components, and implements a worker thread that does the actual integrity
 * test computation. 
 * 
 * @author Christof Bräutigam (christof.braeutigam@cbraeutigam.de)
 * @version $Id: $
 * @since 2015-03-30
 *
 */
public class MainFrame extends JFrame
implements ActionListener, PropertyChangeListener {
	
	private static final long serialVersionUID = -4024436092995281043L;
	
	private final static String ERROR_CANT_READ_INTEGRITY = "Error: cannot read integrity information!";
	private final static String ERROR_INVALID_INTEGRITY = "Error: integrity information invalid!";
	private final static String ERROR_CANT_READ_ORDERING =  "Error: cannot read ordering information!";
	private final static String ERROR_INVALID_ORDERING = "Error: ordering information invalid!";
	private final static String ERROR_CANT_READ_DATAFILE =  "Error: cannot read data file ";
	private final static String ERROR_MISSING_DATAFILE =  "Error: missing data file ";
	private final static String ERROR_MISSING_ALGORITHM =  "Error: this Java installation is missing a SHA512 implementation!";
	
	private final static String VALID =  "VALID";
	private final static String INVALID =  "INVALID";
	
	private JTextArea textArea;
	private JProgressBar progressBar;
	private JFileChooser fc;
	private Task task;
	
	/**
	 * Main working thread.
	 * 
	 * @author Christof Bräutigam (christof.braeutigam@cbraeutigam.de)
	 * @version $Id: $
	 * @since 2015-03-30
	 *
	 */
	class Task extends SwingWorker<Boolean, String> {
		
		private File dir;
		
		public Task(File dir) {
			this.dir = dir;
		}

		@Override
		protected Boolean doInBackground() throws Exception {
			boolean isValid = processDip();
			return isValid;
		}
		
		@Override
		protected void process(List<String> chunks) {
			for (String s : chunks) {
				textArea.append(s);
			}
		}
		
		/**
		 * Convenience method to handle errors which abort the process.
		 * @param abortMessage
		 */
		private void abort(String abortMessage) {
			publish(abortMessage + Const.NEWLINE);
			setProgress(100);
			publish(INVALID);
		}
		
		private boolean processDip() {
			File integrityFile = new File(dir, HashForest.INTEGRITYFILENAME);
			File orderingFile = new File(dir, Ordering.ORDERFILENAME);
			
			if (!integrityFile.isFile() || !integrityFile.canRead()) {
				publish(ERROR_CANT_READ_INTEGRITY);
				return false;
			}
			
			if (!orderingFile.isFile() || !orderingFile.canRead()) {
				publish(ERROR_CANT_READ_ORDERING);
				return false;
			}
			
			publish("Reading integrity information" + Const.NEWLINE);
			
			HashForest<SHA512HashValue> hf = new HashForest<SHA512HashValue>();
			try {
				hf.readFrom(new FileReader(integrityFile));
			} catch (FileNotFoundException e) {
				publish(ERROR_CANT_READ_INTEGRITY);
				return false;
			} catch (IOException e) {
				publish(ERROR_CANT_READ_INTEGRITY);
				return false;
			} catch (InvalidInputException e) {
				publish(ERROR_INVALID_INTEGRITY);
				return false;
			}
			
			publish("Reading ordering information" + Const.NEWLINE);
			
			Ordering ordering = null;
			try {
				ordering = new Ordering(new ChecksumProvider(MessageDigest.getInstance("SHA-512")));
				ordering.readFrom(new FileReader(orderingFile));
			} catch (NoSuchAlgorithmException e) {
				publish(ERROR_MISSING_ALGORITHM);
				return false;
			} catch (FileNotFoundException e) {
				publish(ERROR_CANT_READ_ORDERING);
				return false;
			} catch (IOException e) {
				publish(ERROR_CANT_READ_ORDERING);
				return false;
			} catch (InvalidInputException e) {
				publish(ERROR_INVALID_ORDERING);
				return false;
			}
			
			// Check existence of all data files before processessing all of
			// them and aborting on the last file because it's missing.
			for (String fn : ordering.getIdentifiers()) {
				File file = new File(dir, fn);
				if (!file.isFile()) {
					abort(ERROR_MISSING_DATAFILE + fn);
					return false;
				}
			}
			
			double filesProcessed = 0.0;
			int numFiles = ordering.getIdentifiers().size();
			publish("Processing " + numFiles + " data files" + Const.NEWLINE);
			HashForest<SHA512HashValue> hfNew = new HashForest<SHA512HashValue>();
			for (String fn : ordering.getIdentifiers()) {
				// NOTE: progress is computed by the number of processed files,
				// not the amount of processed bytes
				int progress = Math.min((int)((filesProcessed/numFiles) * 100), 100);
				setProgress(progress);
				
				SHA512HashValue hash;
				try {
					hash = FileUtil.getHash(new File(dir, fn).getAbsolutePath());
				} catch (NoSuchAlgorithmException e) {
					abort(ERROR_MISSING_ALGORITHM);
					return false;
				} catch (MissingDataFileException e) {
					abort(ERROR_MISSING_DATAFILE + fn);
					return false;
				}
				hfNew.update(hash);
				filesProcessed += 1;
			}
			setProgress(100);
			
			publish("Computing validity" + Const.NEWLINE);
			boolean isValid = hf.validate(hfNew);
			
			if (isValid) {
				publish(VALID);
			} else {
				publish(INVALID);
			}
			
			return isValid;
		}
		
	}
	

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
		c.add(new JScrollPane(textArea), BorderLayout.CENTER);
		c.add(progressBar, BorderLayout.SOUTH);
		
		fc = new JFileChooser();
		fc.setDialogTitle("Select DIP directory");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.addActionListener(this);
		fc.showOpenDialog(this);
		
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        }
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
			File dir = fc.getSelectedFile();
			task = new Task(dir);
			task.addPropertyChangeListener(this);
			task.execute();
		} else if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
			textArea.append("No directory chosen." + Const.NEWLINE);
		}
	}

}
