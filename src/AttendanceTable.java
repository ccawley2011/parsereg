import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttendanceTable {
    public ArrayList<Register> registers = new ArrayList<Register>();
    public HashMap<String, AttendanceRow> students = new HashMap<String, AttendanceRow>();

    public void addRegister(Register register) {
        registers.add(register);

        for (RegisterEntry entry : register.entries) {
            if (!students.containsKey(entry.studentNumber)) {
                students.put(entry.studentNumber, new AttendanceRow(entry.studentNumber, entry.studentName));
            }
            students.get(entry.studentNumber).lectures.put(register.startTime, entry);
        }
    }

    public void writeExcel(File output, boolean xssf) throws IOException {
        try (Workbook wb = WorkbookFactory.create(xssf)) {
            Sheet sheet = wb.createSheet("new sheet");

            CellStyle headerStyle = wb.createCellStyle();
             Font font = wb.createFont();
             font.setBold(true);
             headerStyle.setFont(font);

            // Create a row and put some cells in it. Rows are 0 based.
            Row header = sheet.createRow(0);
            int column = 0;
            Cell cell = header.createCell(column++);
            cell.setCellValue("Student ID");
            cell.setCellStyle(headerStyle);

            cell = header.createCell(column++);
            cell.setCellValue("Student Name");
            cell.setCellStyle(headerStyle);

            headerStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
            for (Register register : registers) {
                cell = header.createCell(column++);
                cell.setCellValue(register.startTime);
                cell.setCellStyle(headerStyle);
            }

            int rowNumber = 1;
            for (Map.Entry me : students.entrySet()) {
                AttendanceRow row = (AttendanceRow) me.getValue();
                row.writeExcel(sheet.createRow(rowNumber++), registers);
            }

            // Write the output to a file
            try (FileOutputStream fileOut = new FileOutputStream(output)) {
                wb.write(fileOut);
            }
        }
    }

    public void writeCSV(File output) throws IOException {
        FileWriter outFile = new FileWriter(output);
        outFile.write("Student ID,Student Name");
        for (Register register : registers) {
            outFile.write(",\"" + register.startTime + "\"");
        }
        outFile.write('\n');

        for (Map.Entry me : students.entrySet()) {
            AttendanceRow row = (AttendanceRow) me.getValue();
            row.writeCSV(outFile, registers);
        }
        outFile.close();
    }

}
