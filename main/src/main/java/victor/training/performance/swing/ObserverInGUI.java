package victor.training.performance.swing;

import victor.training.spring.batch.util.PerformanceUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class ObserverInGUI {
	public static void main(String[] args) {
		MyFrame myFrame = new MyFrame();
		
		myFrame.button1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Indexing...");
				PerformanceUtil.sleepMillis(1000);// Increase
				System.out.println("Done");
				Random r = new Random();
				myFrame.button1.setBackground(new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
//				JOptionPane.showMessageDialog(null, "Done"); // just in case
			}
		});
	}
}

