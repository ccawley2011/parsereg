import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

class ExcelSpreadsheetWriter extends SpreadsheetWriter {
    private GUI gui;
    private boolean xssf;

    ExcelSpreadsheetWriter(GUI _gui, boolean _xssf) {
        xssf = _xssf;
        gui = _gui;
    }

    private void writeAttendanceRow(AttendanceRow attendanceRow, Workbook wb, Sheet sheet, Row row, ArrayList<Register> registers) {
        CreationHelper creationHelper = wb.getCreationHelper();
        Drawing<?> patr = sheet.createDrawingPatriarch();
        int column = 0;

        ClientAnchor clientAnchor = creationHelper.createClientAnchor();
        clientAnchor.setCol1(4);
        clientAnchor.setRow1(2);
        clientAnchor.setCol2(7);
        clientAnchor.setRow2(7);

        Cell cell = row.createCell(column++);
        cell.setCellValue(attendanceRow.studentNumber);

        cell = row.createCell(column++);
        cell.setCellValue(attendanceRow.studentName);

        for (Register register : registers) {
            cell = row.createCell(column++);
            if (attendanceRow.lectures.containsKey(register.getID())) {
                RegisterEntry entry = attendanceRow.lectures.get(register.getID());
                cell.setCellValue(entry.getSignatureString(true));

                if (!entry.comments.isEmpty()) {
                    Comment comment = patr.createCellComment(clientAnchor);
                    comment.setString(creationHelper.createRichTextString(entry.comments));
                    cell.setCellComment(comment);
                }
            } else {
                cell.setCellValue("UNKNOWN");
            }
        }

        CellRangeAddress address = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 2, column - 1);
        Cell presentSum = row.createCell(column++);
        presentSum.setCellFormula("COUNTIF("+address.formatAsString()+",\"PRESENT\")");
        Cell notExpectedSum = row.createCell(column++);
        notExpectedSum.setCellFormula(presentSum.getAddress().formatAsString()+"+COUNTIF("+address.formatAsString()+",\"NOT EXPECTED\")");
    }

    private void writeModule(Workbook wb, Sheet sheet, Module module) {
        CellStyle headerStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        CellStyle dateStyle = wb.createCellStyle();
        dateStyle.cloneStyleFrom(headerStyle);
        dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));

        sheet.createFreezePane(2, 1);

        Row header = sheet.createRow(0);
        int column = 0;
        Cell cell = header.createCell(column++);
        cell.setCellValue("Student ID");
        cell.setCellStyle(headerStyle);

        cell = header.createCell(column++);
        cell.setCellValue("Student Name");
        cell.setCellStyle(headerStyle);

        for (Register register : module.registers) {
            cell = header.createCell(column++);
            cell.setCellValue(register.getID());
            cell.setCellStyle(dateStyle);
        }

        cell = header.createCell(column++);
        cell.setCellValue("Present");
        cell.setCellStyle(headerStyle);

        cell = header.createCell(column++);
        cell.setCellValue("Present or not expected");
        cell.setCellStyle(headerStyle);

        int rowNumber = 1;
        for (Map.Entry me : module.students.entrySet()) {
            AttendanceRow row = (AttendanceRow) me.getValue();
            writeAttendanceRow(row, wb, sheet, sheet.createRow(rowNumber++), module.registers);
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

    void writeAttendanceTable(AttendanceTable attendanceTable, File output, String defaultModule) throws IOException {
        try (Workbook wb = WorkbookFactory.create(xssf)) {
            for (Map.Entry me : attendanceTable.modules.entrySet()) {
                String name = (String) me.getKey();
                Module module = (Module) me.getValue();
                gui.debug("Exporting sheet " + name +  "...");

                Sheet sheet = wb.createSheet((String) me.getKey());
                if (name.equals(defaultModule)) {
                    wb.setActiveSheet(wb.getSheetIndex(sheet));
                    sheet.setSelected(true);
                } else {
                    sheet.setSelected(false);
                }

                writeModule(wb, sheet, module);
            }

            try (FileOutputStream fileOut = new FileOutputStream(output)) {
                wb.write(fileOut);
            }
        }
    }

}
