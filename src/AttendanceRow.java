import java.util.HashMap;

class AttendanceRow {
    String studentNumber;
    String studentName;
    HashMap<String, RegisterEntry> lectures = new HashMap<String, RegisterEntry>();

    AttendanceRow(String _studentNumber, String _studentName) {
        studentNumber = _studentNumber;
        studentName = _studentName;
    }
}
