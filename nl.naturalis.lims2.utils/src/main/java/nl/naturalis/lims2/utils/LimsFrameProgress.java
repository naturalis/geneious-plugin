/**
 * 
 */
package nl.naturalis.lims2.utils;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

/**
 * <table>
 * <tr>
 * <td>
 * Date: 24 august 2016</td>
 * </tr>
 * <tr>
 * <td>
 * Company: Naturalis Biodiversity Center</td>
 * </tr>
 * <tr>
 * <td>
 * City: Leiden</td>
 * </tr>
 * <tr>
 * <td>
 * Country: Netherlands</td>
 * </tr>
 * <tr>
 * <td>
 * Description:<br>
 * Class to show a dialog screen during the import process of CSV data
 * 
 * </td>
 * </tr>
 * </table>
 * 
 * @author Reinier.Kartowikromo
 *
 */
public class LimsFrameProgress {

	JLabel jlPercentage = new JLabel();
	JLabel jlFilename = new JLabel();
	JLabel jlMsgEmpty = new JLabel("", JLabel.CENTER);
	JLabel jlMsg = new JLabel("", JLabel.CENTER);
	JLabel jlMsg0 = new JLabel("", JLabel.CENTER);
	static JLabel jlbMsg1 = new JLabel("", JLabel.CENTER);
	JLabel jlbMsg2 = new JLabel("", JLabel.CENTER);

	final LimsProgressBar it = new LimsProgressBar();
	static final int MY_MINIMUM = 0;
	static final int MY_MAXIMUM = 100;
	JFrame frame = new JFrame("Reading records from files");

	/**
	 * Create the dialog screen
	 * */
	public void createProgressGUI() {
		frame.setLayout(new FlowLayout());
		frame.setSize(480, 250);
		frame.isAlwaysOnTop();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setContentPane(it);
		frame.getRootPane().setWindowDecorationStyle(
				JRootPane.INFORMATION_DIALOG);
		frame.getTitle();
		jlPercentage.setText("0%");
		jlPercentage.setHorizontalAlignment(jlPercentage.CENTER);
		jlPercentage.setVerticalAlignment(jlPercentage.CENTER);
		jlFilename.setText("\n");
		jlFilename.setPreferredSize(new Dimension(350, 50));
		jlFilename.setHorizontalTextPosition(jlFilename.CENTER);
		jlFilename.setVerticalAlignment(jlFilename.CENTER);
		jlMsg0.setHorizontalAlignment(jlMsg0.CENTER);
		jlMsg0.setVerticalAlignment(jlMsg0.CENTER);
		jlbMsg1.setHorizontalAlignment(jlbMsg1.CENTER);
		jlbMsg1.setVerticalAlignment(jlbMsg1.CENTER);
		jlbMsg2.setHorizontalAlignment(jlbMsg2.CENTER);
		jlbMsg2.setVerticalAlignment(jlbMsg2.CENTER);
		frame.add(jlPercentage);
		frame.add(jlFilename);
		frame.add(jlMsgEmpty);
		frame.add(jlMsg);
		frame.add(jlMsg0);
		frame.add(jlbMsg1);
		frame.add(jlbMsg2);
		frame.setVisible(true);
	}

	/**
	 * Show the Dilaog screen with a progressbar and messages
	 * 
	 * @param fileName
	 *            Set param filename value
	 * 
	 * */
	public void showProgress(final String fileName) {
		for (int i = MY_MINIMUM; i <= MY_MAXIMUM; i++) {
			final int percent = i;
			try {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						it.updateBar(percent);
						jlPercentage.setText(percent + "%");
						jlFilename.setText("\n" + fileName);
						jlMsgEmpty
								.setText("                                                  ");
						jlMsg.setText("Warning:                                               "
								+ "\n");
						jlMsg0.setText("Geneious is currently processing the selected file(s)."
								+ "\n");
						jlbMsg1.setText("Please wait for the import process to finish."
								+ "\n");
						jlbMsg2.setText("You should preferably not start another action or change folders.");
					}
				});
				java.lang.Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Hide the dialog screen
	 * */
	public void hideFrame() {
		frame.setVisible(false);
	}
}
