import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class AttendanceTable {
    ArrayList<String> moduleList = new ArrayList<String>();
    ArrayList<Register> registers = new ArrayList<Register>();
    HashMap<String, HashMap<String, AttendanceRow>> modules = new HashMap<String, HashMap<String, AttendanceRow>>();
    private GUI gui;

    AttendanceTable(GUI _gui) {
        gui = _gui;
    }

    void addRegister(Register register) {
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
            students.get(entry.studentNumber).lectures.put(register.getID(), entry);
        }
    }

    void addRegister(File file) throws IOException, ParseException {
        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                String extension = subFile.getName().substring(subFile.getName().lastIndexOf('.') + 1);
                if (subFile.isDirectory() || extension.equals("htm") || extension.equals("html")) {
                    addRegister(subFile);
                }
            }
        } else {
            gui.debug("Parsing register " + file.getName() +  "...");
            addRegister(new EVisionRegister(file));
        }
    }

    void clear() {
        modules.clear();
        moduleList.clear();
        registers.clear();
    }

    void writeExcel(File output, boolean xssf, String defaultModule) throws IOException {
        try (Workbook wb = WorkbookFactory.create(xssf)) {
            CellStyle headerStyle = wb.createCellStyle();
            Font font = wb.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            CellStyle dateStyle = wb.createCellStyle();
            dateStyle.cloneStyleFrom(headerStyle);
            dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));

            for (String module : moduleList) {
                gui.debug("Exporting sheet " + module +  "...");
                Sheet sheet = wb.createSheet(module);
                sheet.createFreezePane(2, 1);
                if (module.equals(defaultModule)) {
                    wb.setActiveSheet(wb.getSheetIndex(sheet));
                    sheet.setSelected(true);
                } else {
                    sheet.setSelected(false);
                }

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
                        cell.setCellValue(register.getID());
                        cell.setCellStyle(dateStyle);
                    }
                }

                cell = header.createCell(column++);
                cell.setCellValue("Present");
                cell.setCellStyle(headerStyle);

                cell = header.createCell(column++);
                cell.setCellValue("Present or not expected");
                cell.setCellStyle(headerStyle);

                int rowNumber = 1;
                HashMap<String, AttendanceRow> students = modules.get(module);
                for (Map.Entry me : students.entrySet()) {
                    AttendanceRow row = (AttendanceRow) me.getValue();
                    row.writeExcel(wb, sheet, sheet.createRow(rowNumber++), registers, module);
                }

                sheet.setAutoFilter(new CellRangeAddress(0, sheet.getLastRowNum(), 0, column - 1));

                SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();
                CellRangeAddress[] regions = {
                        new CellRangeAddress(1, sheet.getLastRowNum(), 2, column - 3)
                };

                ConditionalFormattingRule presentRule = sheetCF.createConditionalFormattingRule(ComparisonOperator.EQUAL, "\"PRESENT\"");
                PatternFormatting presentFill = presentRule.createPatternFormatting();
                presentFill.setFillBackgroundColor(IndexedColors.LIGHT_GREEN.index);
                presentFill.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
                sheetCF.addConditionalFormatting(regions, presentRule);

                ConditionalFormattingRule absentRule = sheetCF.createConditionalFormattingRule(ComparisonOperator.EQUAL, "\"ABSENT\"");
                PatternFormatting absentFill = absentRule.createPatternFormatting();
                absentFill.setFillBackgroundColor(IndexedColors.ROSE.index);
                absentFill.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
                sheetCF.addConditionalFormatting(regions, absentRule);

                ConditionalFormattingRule notExpectedRule = sheetCF.createConditionalFormattingRule(ComparisonOperator.EQUAL, "\"NOT EXPECTED\"");
                PatternFormatting notExpectedFill = notExpectedRule.createPatternFormatting();
                notExpectedFill.setFillBackgroundColor(IndexedColors.LIGHT_YELLOW.index);
                notExpectedFill.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
                sheetCF.addConditionalFormatting(regions, notExpectedRule);

                for (int x = 0; x < header.getPhysicalNumberOfCells(); x++) {
                    sheet.autoSizeColumn(x);
                    // autoSizeColumn doesn't calculate the column width correctly with certain fonts
                    // like Calibri, so adjust the column width manually.
                    sheet.setColumnWidth(x, sheet.getColumnWidth(x) + 200);
                }
            }

            try (FileOutputStream fileOut = new FileOutputStream(output)) {
                wb.write(fileOut);
            }
        }
    }

    void writeCSV(File output, String module) throws IOException {
        FileWriter outFile = new FileWriter(output);
        outFile.write("Student ID,Student Name");
        for (Register register : registers) {
            if (register.module.equals(module)) {
                outFile.write(",\"" + register.getID() + "\"");
            }
        }
        outFile.write(",Present,Present or not expected\n");

        HashMap<String, AttendanceRow> students = modules.get(module);
        for (Map.Entry me : students.entrySet()) {
            AttendanceRow row = (AttendanceRow) me.getValue();
            row.writeCSV(outFile, registers, module);
        }
        outFile.close();
    }

}
