import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

class CSVSpreadsheetWriter extends SpreadsheetWriter {
    private void writeAttendanceRow(AttendanceRow attendanceRow, FileWriter outFile, ArrayList<Register> registers) throws IOException {
        int present = 0, notExpected = 0;
        outFile.write("\"" + attendanceRow.studentNumber + "\",\"" + attendanceRow.studentName + "\"");
        for (Register register : registers) {
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
        outFile.write("," + present + "," + (present + notExpected) + '\n');
    }

    private void writeModule(FileWriter outFile, Module module) throws IOException {
        outFile.write("Student ID,Student Name");
        for (Register register : module.registers) {
            outFile.write(",\"" + register.getID() + "\"");
        }
        outFile.write(",Present,Present or not expected\n");

        for (Map.Entry me : module.students.entrySet()) {
            AttendanceRow row = (AttendanceRow) me.getValue();
            writeAttendanceRow(row, outFile, module.registers);
        }
    }

    void writeAttendanceTable(AttendanceTable attendanceTable, File output, String defaultModule) throws IOException {
        FileWriter outFile = new FileWriter(output);
        Module module = attendanceTable.modules.get(defaultModule);
        writeModule(outFile, module);
        outFile.close();
    }

}
