import java.util.ArrayList;

class Register {
    private String id;
    String module;
    ArrayList<RegisterEntry> entries = new ArrayList<RegisterEntry>();

    String getID() {
        return id;
    }

    Register() {
        id = "";
    }

    Register(String _id) {
        id = _id;
    }
}
