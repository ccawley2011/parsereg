import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.text.ParseException;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        if (args.length <= 0) {
            System.out.println("Invalid number of arguments");
            return;
        }

        AttendanceTable attendanceTable = new AttendanceTable();
        for (int i = 0; i < args.length; i++) {
            File input = new File(args[i]);
            attendanceTable.addRegister(new Register(input));
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
