import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AttendanceRow {
    public String studentNumber;
    public String studentName;
    public HashMap<Date, RegisterEntry> lectures = new HashMap<Date, RegisterEntry>();

    public AttendanceRow(String _studentNumber, String _studentName) {
        studentNumber = _studentNumber;
        studentName = _studentName;
    }

    public void writeCSV(FileWriter outFile, ArrayList<Register> registers) throws IOException {
        outFile.write("\"" + studentNumber + "\",\"" + studentName + "\"");
        for (Register register : registers) {
            if (lectures.containsKey(register.startTime)) {
                outFile.write("," + lectures.get(register.startTime).getSignatureString(true));
            } else {
                outFile.write(",UNKNOWN");
            }
        }
        outFile.write('\n');
    }

    public void writeExcel(Row row, ArrayList<Register> registers) {
        int column = 0;

        Cell cell = row.createCell(column++);
        cell.setCellValue(studentNumber);

        cell = row.createCell(column++);
        cell.setCellValue(studentName);

        for (Register register : registers) {
            cell = row.createCell(column++);
            if (lectures.containsKey(register.startTime)) {
                cell.setCellValue(lectures.get(register.startTime).getSignatureString(true));
            } else {
                cell.setCellValue("UNKNOWN");
            }
        }
    }

}
