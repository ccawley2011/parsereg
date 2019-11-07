import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class GUI extends JPanel implements ActionListener {
    private AttendanceTable attendanceTable = new AttendanceTable();
    private JFrame frame;
    private JButton open, save;
    private JLabel label;

    public GUI(JFrame _frame) {
        frame = _frame;

        JPanel controlPane = new JPanel();
        open = new JButton("Open");
        open.setVerticalTextPosition(AbstractButton.CENTER);
        open.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
        open.setMnemonic(KeyEvent.VK_O);
        open.setActionCommand("open");
        open.addActionListener(this);
        controlPane.add(open);

        JLabel label = new JLabel("ParseReg");
        label.setVerticalTextPosition(AbstractButton.BOTTOM);
        label.setHorizontalTextPosition(AbstractButton.CENTER);
        controlPane.add(label);

        save = new JButton("Save");
        //Use the default text position of CENTER, TRAILING (RIGHT).
        save.setMnemonic(KeyEvent.VK_S);
        save.setActionCommand("save");
        save.setEnabled(false);
        save.addActionListener(this);
        controlPane.add(save);

        add(controlPane);
    }

    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "open":
                if (openFileDialog()) {
                    save.setEnabled(true);
                }
                break;
            case "save":
                saveFileDialog();
                break;
        }
    }

    private boolean openFileDialog() {
        JFileChooser fc = new JFileChooser();
        fc.removeChoosableFileFilter(fc.getAcceptAllFileFilter());
        fc.addChoosableFileFilter(new FileNameExtensionFilter("HTML documents", "html", "htm"));
        fc.setMultiSelectionEnabled(true);
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        int returnVal = fc.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            attendanceTable.clear();
            for (File input : fc.getSelectedFiles()) {
                try {
                    attendanceTable.addRegister(input);
                } catch (IOException | ParseException e) {
                    JOptionPane.showMessageDialog(frame, "Failed to open file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean saveFileDialog() {
        JFileChooser fc = new JFileChooser();
        fc.removeChoosableFileFilter(fc.getAcceptAllFileFilter());
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Excel spreadsheet", "xlsx"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Excel 97-2003 spreadsheet", "xls", "xlt"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Comma Separated Values file", "csv"));

        JPanel accessory = new JPanel();
        accessory.setLayout(new FlowLayout());
        accessory.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        fc.setAccessory(accessory);

        JLabel label = new JLabel("Default module:");
        accessory.add(label);

        String[] moduleString = attendanceTable.moduleList.toArray(new String[attendanceTable.modules.size()]);
        JComboBox<String> moduleList = new JComboBox<String>(moduleString);
        accessory.add(moduleList);

        int returnVal = fc.showSaveDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                String extension = fc.getSelectedFile().getName().substring(fc.getSelectedFile().getName().lastIndexOf('.') + 1);
                switch (extension) {
                    case "xlsx":
                        attendanceTable.writeExcel(fc.getSelectedFile(), true, (String) moduleList.getSelectedItem());
                        break;
                    case "xls":
                    case "xlt":
                        attendanceTable.writeExcel(fc.getSelectedFile(), false, (String) moduleList.getSelectedItem());
                        break;
                    case "csv":
                        attendanceTable.writeCSV(fc.getSelectedFile(), (String) moduleList.getSelectedItem());
                        break;
                    default:
                        JOptionPane.showMessageDialog(frame, "Unrecognized file extension: " + extension, "Error", JOptionPane.ERROR_MESSAGE);
                        return false;
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Failed to create file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Failed to export spreadsheet: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                return false;
            }

            JOptionPane.showMessageDialog(frame, "Exporting as a spreadsheet was successful.");
            return true;
        }

        return false;
    }

    public static JFrame createAndShowGUI() {
        JFrame frame = new JFrame("ParseReg");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GUI newContentPane = new GUI(frame);
        newContentPane.setOpaque(true);
        frame.setContentPane(newContentPane);

        frame.pack();
        frame.setVisible(true);

        return frame;
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
