package nl.naturalis.lims2.utils;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

public class ShowWaitAction extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4255410022542016951L;

	public ShowWaitAction(String name) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 350, 400);
		setTitle("In Progress");
		getContentPane().setLayout(null);
		new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							UIManager.setLookAndFeel(UIManager
									.getSystemLookAndFeelClassName());
						} catch (Exception ex) {

						}
						new BackgroundWorker().execute();
					}
				});

			}
		};
	}

	public class BackgroundWorker extends SwingWorker<Void, Void> {

		private JProgressBar pb;
		private JDialog dialog;

		public BackgroundWorker() {
			addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if ("progress".equalsIgnoreCase(evt.getPropertyName())) {
						if (dialog == null) {
							dialog = new JDialog();
							dialog.setTitle("Processing");
							dialog.setLayout(new GridBagLayout());
							dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
							GridBagConstraints gbc = new GridBagConstraints();
							gbc.insets = new Insets(2, 2, 2, 2);
							gbc.weightx = 1;
							gbc.gridy = 0;
							dialog.add(new JLabel("Processing..."), gbc);
							pb = new JProgressBar();
							pb.setStringPainted(true);
							gbc.gridy = 1;
							dialog.add(pb, gbc);
							dialog.pack();
							dialog.setLocationRelativeTo(null);
							dialog.setModal(true);
							JDialog.setDefaultLookAndFeelDecorated(true);
							dialog.setVisible(true);
						}
						pb.setValue(getProgress());
					}
				}

			});
		}

		@Override
		protected void done() {
			if (dialog != null) {
				dialog.dispose();
			}
		}

		@Override
		protected Void doInBackground() throws Exception {
			for (int index = 0; index < 100; index++) {
				setProgress(index);
				Thread.sleep(100);
				/**
				 * Do work Do work Do work
				 */
			}
			return null;
		}
	}
}
