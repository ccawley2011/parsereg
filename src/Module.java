import java.util.ArrayList;
import java.util.HashMap;

class Module {
    ArrayList<Register> registers = new ArrayList<Register>();
    HashMap<String, AttendanceRow> students = new HashMap<String, AttendanceRow>();

    void addRegister(Register register) {
        registers.add(register);
    }

    void finish() {
        for (Register register : registers) {
            for (RegisterEntry entry : register.entries) {
                if (!students.containsKey(entry.studentNumber)) {
                    students.put(entry.studentNumber, new AttendanceRow(entry.studentNumber, entry.studentName));
                }
                students.get(entry.studentNumber).lectures.put(register.getID(), entry);
            }
        }
    }
}
