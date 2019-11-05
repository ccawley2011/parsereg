import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class Main {

    // TODO: Move this into a separate class
    private static void writeExcelHeader(Workbook wb, Sheet sheet,  ArrayList<Register> registers) {
        // Create a row and put some cells in it. Rows are 0 based.
        Row row = sheet.createRow(0);

        CellStyle headerStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        int column = 0;
        Cell cell = row.createCell(column++);
        cell.setCellValue("Student ID");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(column++);
        cell.setCellValue("Student Name");
        cell.setCellStyle(headerStyle);

        headerStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
        for (Register register : registers) {
            cell = row.createCell(column++);
            cell.setCellValue(register.startTime);
            cell.setCellStyle(headerStyle);
        }
    }

    private static void writeExcelEntries(Sheet sheet,  ArrayList<Register> registers, HashMap<String, String> students, HashMap<String, HashMap<Date, RegisterEntry>> attendance) {
        int rowNumber = 1;

        for (Map.Entry me : attendance.entrySet()) {
            Row row = sheet.createRow(rowNumber++);
            int column = 0;

            String studentNumber = (String) me.getKey();
            Cell cell = row.createCell(column++);
            cell.setCellValue(studentNumber);

            cell = row.createCell(column++);
            cell.setCellValue(students.get(studentNumber));

            HashMap<Date, RegisterEntry> lectures = (HashMap<Date, RegisterEntry>) me.getValue();
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

    public static void main(String[] args) throws IOException, ParseException {
        if (args.length <= 0) {
            System.out.println("Invalid number of arguments");
            return;
        }

        ArrayList<Register> registers = new ArrayList<Register>();
        HashMap<String, String> students = new HashMap<String, String>();
        HashMap<String, HashMap<Date, RegisterEntry>> attendance = new HashMap<String, HashMap<Date, RegisterEntry>>();
        for (int i = 0; i < args.length; i++) {
            File input = new File(args[i]);
            Register register = new Register(input);
            registers.add(register);

            for (RegisterEntry entry : register.entries) {
                if (!students.containsKey(entry.studentNumber)) {
                    students.put(entry.studentNumber, entry.studentName);
                    attendance.put(entry.studentNumber, new HashMap<Date, RegisterEntry>());
                }
                attendance.get(entry.studentNumber).put(register.startTime, entry);
            }
        }

        FileWriter output = new FileWriter("output.csv");
        output.write("Student ID,Student Name");
        for (Register register : registers) {
            output.write(",\"" + register.startTime + "\"");
        }
        output.write('\n');

        for (Map.Entry me : attendance.entrySet()) {
            output.write("\"" + me.getKey() + "\",\"" + students.get(me.getKey())+"\"");
            HashMap<Date, RegisterEntry> lectures = (HashMap<Date, RegisterEntry>) me.getValue();
            for (Register register : registers) {
                if (lectures.containsKey(register.startTime)) {
                    output.write("," + lectures.get(register.startTime).getSignatureString(true));
                } else {
                    output.write(",UNKNOWN");
                }
            }
            output.write('\n');
        }
        output.close();

        try (Workbook wb = WorkbookFactory.create(false)) {
            Sheet sheet = wb.createSheet("new sheet");
            writeExcelHeader(wb, sheet, registers);
            writeExcelEntries(sheet, registers, students, attendance);


            // Write the output to a file
            try (FileOutputStream fileOut = new FileOutputStream("workbook.xls")) {
                wb.write(fileOut);
            }
        }
    }
}
