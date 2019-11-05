import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.text.ParseException;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        AttendanceTable attendanceTable = new AttendanceTable();
        if (args.length <= 0) {
            JFileChooser fc = new JFileChooser();
            fc.removeChoosableFileFilter(fc.getAcceptAllFileFilter());
            fc.addChoosableFileFilter(new FileNameExtensionFilter("HTML documents", "html", "htm"));
            fc.setMultiSelectionEnabled(true);
            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

            int returnVal = fc.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                for (File input : fc.getSelectedFiles()) {
                    attendanceTable.addRegister(input);
                }
            } else {
                return;
            }
        } else {
            for (String arg : args) {
                File input = new File(arg);
                attendanceTable.addRegister(input);
            }
        }

        JFileChooser fc = new JFileChooser();
        fc.removeChoosableFileFilter(fc.getAcceptAllFileFilter());
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Excel spreadsheet", "xlsx"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Excel 97-2003 spreadsheet", "xls", "xlt"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Comma Separated Values file", "csv"));

        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String extension = fc.getSelectedFile().getName().substring(fc.getSelectedFile().getName().lastIndexOf('.') + 1);
            switch (extension) {
                case "xlsx":
                    attendanceTable.writeExcel(fc.getSelectedFile(), true);
                    break;
                case "xls":
                case "xlt":
                    attendanceTable.writeExcel(fc.getSelectedFile(), false);
                    break;
                case "csv":
                    attendanceTable.writeCSV(fc.getSelectedFile());
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Unrecognized file extension: " + extension, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
            }

            JOptionPane.showMessageDialog(null, "Exporting as a spreadsheet was successful.");
        }
    }
}
