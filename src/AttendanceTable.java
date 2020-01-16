import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class AttendanceTable {
    HashMap<String, Module> modules = new HashMap<String, Module>();

    void addRegister(Register register) {
        if (!modules.containsKey(register.module)) {
            modules.put(register.module, new Module());
        }

        Module module = modules.get(register.module);
        module.addRegister(register);
    }

    ArrayList<String> listModules() {
        ArrayList<String> list = new ArrayList<String>();

        for (Map.Entry me : modules.entrySet()) {
            String name = (String) me.getKey();
            list.add(name);
        }

        return list;
    }

    void finish() {
        for (Map.Entry me : modules.entrySet()) {
            Module module = (Module) me.getValue();
            module.finish();
        }
    }

    void clear() {
        modules.clear();
    }
}
