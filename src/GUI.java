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
    private AttendanceTable attendanceTable = new AttendanceTable(this);
    private JFrame frame;
    private JButton open, save;
    private JLabel label;
    public JTextArea console;

    public GUI(JFrame _frame) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        frame = _frame;

        console = new JTextArea(30, 40);
        console.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(console);
        add(scrollPane);

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

        debug("ParseReg v1.00\nReady for Input\n");
    }

    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "open":
                openFileDialog();
                break;
            case "save":
                saveFileDialog();
                break;
        }
    }

    public void debug(String message) {
        console.append(message + "\n");
    }

    public void message(String message) {
        console.append(message + "\n");
        JOptionPane.showMessageDialog(frame, message);
    }

    public void error(String message) {
        console.append(message + "\n");
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public class OpenThread extends Thread {
        private JFileChooser fc;

        public OpenThread(JFileChooser _fc) {
            fc = _fc;
        }

        public void run() {
            attendanceTable.clear();
            for (File input : fc.getSelectedFiles()) {
                try {
                    attendanceTable.addRegister(input);
                } catch (IOException | ParseException e) {
                    error("Failed to open file: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            if (attendanceTable.registers.isEmpty()) {
                debug("No registers were found.\n");
            } else {
                save.setEnabled(true);
                debug("Finished loading registers!\n");
            }

            open.setEnabled(true);
            setCursor(null);
        }
    }

    private void openFileDialog() {
        JFileChooser fc = new JFileChooser();
        fc.removeChoosableFileFilter(fc.getAcceptAllFileFilter());
        fc.addChoosableFileFilter(new FileNameExtensionFilter("HTML documents", "html", "htm"));
        fc.setMultiSelectionEnabled(true);
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        int returnVal = fc.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            open.setEnabled(false);
            save.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            (new OpenThread(fc)).start();
        }
    }

    public class SaveThread extends Thread {
        private GUI gui;
        private JFileChooser fc;
        private String module;

        public SaveThread(GUI _gui, JFileChooser _fc, String _module) {
            gui = _gui;
            fc = _fc;
            module = _module;
        }

        private void save(String extension, File output, String defaultModule) throws IOException {
            switch (extension) {
                case "xlsx":
                    attendanceTable.writeExcel(output, true, defaultModule);
                    break;
                case "xls":
                case "xlt":
                    attendanceTable.writeExcel(output, false, defaultModule);
                    break;
                case "csv":
                    attendanceTable.writeCSV(output, defaultModule);
                    break;
                default:
                    error("Unrecognized file extension: " + extension);
                    break;
            }

        }

        public void run() {
            FileNameExtensionFilter fileFilter = (FileNameExtensionFilter) fc.getFileFilter();
            if (fileFilter==null) {
                fileFilter = (FileNameExtensionFilter) fc.getChoosableFileFilters()[0];
            }
            String leafName = fc.getSelectedFile().getName();
            String extension = "";
            if (leafName.contains(".")) {
                extension = leafName.substring(leafName.lastIndexOf('.') + 1);
                leafName = leafName.substring(0, leafName.lastIndexOf('.'));
            }

            try {
                debug("Saving file " + fc.getSelectedFile() + "...");
                switch (extension) {
                    case "xlsx":
                    case "xls":
                    case "xlt":
                    case "csv":
                        save(extension, fc.getSelectedFile(), module);
                        break;
                    default:
                        File outFile = new File(fc.getSelectedFile().getParent(), leafName + "." + fileFilter.getExtensions()[0]);
                        save(fileFilter.getExtensions()[0], outFile, module);
                        break;
                }
                message("Exporting as a spreadsheet was successful.\n");
            } catch (IOException e) {
                error("Failed to create file: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                error("Failed to export spreadsheet: " + e.getMessage());
                e.printStackTrace();
            }

            open.setEnabled(true);
            save.setEnabled(true);
            setCursor(null);
        }
    }

    private void saveFileDialog() {
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
            open.setEnabled(false);
            save.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            (new SaveThread(this, fc, (String) moduleList.getSelectedItem())).start();
        }

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
