/**
 * 
 */
package nl.naturalis.lims2.utils;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsFrameProgress {

	JLabel jlPercentage = new JLabel();
	JLabel jlFilename = new JLabel();
	JLabel jlMsgEmpty = new JLabel("", JLabel.CENTER);
	JLabel jlMsg = new JLabel("", JLabel.CENTER);
	JLabel jlMsg0 = new JLabel("", JLabel.CENTER);
	JLabel jlbMsg1 = new JLabel("", JLabel.CENTER);
	JLabel jlbMsg2 = new JLabel("", JLabel.CENTER);

	final LimsProgressBar it = new LimsProgressBar();
	static final int MY_MINIMUM = 0;
	static final int MY_MAXIMUM = 100;
	JFrame frame = new JFrame("Reading records from files");

	public void createProgressGUI() {
		frame.isAlwaysOnTop();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setContentPane(it);
		frame.getRootPane().setWindowDecorationStyle(
				JRootPane.INFORMATION_DIALOG);
		frame.getTitle();
		jlPercentage.setText("0%");
		jlFilename.setText("");
		frame.add(BorderLayout.CENTER, jlPercentage);
		frame.add(BorderLayout.CENTER, jlFilename);
		frame.add(BorderLayout.CENTER, jlMsgEmpty);
		frame.add(BorderLayout.CENTER, jlMsg);
		frame.add(BorderLayout.CENTER, jlMsg0);
		frame.add(BorderLayout.CENTER, jlbMsg1);
		frame.add(BorderLayout.CENTER, jlbMsg2);
		frame.setBounds(0, 0, 375, 160);
		frame.setVisible(true);
	}

	public void showProgress(final String fileName) {
		for (int i = MY_MINIMUM; i <= MY_MAXIMUM; i++) {
			final int percent = i;
			try {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						it.updateBar(percent);
						jlPercentage.setText(percent + "%");
						jlFilename.setText(fileName);
						jlMsgEmpty
								.setText("                                                  ");
						jlMsg.setText("Warning:                                               ");
						jlMsg0.setText("Geneious is currently processing the selected file(s).");
						jlbMsg1.setText("Please wait for the import process to finish.");
						jlbMsg2.setText("You should preferably not start another action or change maps.");
					}
				});
				java.lang.Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void hideFrame() {
		frame.setVisible(false);
	}
}
