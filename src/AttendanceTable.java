import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class AttendanceTable {
    HashMap<String, Module> modules = new HashMap<String, Module>();
    private GUI gui;

    AttendanceTable(GUI _gui) {
        gui = _gui;
    }

    void addRegister(Register register) {
        if (!modules.containsKey(register.module)) {
            modules.put(register.module, new Module());
        }

        Module module = modules.get(register.module);
        module.addRegister(register);
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

    ArrayList<String> listModules() {
        ArrayList<String> list = new ArrayList<String>();

        for (Map.Entry me : modules.entrySet()) {
            String name = (String) me.getKey();
            list.add(name);
        }

        return list;
    }

    void clear() {
        modules.clear();
    }
}
