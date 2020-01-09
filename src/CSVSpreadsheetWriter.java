import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CSVSpreadsheetWriter extends SpreadsheetWriter {
    private GUI gui;

    CSVSpreadsheetWriter(GUI _gui) {
        gui = _gui;
    }

    void writeAttendanceRow(AttendanceRow attendanceRow, FileWriter outFile, ArrayList<Register> registers, String module) throws IOException {
        int present = 0, notExpected = 0;
        outFile.write("\"" + attendanceRow.studentNumber + "\",\"" + attendanceRow.studentName + "\"");
        for (Register register : registers) {
            if (register.module.equals(module)) {
                if (attendanceRow.lectures.containsKey(register.getID())) {
                    RegisterEntry entry = attendanceRow.lectures.get(register.getID());
                    outFile.write("," + entry.getSignatureString(true));

                    if (entry.signature == RegisterEntry.Signature.PRESENT) {
                        present += 1;
                    } else if (!entry.comments.isEmpty()) {
                        notExpected += 1;
                    }
                } else {
                    outFile.write(",UNKNOWN");
                }
            }
        }
        outFile.write("," + present + "," + (present + notExpected) + '\n');
    }

    void writeAttendanceTable(AttendanceTable attendanceTable, File output, String defaultModule) throws IOException {
        FileWriter outFile = new FileWriter(output);
        outFile.write("Student ID,Student Name");
        for (Register register : attendanceTable.registers) {
            if (register.module.equals(defaultModule)) {
                outFile.write(",\"" + register.getID() + "\"");
            }
        }
        outFile.write(",Present,Present or not expected\n");

        HashMap<String, AttendanceRow> students = attendanceTable.modules.get(defaultModule);
        for (Map.Entry me : students.entrySet()) {
            AttendanceRow row = (AttendanceRow) me.getValue();
            writeAttendanceRow(row, outFile, attendanceTable.registers, defaultModule);
        }
        outFile.close();
    }

}
