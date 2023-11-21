package victor.training.performance.swing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.function.Consumer;

public class ContributorsUI extends JFrame {
   private static final Insets INSETS = new Insets(3, 10, 3, 10);
   private static final String[] COLUMNS = new String[]{"Login", "Contributions"};
   private final JTextField username = new JTextField(20);
   private final JPasswordField password = new JPasswordField(20);
   private final JTextField org = new JTextField(20);
   private final JComboBox<Variant> variant = new JComboBox<Variant>(Variant.values());
   private final JButton load = new JButton("Load contributors");
   private final JButton cancel = new JButton("Cancel");
   private final DefaultTableModel resultsModel = new DefaultTableModel(COLUMNS, 0);
   private final JTable results = new JTable(resultsModel);
   private final JScrollPane resultsScroll = new JScrollPane(results);
   private final ImageIcon loadingIcon = new ImageIcon(ContributorsUI.class.getClassLoader().getResource("ajax-loader.gif"));
   private final JLabel loadingStatus = new JLabel("Start new loading", loadingIcon, SwingConstants.CENTER);

   {
      cancel.setEnabled(false);
   }

   {
      resultsScroll.setPreferredSize(new Dimension(200, 200));
   }
   public ContributorsUI() {
      super("GitHub Contributors");
      JPanel content = new JPanel(new GridBagLayout());
      rootPane.setContentPane(content);

      addLabeled(content, "GitHub Username", username);
      addLabeled(content, "Password/Token", password);

      //      content.addWideSeparator()
//      content.addLabeled("Organization", org)
//      content.addLabeled("Variant", variant)
//      content.addWideSeparator()
//      content.addWide(JPanel().apply {
//         add(load)
//         add(cancel)
//      })
//      cotent.addWide(resultsScroll) {
//         weightx = 1.0
//         weighty = 1.0
//         fill = GridBagConstraints.BOTH
//      }
//      addWide(loadingStatus)
//      }
      // Initialize actions
//      init()

   }


   public static void addLabeled(JPanel panel, String label, JComponent component) {
      GridBagConstraints panelContstraints = new GridBagConstraints();
      panelContstraints.gridx = 0;
      panelContstraints.insets = INSETS;
      panel.add(new JLabel(label), panelContstraints);

      GridBagConstraints componentConstraints = new GridBagConstraints();
      componentConstraints.gridx = 1;
      componentConstraints.insets = INSETS;
      componentConstraints.anchor = GridBagConstraints.WEST;
      componentConstraints.fill = GridBagConstraints.HORIZONTAL;
      componentConstraints.weightx = 1.0;
      panel.add(component, componentConstraints);
   }

   public static void addWide(JPanel panel, JComponent component, Consumer<GridBagConstraints> constraintSetter) {
      GridBagConstraints constraints = new GridBagConstraints();
      constraints.gridx = 0;
      constraints.gridwidth = 2;
      constraints.insets = INSETS;
      if (constraintSetter!=null) {
         constraintSetter.accept(constraints);
      }
      panel.add(component, constraints);
   }

   public static void addWideSeparator(JPanel panel) {
      JSeparator separator = new JSeparator();
      addWide(panel, separator, constraints -> {
         constraints.fill = GridBagConstraints.HORIZONTAL;
      });
   }

}