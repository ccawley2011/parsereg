import org.apache.poi.ss.usermodel.*;

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

    public void writeCSV(FileWriter outFile, ArrayList<Register> registers, String module) throws IOException {
        outFile.write("\"" + studentNumber + "\",\"" + studentName + "\"");
        for (Register register : registers) {
            if (register.module.equals(module)) {
                if (lectures.containsKey(register.startTime)) {
                    outFile.write("," + lectures.get(register.startTime).getSignatureString(true));
                } else {
                    outFile.write(",UNKNOWN");
                }
            }
        }
        outFile.write('\n');
    }

    public void writeExcel(Workbook wb, Sheet sheet, Row row, ArrayList<Register> registers, String module) {
        CreationHelper creationHelper = wb.getCreationHelper();
        Drawing<?> patr = sheet.createDrawingPatriarch();
        int column = 0;

        ClientAnchor clientAnchor = creationHelper.createClientAnchor();
        clientAnchor.setCol1(4);
        clientAnchor.setRow1(2);
        clientAnchor.setCol2(7);
        clientAnchor.setRow2(7);

        Cell cell = row.createCell(column++);
        cell.setCellValue(studentNumber);

        cell = row.createCell(column++);
        cell.setCellValue(studentName);

        for (Register register : registers) {
            if (register.module.equals(module)) {
                cell = row.createCell(column++);
                if (lectures.containsKey(register.startTime)) {
                    RegisterEntry entry = lectures.get(register.startTime);
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
        }
    }

}
