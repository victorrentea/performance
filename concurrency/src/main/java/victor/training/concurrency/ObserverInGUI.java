package victor.training.concurrency;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ObserverInGUI {
	public static void main(String[] args) {
		MyFrame myFrame = new MyFrame();
		
		// SOLUTION(
		myFrame.button1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Button clicked");
			}
		});
		
		SwingUtilities.invokeLater( () -> 
		myFrame.button1.setBackground(Color.red));
		// SOLUTION)
		//TODO myFrame.button1.addActionListener(new ActionListener);
	}
}