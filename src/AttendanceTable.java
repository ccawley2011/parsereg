import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttendanceTable {
    public ArrayList<String> moduleList = new ArrayList<String>();
    public ArrayList<Register> registers = new ArrayList<Register>();
    public HashMap<String, HashMap<String, AttendanceRow>> modules = new HashMap<String, HashMap<String, AttendanceRow>>();

    public void addRegister(Register register) {
        registers.add(register);

        if (!moduleList.contains(register.module)) {
            moduleList.add(register.module);
            modules.put(register.module, new HashMap<String, AttendanceRow>());
        }

        HashMap<String, AttendanceRow> students = modules.get(register.module);
        for (RegisterEntry entry : register.entries) {
            if (!students.containsKey(entry.studentNumber)) {
                students.put(entry.studentNumber, new AttendanceRow(entry.studentNumber, entry.studentName));
            }
            students.get(entry.studentNumber).lectures.put(register.startTime, entry);
        }
    }

    public void addRegister(File file) throws IOException, ParseException {
        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                String extension = subFile.getName().substring(subFile.getName().lastIndexOf('.') + 1);
                if (extension.equals("htm") || extension.equals("html")) {
                    addRegister(subFile);
                }
            }
        } else {
            addRegister(new Register(file));
        }
    }

    public void writeExcel(File output, boolean xssf) throws IOException {
        try (Workbook wb = WorkbookFactory.create(xssf)) {
            CellStyle headerStyle = wb.createCellStyle();
            Font font = wb.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            CellStyle dateStyle = wb.createCellStyle();
            dateStyle.cloneStyleFrom(headerStyle);
            dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));

            for (String module : moduleList) {
                Sheet sheet = wb.createSheet(module);

                Row header = sheet.createRow(0);
                int column = 0;
                Cell cell = header.createCell(column++);
                cell.setCellValue("Student ID");
                cell.setCellStyle(headerStyle);

                cell = header.createCell(column++);
                cell.setCellValue("Student Name");
                cell.setCellStyle(headerStyle);

                for (Register register : registers) {
                    if (register.module.equals(module)) {
                        cell = header.createCell(column++);
                        cell.setCellValue(register.startTime);
                        cell.setCellStyle(dateStyle);
                    }
                }

                int rowNumber = 1;
                HashMap<String, AttendanceRow> students = modules.get(module);
                for (Map.Entry me : students.entrySet()) {
                    AttendanceRow row = (AttendanceRow) me.getValue();
                    row.writeExcel(wb, sheet, sheet.createRow(rowNumber++), registers, module);
                }
            }

            try (FileOutputStream fileOut = new FileOutputStream(output)) {
                wb.write(fileOut);
            }
        }
    }

    public void writeCSV(File output) throws IOException {
        FileWriter outFile = new FileWriter(output);
        outFile.write("Student ID,Student Name");
        String module = moduleList.get(0);
        for (Register register : registers) {
            if (register.module.equals(module)) {
                outFile.write(",\"" + register.startTime + "\"");
            }
        }
        outFile.write('\n');

        HashMap<String, AttendanceRow> students = modules.get(module);
        for (Map.Entry me : students.entrySet()) {
            AttendanceRow row = (AttendanceRow) me.getValue();
            row.writeCSV(outFile, registers, module);
        }
        outFile.close();
    }

}
