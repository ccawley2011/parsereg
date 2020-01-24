import java.util.ArrayList;
import java.util.HashMap;

class Module {
    ArrayList<Register> registers = new ArrayList<>();
    HashMap<String, AttendanceRow> students = new HashMap<>();

    void addRegister(Register register) {
        registers.add(register);
    }

    void addRegisterEntry(String id, RegisterEntry entry) {
        for (Register register : registers) {
            if (register.getID().equals(id)) {
                register.entries.add(entry);
                break;
            }
        }
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
