import java.util.ArrayList;
import java.util.HashMap;

public class Module {
    ArrayList<Register> registers = new ArrayList<Register>();
    HashMap<String, AttendanceRow> students = new HashMap<String, AttendanceRow>();

    void addRegister(Register register) {
        registers.add(register);

        for (RegisterEntry entry : register.entries) {
            if (!students.containsKey(entry.studentNumber)) {
                students.put(entry.studentNumber, new AttendanceRow(entry.studentNumber, entry.studentName));
            }
            students.get(entry.studentNumber).lectures.put(register.getID(), entry);
        }
    }
}
