//package victor.training.performance.swing;
//
//import victor.training.performance.ConcurrencyUtil;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.util.Random;
//
//public class ObserverInGUI {
//	public static void main(String[] args) {
//		MyFrame myFrame = new MyFrame();
//
//		myFrame.button1.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// UI Thread
//				System.out.println("Indexing...");
//				{ // in backround thread
//					ConcurrencyUtil.sleepq(5000);// Increase
//					System.out.println("Done");
//					Random r = new Random();
//					{ // pe UI Thread
//						myFrame.button1.setBackground(new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
//					}
////				JOptionPane.showMessageDialog(null, "Done"); // just in case}
//			}
//		});
//	}
//}
//
