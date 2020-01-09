import java.util.ArrayList;

abstract class Register {
    String module;
    ArrayList<RegisterEntry> entries;

    abstract String getID();
}
