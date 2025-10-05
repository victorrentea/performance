package victor.training.performance.leak;

import victor.training.performance.leak.obj.Big100MB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class Leak16_GuiObserver {
  public static void main(String[] args) {
    JFrame mainFrame = new JFrame("Observer Leak");
    JButton button = new JButton("Open dialog");
    //region irrelevant code
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setSize(200, 100);
    mainFrame.setLocationRelativeTo(null); // means centered (in 90s style)
    mainFrame.setVisible(true);
    JPanel panel = new JPanel();
    panel.add(button);
    mainFrame.setContentPane(panel);
    mainFrame.revalidate();
    //endregion

    button.addActionListener(e -> {
      LeakyFrame leakyFrame = new LeakyFrame();

      // ☣️ main frame linked to the new (disposable) frame
      MouseAdapter listener = new MouseAdapter() {
        @Override
        public void mouseMoved(MouseEvent e) {
          leakyFrame.changeColor();
        }
      };
      mainFrame.addMouseMotionListener(listener);

      //region solution
      //      leakyFrame.addWindowListener(new WindowAdapter() {
//        @Override
//        public void windowClosed(WindowEvent e) {
//          mainFrame.removeMouseMotionListener(listener);
//        }
//      });
      //endregion
    });
  }

  static class LeakyFrame extends JFrame {
    Big100MB big = new Big100MB(); // panel state [brutal]
    JPanel panel = new JPanel();
    Random random = new Random();
    public LeakyFrame() {
      setTitle("Leaky Frame");
      setContentPane(panel);
      revalidate();
      setSize(200, 200);
      Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
      int x = (int) (Math.random() * (screen.width - getWidth()));
      int y = (int) (Math.random() * (screen.height - getHeight()));
      setLocation(x, y);
      setVisible(true);
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void changeColor() {
      panel.setBackground(randomColor(random));
    }
    private static Color randomColor(Random random) {
      return new Color(
          random.nextInt(256),
          random.nextInt(256),
          random.nextInt(256)
      );
    }
  }
}