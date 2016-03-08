/**
 * 
 */
package nl.naturalis.lims2.utils;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsFrameProgress {

	JLabel jl = new JLabel();
	final LimsProgressBar it = new LimsProgressBar();
	static final int MY_MINIMUM = 0;
	static final int MY_MAXIMUM = 100;
	JFrame frame = new JFrame("Reading records from files");

	public void createProgressBar() {
		// JFrame frame = new JFrame("Reading records from files");
		frame.getTitle();
		frame.setSize(280, 75);
		frame.isAlwaysOnTop();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setContentPane(it);
		jl.setText("0%");
		frame.add(BorderLayout.CENTER, jl);
		frame.setVisible(true);
	}

	public void showProgress() {
		for (int i = MY_MINIMUM; i <= MY_MAXIMUM; i++) {
			final int percent = i;
			try {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						it.updateBar(percent);
						jl.setText(percent + "%");
					}
				});
				java.lang.Thread.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void hideFrame() {
		frame.setVisible(false);
	}
}
