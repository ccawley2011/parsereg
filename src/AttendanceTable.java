import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

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
}
