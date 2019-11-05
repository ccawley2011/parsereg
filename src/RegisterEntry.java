import java.util.logging.SimpleFormatter;

public class RegisterEntry {
    public String studentNumber;
    public String studentName;
    public Signature signature;
    public String comments;

    enum Signature {
        UNKNOWN,
        PRESENT,
        ABSENT,
        NOT_EXPECTED
    }

    public String getSignatureString() {
        if (signature == Signature.PRESENT) {
            return "PRESENT";
        } else if (signature == Signature.ABSENT) {
            return "ABSENT";
        } else if (signature == Signature.NOT_EXPECTED) {
            return "NOT EXPECTED";
        } else {
            return "UNKNOWN";
        }
    }

    public void setSignatureFromString(String string) {
        if (string.contains("PRESENT")) {
            signature = Signature.PRESENT;
        } else if (string.contains("ABSENT")) {
            signature = Signature.ABSENT;
        } else {
            signature = Signature.UNKNOWN;
        }
    }
}
