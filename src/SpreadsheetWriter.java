import java.io.File;
import java.io.IOException;

abstract class SpreadsheetWriter {
    abstract void writeAttendanceTable(AttendanceTable attendanceTable, File output, String defaultModule) throws IOException;
}
